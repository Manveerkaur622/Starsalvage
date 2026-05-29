package starsalvage.engine;

import java.io.Serializable;

public enum TileContent implements Serializable {
    EMPTY('.'),
    SALVAGE('*'),
    MEDKIT('+'),
    SHIELD('S'),
    EXIT('E');

    private final char symbol;

    TileContent(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }
}
