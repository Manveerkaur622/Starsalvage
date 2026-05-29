package starsalvage.engine;

public enum Direction {
    NORTH(-1, 0), SOUTH(1, 0), WEST(0, -1), EAST(0, 1);

    private final int rowDelta;
    private final int columnDelta;

    Direction(int rowDelta, int columnDelta) {
        this.rowDelta = rowDelta;
        this.columnDelta = columnDelta;
    }

    public int getRowDelta() {
        return rowDelta;
    }

    public int getColumnDelta() {
        return columnDelta;
    }

    public static Direction fromCommand(String command) {
        if (command == null || command.isBlank()) {
            return null;
        }
        String value = command.trim().toLowerCase();
        if (value.equals("n") || value.equals("north") || value.equals("up")) {
            return NORTH;
        }
        if (value.equals("s") || value.equals("south") || value.equals("down")) {
            return SOUTH;
        }
        if (value.equals("w") || value.equals("west") || value.equals("left")) {
            return WEST;
        }
        if (value.equals("e") || value.equals("east") || value.equals("right")) {
            return EAST;
        }
        return null;
    }
}
