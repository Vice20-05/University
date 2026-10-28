package model;

/**
 * A semester, e.g. new Felev(2025, 1) = 2025 first semester.
 * Record: equals/hashCode/toString are generated automatically.
 * Comparable: semesters can be sorted (first by year, then by number).
 */
public record Felev(int ev, int szam) implements Comparable<Felev> {

    // compact constructor -> only the validation goes here
    public Felev {
        if (szam != 1 && szam != 2) {
            throw new IllegalArgumentException("A felev szama 1 vagy 2 lehet!");
        }
    }

    @Override
    public int compareTo(Felev other) {
        if (ev != other.ev) {
            return Integer.compare(ev, other.ev);
        }
        return Integer.compare(szam, other.szam);
    }

    @Override
    public String toString() {
        return ev + "/" + szam;
    }
}
