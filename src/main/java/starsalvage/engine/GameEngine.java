package starsalvage.engine;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

public class GameEngine implements Serializable {
    private static final long serialVersionUID = 1L;

    private GameState state;
    private final Deque<GameState> undoStack;

    public GameEngine() {
        this.state = new GameState();
        this.undoStack = new ArrayDeque<>();
    }

    public MoveResult move(Direction direction) {
        if (direction == null) {
            return new MoveResult(false, "Unknown direction.");
        }
        if (state.isFinished()) {
            return new MoveResult(false, "The game has already finished. Start a new game to play again.");
        }
        Position target = state.getPlayerPosition().move(direction);
        if (!state.isInside(target)) {
            return new MoveResult(false, "You cannot move outside the ship grid.");
        }
        saveUndoSnapshot();
        state.setPlayerPosition(target);
        StringBuilder message = new StringBuilder("Moved to " + target + ".");
        collectCurrentTile(message);
        takeTurn(message);
        state.setLastMessage(message.toString());
        return new MoveResult(true, state.getLastMessage());
    }

    public MoveResult waitTurn() {
        if (state.isFinished()) {
            return new MoveResult(false, "The game has already finished. Start a new game to play again.");
        }
        saveUndoSnapshot();
        StringBuilder message = new StringBuilder("Waited and scanned the corridor.");
        takeTurn(message);
        state.setLastMessage(message.toString());
        return new MoveResult(true, state.getLastMessage());
    }

    public MoveResult undo() {
        if (undoStack.isEmpty()) {
            return new MoveResult(false, "Nothing to undo.");
        }
        state = undoStack.pop();
        state.setLastMessage("Undo complete. Returned to the previous turn.");
        return new MoveResult(true, state.getLastMessage());
    }

    private void saveUndoSnapshot() {
        undoStack.push(new GameState(state));
    }

    private void collectCurrentTile(StringBuilder message) {
        Position here = state.getPlayerPosition();
        TileContent content = state.getTile(here);
        if (content == TileContent.SALVAGE) {
            state.getInventory().addSalvage();
            state.setTile(here, TileContent.EMPTY);
            message.append(" Collected a salvage crate.");
        } else if (content == TileContent.MEDKIT) {
            state.getInventory().addMedKit();
            state.setTile(here, TileContent.EMPTY);
            message.append(" Collected a medkit.");
        } else if (content == TileContent.SHIELD) {
            state.getInventory().addShield();
            state.setTile(here, TileContent.EMPTY);
            message.append(" Collected an energy shield.");
        } else if (content == TileContent.EXIT) {
            message.append(" Reached the exit airlock.");
        }
    }

    private void takeTurn(StringBuilder message) {
        for (Enemy enemy : state.getEnemies()) {
            enemy.advanceState();
        }
        state.incrementTurn();
        resolveEnemyThreats(message);
        checkWinOrLoss(message);
    }

    private void resolveEnemyThreats(StringBuilder message) {
        for (Enemy enemy : state.getEnemies()) {
            if (!enemy.threatens(state.getPlayerPosition())) {
                continue;
            }
            int damage = enemy.getState().damage();
            if (damage <= 0) {
                continue;
            }
            if (state.getInventory().useShield()) {
                message.append(" ").append(enemy.getName()).append(" attacked, but a shield absorbed the hit.");
                continue;
            }
            int newHealth = state.getHealth() - damage;
            if (newHealth <= 0 && state.getInventory().useMedKit()) {
                state.setHealth(2);
                message.append(" ").append(enemy.getName()).append(" caused critical damage, but a medkit restored health.");
            } else {
                state.setHealth(newHealth);
                message.append(" ").append(enemy.getName()).append(" dealt ").append(damage).append(" damage.");
            }
        }
    }

    private void checkWinOrLoss(StringBuilder message) {
        boolean atExit = state.getTile(state.getPlayerPosition()) == TileContent.EXIT;
        if (state.getInventory().getSalvage() >= GameState.TOTAL_SALVAGE && atExit) {
            state.setWon(true);
            state.setFinished(true);
            message.append(" All salvage recovered. Mission complete!");
            return;
        }
        if (state.getHealth() <= 0) {
            state.setWon(false);
            state.setFinished(true);
            message.append(" The salvage drone has been disabled. Mission failed.");
        }
    }

    public char[][] boardSymbols() {
        char[][] board = new char[state.getRows()][state.getColumns()];
        for (int row = 0; row < state.getRows(); row++) {
            for (int column = 0; column < state.getColumns(); column++) {
                Position position = new Position(row, column);
                board[row][column] = state.getTile(position).getSymbol();
            }
        }
        for (Enemy enemy : state.getEnemies()) {
            Position position = enemy.getPosition();
            board[position.getRow()][position.getColumn()] = enemy.symbol();
        }
        Position player = state.getPlayerPosition();
        board[player.getRow()][player.getColumn()] = 'P';
        return board;
    }

    public String boardAsText() {
        StringBuilder builder = new StringBuilder();
        char[][] board = boardSymbols();
        for (int row = 0; row < board.length; row++) {
            for (int column = 0; column < board[row].length; column++) {
                builder.append(board[row][column]).append(' ');
            }
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }

    public String statusText() {
        return "Turn: " + state.getTurn()
                + " | Health: " + state.getHealth()
                + " | " + state.getInventory()
                + " | Position: " + state.getPlayerPosition()
                + " | Message: " + state.getLastMessage();
    }

    public GameState getState() {
        return state;
    }

    public int undoCount() {
        return undoStack.size();
    }
}
