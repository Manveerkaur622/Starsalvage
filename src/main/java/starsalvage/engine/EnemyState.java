package starsalvage.engine;

import java.io.Serializable;

public enum EnemyState implements Serializable {
    DORMANT,
    ALERT,
    HUNTING;

    public EnemyState next() {
        if (this == DORMANT) {
            return ALERT;
        }
        if (this == ALERT) {
            return HUNTING;
        }
        return DORMANT;
    }

    public int damage() {
        if (this == ALERT) {
            return 1;
        }
        if (this == HUNTING) {
            return 2;
        }
        return 0;
    }
}
