package starsalvage.engine;

import java.io.Serializable;

public class Enemy implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final Position position;
    private EnemyState state;

    public Enemy(String name, Position position, EnemyState state) {
        this.name = name;
        this.position = position;
        this.state = state;
    }

    public Enemy(Enemy other) {
        this.name = other.name;
        this.position = other.position;
        this.state = other.state;
    }

    public void advanceState() {
        state = state.next();
    }

    public String getName() {
        return name;
    }

    public Position getPosition() {
        return position;
    }

    public EnemyState getState() {
        return state;
    }

    public boolean threatens(Position playerPosition) {
        return state != EnemyState.DORMANT && position.manhattanDistance(playerPosition) <= 1;
    }

    public char symbol() {
        if (state == EnemyState.DORMANT) {
            return 'd';
        }
        if (state == EnemyState.ALERT) {
            return 'A';
        }
        return 'H';
    }
}
