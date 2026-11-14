
import java.util.*;

public class ListBenchmark {

    static volatile long resultSink;

    static ArrayList<Integer> createArrayList(int size) {
        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            list.add(i);
        }

        return list;
    }

    static LinkedList<Integer> createLinkedList(int size) {
        LinkedList<Integer> list = new LinkedList<>();

        for (int i = 0; i < size; i++) {
            list.add(i);
        }

        return list;
    }

    static double measureAddEnd(boolean array, int size) {
        long start = System.nanoTime();

        if (array) {
            ArrayList<Integer> list = createArrayList(size);
            for (int i = 0; i < 1000; i++) list.add(i);
            resultSink = list.size();
        } else {
            LinkedList<Integer> list = createLinkedList(size);
            for (int i = 0; i < 1000; i++) list.add(i);
            resultSink = list.size();
        }

        return (System.nanoTime() - start) / 1_000_000.0;
    }

    static double measureFrontInsert(boolean array, int size) {
        long start = System.nanoTime();

        if (array) {
            ArrayList<Integer> list = createArrayList(size);
            for (int i = 0; i < 100; i++) list.add(0, i);
            resultSink = list.size();
        } else {
            LinkedList<Integer> list = createLinkedList(size);
            for (int i = 0; i < 100; i++) list.addFirst(i);
            resultSink = list.size();
        }

        return (System.nanoTime() - start) / 1_000_000.0;
    }

    static double measureMiddleInsert(boolean array, int size) {
        long start = System.nanoTime();

        if (array) {
            ArrayList<Integer> list = createArrayList(size);
            for (int i = 0; i < 100; i++) {
                list.add(list.size() / 2, i);
            }
            resultSink = list.size();
        } else {
            LinkedList<Integer> list = createLinkedList(size);
            for (int i = 0; i < 100; i++) {
                list.add(list.size() / 2, i);
            }
            resultSink = list.size();
        }

        return (System.nanoTime() - start) / 1_000_000.0;
    }

    static double measureFrontRemove(boolean array, int size) {
        long start = System.nanoTime();

        if (array) {
            ArrayList<Integer> list = createArrayList(size);
            for (int i = 0; i < 100; i++) list.remove(0);
            resultSink = list.size();
        } else {
            LinkedList<Integer> list = createLinkedList(size);
            for (int i = 0; i < 100; i++) list.removeFirst();
            resultSink = list.size();
        }

        return (System.nanoTime() - start) / 1_000_000.0;
    }

    static double measureMiddleRemove(boolean array, int size) {
        long start = System.nanoTime();

        if (array) {
            ArrayList<Integer> list = createArrayList(size);
            for (int i = 0; i < 100; i++) {
                list.remove(list.size() / 2);
            }
            resultSink = list.size();
        } else {
            LinkedList<Integer> list = createLinkedList(size);
            for (int i = 0; i < 100; i++) {
                list.remove(list.size() / 2);
            }
            resultSink = list.size();
        }

        return (System.nanoTime() - start) / 1_000_000.0;
    }

    static double measureAccess(boolean array, int size) {
        long start = System.nanoTime();
        long sum = 0;

        if (array) {
            ArrayList<Integer> list = createArrayList(size);
            for (int i = 0; i < 1000; i++) {
                sum += list.get(size / 2);
            }
        } else {
            LinkedList<Integer> list = createLinkedList(size);
            for (int i = 0; i < 1000; i++) {
                sum += list.get(size / 2);
            }
        }

        resultSink = sum;
        return (System.nanoTime() - start) / 1_000_000.0;
    }

    static double measureIteration(boolean array, int size) {
        long start = System.nanoTime();
        long sum = 0;

        if (array) {
            ArrayList<Integer> list = createArrayList(size);
            for (int value : list) sum += value;
        } else {
            LinkedList<Integer> list = createLinkedList(size);
            for (int value : list) sum += value;
        }

        resultSink = sum;
        return (System.nanoTime() - start) / 1_000_000.0;
    }

    static double average(int operation, boolean array,
                          int size, int repetitions) {

        double total = 0;

        for (int r = 0; r < repetitions; r++) {
            switch (operation) {
                case 1: total += measureAddEnd(array, size); break;
                case 2: total += measureFrontInsert(array, size); break;
                case 3: total += measureMiddleInsert(array, size); break;
                case 4: total += measureFrontRemove(array, size); break;
                case 5: total += measureMiddleRemove(array, size); break;
                case 6: total += measureAccess(array, size); break;
                case 7: total += measureIteration(array, size); break;
            }
        }

        return total / repetitions;
    }

    static void demonstrateArrayDeque() {
        System.out.println("\nUP NEXT QUEUE USING ARRAYDEQUE");
        System.out.println("--------------------------------");

        ArrayDeque<String> queue = new ArrayDeque<>();

        queue.addLast("Song A");
        queue.addLast("Song B");
        queue.addLast("Song C");

        System.out.println("Initial queue: " + queue);

        queue.addFirst("Priority Song");
        queue.addLast("Song D");

        System.out.println("After adding at both ends: " + queue);

        System.out.println("Playing next: " + queue.removeFirst());

        System.out.println("Final queue: " + queue);
    }

    public static void main(String[] args) {

        int[] sizes = {1000, 5000, 10000};
        int repetitions = 5;

        String[] operations = {
            "Add at end",
            "Insert at front",
            "Insert in middle",
            "Remove from front",
            "Remove from middle",
            "Access k-th element",
            "Full iteration"
        };

        System.out.println("ARRAYLIST VS LINKEDLIST BENCHMARK");
        System.out.println("Average time in milliseconds");
        System.out.println("Each measurement uses 5 repetitions.");

        for (int size : sizes) {

            System.out.println("\n================================");
            System.out.println("Playlist size: " + size);
            System.out.println("================================");

            System.out.printf("%-22s %15s %15s%n",
                    "Operation", "ArrayList", "LinkedList");

            for (int op = 1; op <= 7; op++) {

                double arrayTime =
                    average(op, true, size, repetitions);

                double linkedTime =
                    average(op, false, size, repetitions);

                System.out.printf("%-22s %12.4f ms %12.4f ms%n",
                        operations[op - 1],
                        arrayTime,
                        linkedTime);
            }
        }

        demonstrateArrayDeque();

        System.out.println("\nBenchmark completed.");
    }
}