# Egyetemi tanulmányi rendszer – UML-ből Java (b-03)

A small university study system model built from a UML description:
`Szemely` (abstract) → `Hallgato`, `Oktato`; `Kurzus`, `Beiratkozas`, `Jegy`, `Felev`.

## Requirements

- Java JDK 17 or newer (uses `record`)

## Project structure

```
University/
├── src/
│   ├── Main.java          -> small demo
│   └── model/
│       ├── Szemely.java       -> abstract parent, equals/hashCode by azonosito
│       ├── Hallgato.java      -> enrollment rules, weighted average, credits
│       ├── Oktato.java
│       ├── Kurzus.java        -> head count, full check, pass rate
│       ├── Beiratkozas.java   -> Optional<Jegy>
│       ├── Jegy.java          -> record, 1..10
│       ├── Felev.java         -> record, Comparable
│       └── Lekerdezesek.java  -> Stream API queries
├── test/model/
│   └── TanulmanyiRendszerTest.java   -> 14 JUnit 5 tests
├── lib/       -> junit-platform-console-standalone-6.1.3.jar
├── docs/
│   └── university.puml    -> PlantUML class diagram
└── README.md
```

## How to run

```bash
javac -d out src/Main.java src/model/*.java
java -cp out Main
```

## How to run the tests

```bash
javac -d out src/Main.java src/model/*.java
javac -d out_test -cp out:lib/junit-platform-console-standalone-6.1.3.jar test/model/*.java
java -jar lib/junit-platform-console-standalone-6.1.3.jar \
  execute --class-path out:out_test --scan-class-path
```

## UML diagram

`docs/university.puml` – open it with the PlantUML VS Code extension or paste it
into https://www.plantuml.com/plantuml. It shows:

- inheritance: `Szemely <|-- Hallgato`, `Szemely <|-- Oktato`
- **composition**: `Hallgato *-- Beiratkozas` (0..*), `Beiratkozas *-- Jegy` (0..1)
- **aggregation**: `Oktato o-- Kurzus` (0..*)
- association with multiplicity 1: `Beiratkozas --> Kurzus`, `Beiratkozas --> Felev`

## How the multiplicities appear in the code

| UML              | Code                                                        |
|------------------|-------------------------------------------------------------|
| `1` (required)   | constructor parameter + null check, `final` field (e.g. `Kurzus.oktato`) |
| `0..1` (optional)| `Optional<Jegy> getJegy()` – never returns `null`           |
| `*` (many)       | `List<...>` inside, outside only `Collections.unmodifiableList` |

## Business rules

- A full course can't be taken → `IllegalStateException`
- The same course only once per semester → `IllegalStateException`
- The grade is an integer from 1 to 10 → checked in the `Jegy` constructor
- Invalid data (empty id/name, email without `@`, credit ≤ 0) → `IllegalArgumentException`

## Where I differed from the text and why

1. **The full check is per semester.** The text only says "a full course can't be taken".
   If I counted every enrollment ever, a course would be full forever after one year,
   so `Kurzus.telt(felev)` only counts the students of that semester.
2. **Enrollment only through `Hallgato.beiratkozik()`.** The `Beiratkozas` constructor
   is package-private, so nobody can create one that skips the rules. This is also
   why it is drawn as a composition: the enrollment belongs to the student.
3. **Two-way links.** `Kurzus` also keeps its list of enrollments, and `Oktato` its list
   of courses (needed for head count, pass rate and the queries). The list is updated
   automatically, the `add...` methods are package-private.
4. **`Jegy` and `Felev` are records.** They are small value objects, a record gives
   immutability and `equals/hashCode/toString` for free. `Felev` is `Comparable` (stretch task).
5. **Calculations only use graded enrollments.** The weighted average and the pass rate
   ignore enrollments without a grade yet; if there are none the result is `0.0`.
   "Passed" = grade ≥ 5, as the task says.
6. **`equals` uses `getClass()`**, so a `Hallgato` and an `Oktato` with the same id
   are not equal.
7. **Not done from the stretch tasks:** transcript file export and prerequisites
   (to keep the code simple).
