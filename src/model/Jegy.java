package model;

/**
 * A grade between 1 and 10.
 * Record, so it is immutable: if the grade changes we make a new Jegy.
 */
public record Jegy(int ertek) {

    public Jegy {
        if (ertek < 1 || ertek > 10) {
            throw new IllegalArgumentException("A jegy 1 es 10 kozotti egesz lehet, nem: " + ertek);
        }
    }

    // grade 5 or above means the course is passed
    public boolean atment() {
        return ertek >= 5;
    }

    @Override
    public String toString() {
        return "Jegy[" + ertek + "]";
    }
}
