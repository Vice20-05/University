package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A student. Has many enrollments (0..*).
 * The enrollments belong to the student (composition): they are created here.
 */
public class Hallgato extends Szemely {
    private final List<Beiratkozas> beiratkozasok = new ArrayList<>();

    public Hallgato(String azonosito, String nev, String email) {
        super(azonosito, nev, email);
    }

    /**
     * Enrolls the student to a course in a semester.
     * Business rules:
     *  - a full course can't be taken
     *  - the same course only once per semester
     */
    public Beiratkozas beiratkozik(Kurzus kurzus, Felev felev) {
        if (kurzus == null || felev == null) {
            throw new IllegalArgumentException("A kurzus es a felev kotelezo!");
        }
        if (kurzus.telt(felev)) {
            throw new IllegalStateException("A kurzus megtelt: " + kurzus.getKod());
        }
        for (Beiratkozas b : beiratkozasok) {
            if (b.getKurzus().equals(kurzus) && b.getFelev().equals(felev)) {
                throw new IllegalStateException("Mar fel vagy veve erre a kurzusra ebben a felevben!");
            }
        }

        Beiratkozas uj = new Beiratkozas(this, kurzus, felev);
        beiratkozasok.add(uj);
        kurzus.addBeiratkozas(uj); // the course also has to know about it
        return uj;
    }

    public List<Beiratkozas> getBeiratkozasok() {
        return Collections.unmodifiableList(beiratkozasok);
    }

    /**
     * Credit-weighted average: sum(grade * credit) / sum(credit).
     * Only enrollments with a grade count. 0.0 if there is no grade yet.
     */
    public double sulyozottAtlag() {
        int osszeg = 0;
        int kreditek = 0;
        for (Beiratkozas b : beiratkozasok) {
            if (b.getJegy().isPresent()) {
                int kredit = b.getKurzus().getKredit();
                osszeg += b.getJegy().get().ertek() * kredit;
                kreditek += kredit;
            }
        }
        if (kreditek == 0) {
            return 0.0;
        }
        return (double) osszeg / kreditek;
    }

    // sum of credits of the passed courses (grade >= 5)
    public int megszerzettKredit() {
        return beiratkozasok.stream()
                .filter(b -> b.getJegy().isPresent() && b.getJegy().get().atment())
                .mapToInt(b -> b.getKurzus().getKredit())
                .sum();
    }
}
