package starsalvage.engine;

import java.io.Serializable;

public class Inventory implements Serializable {
    private static final long serialVersionUID = 1L;

    private int salvage;
    private int medKits;
    private int shields;
    private int score;

    public Inventory() {
        this.salvage = 0;
        this.medKits = 0;
        this.shields = 0;
        this.score = 0;
    }

    public Inventory(Inventory other) {
        this.salvage = other.salvage;
        this.medKits = other.medKits;
        this.shields = other.shields;
        this.score = other.score;
    }

    public void addSalvage() {
        salvage++;
        score += 100;
    }

    public void addMedKit() {
        medKits++;
        score += 25;
    }

    public void addShield() {
        shields++;
        score += 25;
    }

    public boolean useMedKit() {
        if (medKits <= 0) {
            return false;
        }
        medKits--;
        return true;
    }

    public boolean useShield() {
        if (shields <= 0) {
            return false;
        }
        shields--;
        return true;
    }

    public int getSalvage() {
        return salvage;
    }

    public int getMedKits() {
        return medKits;
    }

    public int getShields() {
        return shields;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "salvage=" + salvage + ", medKits=" + medKits + ", shields=" + shields + ", score=" + score;
    }
}
