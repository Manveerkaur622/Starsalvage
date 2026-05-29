package starsalvage.engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_ROWS = 5;
    public static final int DEFAULT_COLUMNS = 5;
    public static final int TOTAL_SALVAGE = 3;

    private final int rows;
    private final int columns;
    private final Map<Position, TileContent> tiles;
    private final List<Enemy> enemies;
    private final Inventory inventory;
    private Position playerPosition;
    private int health;
    private int turn;
    private boolean won;
    private boolean finished;
    private String lastMessage;

    public GameState() {
        this.rows = DEFAULT_ROWS;
        this.columns = DEFAULT_COLUMNS;
        this.tiles = new HashMap<>();
        this.enemies = new ArrayList<>();
        this.inventory = new Inventory();
        this.playerPosition = new Position(0, 0);
        this.health = 3;
        this.turn = 0;
        this.won = false;
        this.finished = false;
        this.lastMessage = "Command the salvage drone and recover all crates.";
        initialiseBoard();
    }

    public GameState(GameState other) {
        this.rows = other.rows;
        this.columns = other.columns;
        this.tiles = new HashMap<>(other.tiles);
        this.enemies = new ArrayList<>();
        for (Enemy enemy : other.enemies) {
            this.enemies.add(new Enemy(enemy));
        }
        this.inventory = new Inventory(other.inventory);
        this.playerPosition = other.playerPosition;
        this.health = other.health;
        this.turn = other.turn;
        this.won = other.won;
        this.finished = other.finished;
        this.lastMessage = other.lastMessage;
    }

    private void initialiseBoard() {
        tiles.put(new Position(1, 1), TileContent.SALVAGE);
        tiles.put(new Position(2, 3), TileContent.SALVAGE);
        tiles.put(new Position(3, 0), TileContent.SALVAGE);
        tiles.put(new Position(0, 3), TileContent.SHIELD);
        tiles.put(new Position(4, 1), TileContent.MEDKIT);
        tiles.put(new Position(4, 4), TileContent.EXIT);
        enemies.add(new Enemy("Drone-7", new Position(1, 3), EnemyState.DORMANT));
        enemies.add(new Enemy("Hunter-2", new Position(3, 2), EnemyState.ALERT));
    }

    public boolean isInside(Position position) {
        return position.getRow() >= 0 && position.getRow() < rows
                && position.getColumn() >= 0 && position.getColumn() < columns;
    }

    public TileContent getTile(Position position) {
        return tiles.getOrDefault(position, TileContent.EMPTY);
    }

    public void setTile(Position position, TileContent content) {
        if (content == TileContent.EMPTY) {
            tiles.remove(position);
        } else {
            tiles.put(position, content);
        }
    }

    public Map<Position, TileContent> getTiles() {
        return tiles;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Position getPlayerPosition() {
        return playerPosition;
    }

    public void setPlayerPosition(Position playerPosition) {
        this.playerPosition = playerPosition;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getTurn() {
        return turn;
    }

    public void incrementTurn() {
        turn++;
    }

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won) {
        this.won = won;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }
}
