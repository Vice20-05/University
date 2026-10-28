package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the business rules and the calculations.
 */
public class TanulmanyiRendszerTest {

    private Oktato oktato;
    private Kurzus java;   // 5 credits, max 30
    private Kurzus mat;    // 3 credits, max 2
    private Hallgato anna;
    private Felev osz;

    // runs before every test -> each test starts with fresh objects
    @BeforeEach
    public void setUp() {
        oktato = new Oktato("O1", "Kovacs Bela", "kovacs@egyetem.hu");
        java = new Kurzus("PROG1", "Programozas 1", 5, 30, oktato);
        mat = new Kurzus("MAT1", "Matematika 1", 3, 2, oktato);
        anna = new Hallgato("H1", "Kiss Anna", "anna@stud.hu");
        osz = new Felev(2025, 1);
    }

    // ---------- business rules ----------

    @Test
    public void fullCourseCannotBeTaken() {
        new Hallgato("H2", "B", "b@x.hu").beiratkozik(mat, osz);
        new Hallgato("H3", "C", "c@x.hu").beiratkozik(mat, osz);
        // mat has max 2 students -> the third one fails
        assertThrows(IllegalStateException.class, () -> anna.beiratkozik(mat, osz));
    }

    @Test
    public void fullCourseIsOnlyFullInThatSemester() {
        new Hallgato("H2", "B", "b@x.hu").beiratkozik(mat, osz);
        new Hallgato("H3", "C", "c@x.hu").beiratkozik(mat, osz);
        // next semester is empty again
        anna.beiratkozik(mat, new Felev(2025, 2));
        assertEquals(1, anna.getBeiratkozasok().size());
    }

    @Test
    public void sameCourseOnlyOncePerSemester() {
        anna.beiratkozik(java, osz);
        assertThrows(IllegalStateException.class, () -> anna.beiratkozik(java, osz));
        // but in another semester it is allowed (e.g. retake)
        anna.beiratkozik(java, new Felev(2025, 2));
        assertEquals(2, anna.getBeiratkozasok().size());
    }

    @Test
    public void gradeMustBeBetween1And10() {
        Beiratkozas b = anna.beiratkozik(java, osz);
        assertThrows(IllegalArgumentException.class, () -> b.setJegy(0));
        assertThrows(IllegalArgumentException.class, () -> b.setJegy(11));
        b.setJegy(10); // ok
        assertEquals(10, b.getJegy().get().ertek());
    }

    @Test
    public void newEnrollmentHasNoGrade() {
        Beiratkozas b = anna.beiratkozik(java, osz);
        assertTrue(b.getJegy().isEmpty());
    }

    @Test
    public void invalidEmailIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Hallgato("H9", "X", "nincs-kukac"));
    }

    // ---------- calculations ----------

    @Test
    public void weightedAverageUsesCredits() {
        anna.beiratkozik(java, osz).setJegy(10); // 5 credits
        anna.beiratkozik(mat, osz).setJegy(2);   // 3 credits
        // (10*5 + 2*3) / 8 = 56 / 8 = 7.0
        assertEquals(7.0, anna.sulyozottAtlag(), 0.0001);
    }

    @Test
    public void averageIsZeroWithoutGrades() {
        anna.beiratkozik(java, osz);
        assertEquals(0.0, anna.sulyozottAtlag(), 0.0001);
    }

    @Test
    public void earnedCreditsOnlyCountPassedCourses() {
        anna.beiratkozik(java, osz).setJegy(5); // passed, 5 credits
        anna.beiratkozik(mat, osz).setJegy(4);  // failed
        assertEquals(5, anna.megszerzettKredit());
    }

    @Test
    public void passRateOfCourse() {
        anna.beiratkozik(java, osz).setJegy(8);
        new Hallgato("H2", "B", "b@x.hu").beiratkozik(java, osz).setJegy(3);
        new Hallgato("H3", "C", "c@x.hu").beiratkozik(java, osz).setJegy(6);
        new Hallgato("H4", "D", "d@x.hu").beiratkozik(java, osz); // no grade -> not counted
        // 2 passed out of 3 graded
        assertEquals(2.0 / 3, java.atmenesiArany(), 0.0001);
    }

    // ---------- equals / hashCode ----------

    @Test
    public void personsWithSameIdAreEqual() {
        Hallgato masik = new Hallgato("H1", "Mas Nev", "mas@stud.hu");
        assertEquals(anna, masik);
        assertEquals(anna.hashCode(), masik.hashCode());
        assertNotEquals(anna, new Hallgato("H2", "Kiss Anna", "anna@stud.hu"));
    }

    // ---------- stream queries ----------

    @Test
    public void bestThreeStudents() {
        Hallgato b = new Hallgato("H2", "B", "b@x.hu");
        Hallgato c = new Hallgato("H3", "C", "c@x.hu");
        Hallgato d = new Hallgato("H4", "D", "d@x.hu");
        anna.beiratkozik(java, osz).setJegy(6);
        b.beiratkozik(java, osz).setJegy(9);
        c.beiratkozik(java, osz).setJegy(3);
        d.beiratkozik(java, osz).setJegy(10);

        List<Hallgato> top = Lekerdezesek.legjobbHaromHallgato(java);
        assertEquals(List.of(d, b, anna), top);
        assertFalse(top.contains(c));
    }

    @Test
    public void creditsPerTeacher() {
        Oktato nagy = new Oktato("O2", "Nagy Eva", "nagy@egyetem.hu");
        Kurzus fizika = new Kurzus("FIZ", "Fizika", 4, 10, nagy);

        Map<Oktato, Integer> eredmeny = Lekerdezesek.kreditekOktatonkent(List.of(java, mat, fizika));
        assertEquals(8, eredmeny.get(oktato)); // 5 + 3
        assertEquals(4, eredmeny.get(nagy));
    }

    // ---------- Felev (stretch) ----------

    @Test
    public void semestersAreSorted() {
        assertTrue(new Felev(2024, 2).compareTo(new Felev(2025, 1)) < 0);
        assertTrue(new Felev(2025, 2).compareTo(new Felev(2025, 1)) > 0);
    }
}
