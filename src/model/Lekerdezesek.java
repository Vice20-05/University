package model;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Stream API queries. Only static methods, no objects needed.
 */
public class Lekerdezesek {

    private Lekerdezesek() {
        // utility class, no instances
    }

    /**
     * The best 3 students on a course (by grade, highest first).
     * Students without a grade are left out.
     */
    public static List<Hallgato> legjobbHaromHallgato(Kurzus kurzus) {
        return kurzus.getBeiratkozasok().stream()
                .filter(b -> b.getJegy().isPresent())
                .sorted(Comparator.comparingInt((Beiratkozas b) -> b.getJegy().get().ertek()).reversed())
                .limit(3)
                .map(Beiratkozas::getHallgato)
                .collect(Collectors.toList());
    }

    /**
     * For every teacher: the sum of credits of the courses they teach.
     * Example: {Kovacs -> 9, Nagy -> 4}
     */
    public static Map<Oktato, Integer> kreditekOktatonkent(List<Kurzus> kurzusok) {
        return kurzusok.stream()
                .collect(Collectors.groupingBy(
                        Kurzus::getOktato,
                        Collectors.summingInt(Kurzus::getKredit)));
    }
}
