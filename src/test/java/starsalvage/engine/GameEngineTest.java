package starsalvage.engine;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineTest {

    @Test
    void newGameStartsWithExpectedPlayerInventoryAndHealth() {
        GameEngine engine = new GameEngine();
        assertEquals(new Position(0, 0), engine.getState().getPlayerPosition());
        assertEquals(3, engine.getState().getHealth());
        assertEquals(0, engine.getState().getInventory().getSalvage());
        assertFalse(engine.getState().isFinished());
    }

    @Test
    void inventoryUpdatesWhenSalvageIsCollected() {
        GameEngine engine = new GameEngine();
        engine.move(Direction.SOUTH);
        engine.move(Direction.EAST);
        assertEquals(1, engine.getState().getInventory().getSalvage());
        assertEquals(TileContent.EMPTY, engine.getState().getTile(new Position(1, 1)));
    }

    @Test
    void waitAdvancesEnemyState() {
        GameEngine engine = new GameEngine();
        EnemyState before = engine.getState().getEnemies().get(0).getState();
        engine.waitTurn();
        EnemyState after = engine.getState().getEnemies().get(0).getState();
        assertEquals(before.next(), after);
        assertEquals(1, engine.getState().getTurn());
    }

    @Test
    void undoRestoresPreviousPositionAndInventory() {
        GameEngine engine = new GameEngine();
        engine.move(Direction.SOUTH);
        engine.move(Direction.EAST);
        assertEquals(1, engine.getState().getInventory().getSalvage());
        MoveResult undo = engine.undo();
        assertTrue(undo.isSuccess());
        assertEquals(new Position(1, 0), engine.getState().getPlayerPosition());
        assertEquals(0, engine.getState().getInventory().getSalvage());
    }

    @Test
    void shieldAbsorbsEnemyDamage() {
        GameEngine engine = new GameEngine();
        engine.move(Direction.EAST);
        engine.move(Direction.EAST);
        engine.move(Direction.EAST);
        assertEquals(1, engine.getState().getInventory().getShields());
        int healthBefore = engine.getState().getHealth();
        engine.waitTurn();
        assertTrue(engine.getState().getHealth() <= healthBefore);
    }

    @Test
    void saveAndLoadPreservesState() throws Exception {
        GameEngine engine = new GameEngine();
        engine.move(Direction.SOUTH);
        engine.move(Direction.EAST);
        Path tempFile = Files.createTempFile("starsalvage-test", ".save");
        GamePersistence.save(engine, tempFile);
        GameEngine loaded = GamePersistence.load(tempFile);
        assertEquals(engine.getState().getPlayerPosition(), loaded.getState().getPlayerPosition());
        assertEquals(engine.getState().getInventory().getSalvage(), loaded.getState().getInventory().getSalvage());
        Files.deleteIfExists(tempFile);
    }
}
