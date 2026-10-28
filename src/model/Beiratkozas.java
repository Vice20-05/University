package model;

import java.util.Optional;

/**
 * One enrollment: a student takes a course in a semester.
 * Hallgato, Kurzus and Felev are required (1) -> constructor parameters.
 * The grade is optional (0..1) -> Optional<Jegy>.
 */
public class Beiratkozas {
    private final Hallgato hallgato;
    private final Kurzus kurzus;
    private final Felev felev;
    private Jegy jegy; // null until the student gets a grade

    // package-private: only Hallgato.beiratkozik() can create one,
    // so the business rules can't be skipped
    Beiratkozas(Hallgato hallgato, Kurzus kurzus, Felev felev) {
        this.hallgato = hallgato;
        this.kurzus = kurzus;
        this.felev = felev;
    }

    public Hallgato getHallgato() {
        return hallgato;
    }

    public Kurzus getKurzus() {
        return kurzus;
    }

    public Felev getFelev() {
        return felev;
    }

    // we never return null, the caller gets an Optional
    public Optional<Jegy> getJegy() {
        return Optional.ofNullable(jegy);
    }

    // the 1-10 check happens inside the Jegy constructor
    public void setJegy(int ertek) {
        this.jegy = new Jegy(ertek);
    }

    @Override
    public String toString() {
        String jegySzoveg = (jegy == null) ? "nincs jegy" : String.valueOf(jegy.ertek());
        return "Beiratkozas[" + hallgato.getNev() + ", " + kurzus.getKod() + ", " + felev
                + ", " + jegySzoveg + "]";
    }
}
