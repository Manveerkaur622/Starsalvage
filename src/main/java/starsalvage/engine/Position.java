package starsalvage.engine;

import java.io.Serializable;
import java.util.Objects;

public final class Position implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int row;
    private final int column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Position move(Direction direction) {
        return new Position(row + direction.getRowDelta(), column + direction.getColumnDelta());
    }

    public int manhattanDistance(Position other) {
        return Math.abs(row - other.row) + Math.abs(column - other.column);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Position)) {
            return false;
        }
        Position position = (Position) o;
        return row == position.row && column == position.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public String toString() {
        return "(" + row + ", " + column + ")";
    }
}
