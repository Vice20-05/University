package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A course. Exactly one teacher (1) is required, so it is a constructor parameter.
 */
public class Kurzus {
    private final String kod;
    private final String nev;
    private final int kredit;
    private final int maxLetszam;
    private final Oktato oktato;
    private final List<Beiratkozas> beiratkozasok = new ArrayList<>();

    public Kurzus(String kod, String nev, int kredit, int maxLetszam, Oktato oktato) {
        if (kod == null || kod.isBlank()) {
            throw new IllegalArgumentException("A kurzus kodja nem lehet ures!");
        }
        if (kredit <= 0) {
            throw new IllegalArgumentException("A kredit pozitiv kell legyen!");
        }
        if (maxLetszam <= 0) {
            throw new IllegalArgumentException("A max letszam pozitiv kell legyen!");
        }
        if (oktato == null) {
            throw new IllegalArgumentException("A kurzusnak kell oktato!");
        }
        this.kod = kod;
        this.nev = nev;
        this.kredit = kredit;
        this.maxLetszam = maxLetszam;
        this.oktato = oktato;
        oktato.addKurzus(this); // keep the other side of the connection up to date
    }

    public String getKod() {
        return kod;
    }

    public String getNev() {
        return nev;
    }

    public int getKredit() {
        return kredit;
    }

    public int getMaxLetszam() {
        return maxLetszam;
    }

    public Oktato getOktato() {
        return oktato;
    }

    public List<Beiratkozas> getBeiratkozasok() {
        return Collections.unmodifiableList(beiratkozasok);
    }

    // package-private: only Hallgato.beiratkozik() calls it
    void addBeiratkozas(Beiratkozas b) {
        beiratkozasok.add(b);
    }

    // how many students are enrolled in the given semester
    public long letszam(Felev felev) {
        return beiratkozasok.stream()
                .filter(b -> b.getFelev().equals(felev))
                .count();
    }

    public boolean telt(Felev felev) {
        return letszam(felev) >= maxLetszam;
    }

    /**
     * Pass rate: passed / graded enrollments (0.0 - 1.0).
     * Enrollments without a grade are not counted.
     */
    public double atmenesiArany() {
        long jegyesek = beiratkozasok.stream()
                .filter(b -> b.getJegy().isPresent())
                .count();
        if (jegyesek == 0) {
            return 0.0; // no grades yet -> avoid division by zero
        }
        long atmentek = beiratkozasok.stream()
                .filter(b -> b.getJegy().isPresent())
                .filter(b -> b.getJegy().get().atment())
                .count();
        return (double) atmentek / jegyesek;
    }

    @Override
    public String toString() {
        return "Kurzus[" + kod + ", " + nev + ", " + kredit + " kredit, max " + maxLetszam
                + " fo, oktato: " + oktato.getNev() + "]";
    }
}
