package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A teacher. One teacher can teach many courses (0..*).
 */
public class Oktato extends Szemely {
    private final List<Kurzus> kurzusok = new ArrayList<>();

    public Oktato(String azonosito, String nev, String email) {
        super(azonosito, nev, email);
    }

    // package-private: only Kurzus calls this, when a new course is created
    void addKurzus(Kurzus kurzus) {
        kurzusok.add(kurzus);
    }

    // read-only view, so nobody can modify the list from outside
    public List<Kurzus> getKurzusok() {
        return Collections.unmodifiableList(kurzusok);
    }
}
