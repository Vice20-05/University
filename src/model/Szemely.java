package model;

import java.util.Objects;

/**
 * Common parent of Hallgato and Oktato.
 * Abstract, because there is no "plain" person in the system.
 */
public abstract class Szemely {
    private final String azonosito; // never changes -> final
    private String nev;
    private String email;

    protected Szemely(String azonosito, String nev, String email) {
        // validation: nothing can be empty, and the email needs an @
        if (azonosito == null || azonosito.isBlank()) {
            throw new IllegalArgumentException("Az azonosito nem lehet ures!");
        }
        if (nev == null || nev.isBlank()) {
            throw new IllegalArgumentException("A nev nem lehet ures!");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Hibas e-mail cim: " + email);
        }
        this.azonosito = azonosito;
        this.nev = nev;
        this.email = email;
    }

    public String getAzonosito() {
        return azonosito;
    }

    public String getNev() {
        return nev;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Hibas e-mail cim: " + email);
        }
        this.email = email;
    }

    // Two people are the same if their azonosito is the same.
    // getClass() check: a Hallgato and an Oktato are never equal.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Szemely other = (Szemely) o;
        return azonosito.equals(other.azonosito);
    }

    // must use the same field as equals
    @Override
    public int hashCode() {
        return Objects.hash(azonosito);
    }

    @Override
    public String toString() {
        // getSimpleName() -> "Hallgato" or "Oktato"
        return getClass().getSimpleName() + "[" + azonosito + ", " + nev + ", " + email + "]";
    }
}
