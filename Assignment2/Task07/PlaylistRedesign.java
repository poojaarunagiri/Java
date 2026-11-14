
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;

public class PlaylistRedesign {

    static volatile long result;

    static long originalLinkedList(List<Integer> songs) {
        long sum = 0;

        for (int i = 0; i < songs.size(); i++) {
            sum += songs.get(i);
        }

        return sum;
    }

    static long redesignedIterator(LinkedList<Integer> songs) {
        long sum = 0;
        Iterator<Integer> iterator = songs.iterator();

        while (iterator.hasNext()) {
            sum += iterator.next();
        }

        return sum;
    }

    static long arrayListAccess(ArrayList<Integer> songs) {
        long sum = 0;

        for (int i = 0; i < songs.size(); i++) {
            sum += songs.get(i);
        }

        return sum;
    }

    static double measure(Runnable operation) {
        long total = 0;

        // Warm-up
        for (int i = 0; i < 2; i++) {
            operation.run();
        }

        // Five measured runs
        for (int i = 0; i < 5; i++) {
            long start = System.nanoTime();
            operation.run();
            total += System.nanoTime() - start;
        }

        return total / 5.0 / 1_000_000.0;
    }

    public static void main(String[] args) {

        int[] sizes = {1000, 5000, 10000};

        System.out.println("PLAYLIST REDESIGN BENCHMARK");
        System.out.println("===========================");

        System.out.println(
            "Size\tLinkedList get(i)\tLinkedList Iterator\tArrayList get(i)"
        );

        for (int size : sizes) {

            LinkedList<Integer> linked = new LinkedList<>();
            ArrayList<Integer> array = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                linked.add(i + 1);
                array.add(i + 1);
            }

            double original = measure(
                () -> result = originalLinkedList(linked)
            );

            double redesigned = measure(
                () -> result = redesignedIterator(linked)
            );

            double baseline = measure(
                () -> result = arrayListAccess(array)
            );

            System.out.printf(
                "%d\t%.4f ms\t\t%.4f ms\t\t%.4f ms%n",
                size, original, redesigned, baseline
            );

            // Verify that all approaches produce the same result.
            long a = originalLinkedList(linked);
            long b = redesignedIterator(linked);
            long c = arrayListAccess(array);

            System.out.println("Correctness check: " +
                (a == b && b == c ? "PASSED" : "FAILED"));
        }

        System.out.println("\nRedesign experiment completed.");
    }
}
