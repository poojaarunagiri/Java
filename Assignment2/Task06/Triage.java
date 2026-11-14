
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

class Patient {
    final String name;
    final int severity;
    final long arrival;

    Patient(String name, int severity, long arrival) {
        this.name = name;
        this.severity = severity;
        this.arrival = arrival;
    }

    @Override
    public String toString() {
        return name + "(S" + severity + ")";
    }
}

public class Triage {

    private long counter = 0;

    // Lowest severity number first; earliest arrival breaks ties.
    private final PriorityQueue<Patient> queue = new PriorityQueue<>(
        Comparator.comparingInt((Patient p) -> p.severity)
                  .thenComparingLong(p -> p.arrival)
    );

    void admit(String name, int severity) {
        if (severity < 1 || severity > 5) {
            throw new IllegalArgumentException("Severity must be from 1 to 5");
        }

        queue.add(new Patient(name, severity, counter++));
    }

    Patient next() {
        return queue.poll();
    }

    void displayTreatmentOrder() {
        System.out.println("Treatment order:");

        Patient patient;
        while ((patient = next()) != null) {
            System.out.println(patient);
        }
    }

    static void growthInvestigation() {
        System.out.println("\nGROWTH INVESTIGATION");
        System.out.println("Patients\tAdd Time (ms)\tPoll Time (ms)");

        int[] sizes = {100, 1000, 5000};
        Random random = new Random(42);

        for (int size : sizes) {
            long addTotal = 0;
            long pollTotal = 0;

            // Repeat each experiment five times.
            for (int trial = 0; trial < 5; trial++) {
                PriorityQueue<Patient> pq = new PriorityQueue<>(
                    Comparator.comparingInt((Patient p) -> p.severity)
                              .thenComparingLong(p -> p.arrival)
                );

                long startAdd = System.nanoTime();

                for (int i = 0; i < size; i++) {
                    pq.add(new Patient(
                        "Patient" + i,
                        random.nextInt(5) + 1,
                        i
                    ));
                }

                long endAdd = System.nanoTime();

                long startPoll = System.nanoTime();

                while (!pq.isEmpty()) {
                    pq.poll();
                }

                long endPoll = System.nanoTime();

                addTotal += endAdd - startAdd;
                pollTotal += endPoll - startPoll;
            }

            double averageAdd = addTotal / 5.0 / 1_000_000.0;
            double averagePoll = pollTotal / 5.0 / 1_000_000.0;

            System.out.printf(
                "%d\t\t%.4f\t\t%.4f%n",
                size, averageAdd, averagePoll
            );
        }
    }

    public static void main(String[] args) {

        Triage triage = new Triage();

        System.out.println("HOSPITAL EMERGENCY TRIAGE");
        System.out.println("=========================");

        System.out.println("\nAdmitting 8 patients:");

        triage.admit("Arun", 3);
        triage.admit("Bala", 1);
        triage.admit("Charan", 2);
        triage.admit("Divya", 1);
        triage.admit("Esha", 4);
        triage.admit("Farhan", 2);
        triage.admit("Gokul", 5);
        triage.admit("Harini", 3);

        System.out.println("Patients admitted: 8");

        System.out.println("\nPriorityQueue internal display:");
        System.out.println(triage.queue);

        System.out.println();
        triage.displayTreatmentOrder();

        growthInvestigation();

        System.out.println("\nTriage completed.");
    }
}
