import java.util.List;

import model.Beiratkozas;
import model.Felev;
import model.Hallgato;
import model.Kurzus;
import model.Lekerdezesek;
import model.Oktato;

/**
 * Small demo that shows the model working.
 */
public class Main {
    public static void main(String[] args) {
        Oktato kovacs = new Oktato("O1", "Kovacs Bela", "kovacs@egyetem.hu");
        Oktato nagy = new Oktato("O2", "Nagy Eva", "nagy@egyetem.hu");

        Kurzus java = new Kurzus("PROG1", "Programozas 1", 5, 30, kovacs);
        Kurzus adat = new Kurzus("ADAT", "Adatbazisok", 4, 20, kovacs);
        Kurzus mat = new Kurzus("MAT1", "Matematika 1", 6, 2, nagy);

        Hallgato anna = new Hallgato("H1", "Kiss Anna", "anna@stud.hu");
        Hallgato peter = new Hallgato("H2", "Toth Peter", "peter@stud.hu");
        Hallgato zoli = new Hallgato("H3", "Szabo Zoltan", "zoli@stud.hu");
        Hallgato reka = new Hallgato("H4", "Varga Reka", "reka@stud.hu");

        Felev osz = new Felev(2025, 1);

        anna.beiratkozik(java, osz).setJegy(9);
        anna.beiratkozik(mat, osz).setJegy(7);
        peter.beiratkozik(java, osz).setJegy(4);
        peter.beiratkozik(adat, osz).setJegy(8);
        zoli.beiratkozik(java, osz).setJegy(10);
        reka.beiratkozik(java, osz).setJegy(6);
        zoli.beiratkozik(mat, osz); // no grade yet

        // business rule demo: MAT1 is full (max 2)
        try {
            reka.beiratkozik(mat, osz);
        } catch (IllegalStateException e) {
            System.out.println("Hiba: " + e.getMessage());
        }

        System.out.println();
        for (Hallgato h : List.of(anna, peter, zoli, reka)) {
            System.out.println(h);
            for (Beiratkozas b : h.getBeiratkozasok()) {
                System.out.println("   " + b);
            }
            System.out.printf("   atlag: %.2f, megszerzett kredit: %d%n",
                    h.sulyozottAtlag(), h.megszerzettKredit());
        }

        System.out.println();
        System.out.printf("%s atmenesi arany: %.0f%%%n", java.getKod(), java.atmenesiArany() * 100);
        System.out.println("Legjobb 3 a " + java.getKod() + " kurzuson:");
        for (Hallgato h : Lekerdezesek.legjobbHaromHallgato(java)) {
            System.out.println("   " + h.getNev());
        }

        System.out.println();
        System.out.println("Kreditek oktatonkent:");
        Lekerdezesek.kreditekOktatonkent(List.of(java, adat, mat))
                .forEach((oktato, kredit) -> System.out.println("   " + oktato.getNev() + ": " + kredit));
    }
}
