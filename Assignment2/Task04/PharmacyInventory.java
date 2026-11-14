
import java.util.*;

public class PharmacyInventory {

    static class Medicine {
        String code;
        String name;
        int quantity;
        double price;

        Medicine(String code, String name, int quantity, double price) {
            this.code = code;
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }

        @Override
        public String toString() {
            return code + " | " + name + " | Stock: "
                    + quantity + " | Price: " + price;
        }
    }

    static final int LOOKUPS = 100000;
    static volatile long resultSink;

    static List<Medicine> generateMedicines(int size) {

        List<Medicine> medicines = new ArrayList<>();

        for (int i = 1; i <= size; i++) {

            String code = String.format("MED%06d", i);

            medicines.add(new Medicine(
                    code,
                    "Medicine" + i,
                    100 + i,
                    10.0 + i
            ));
        }

        return medicines;
    }

    static HashMap<String, Medicine> createHashMap(
            List<Medicine> medicines) {

        HashMap<String, Medicine> map = new HashMap<>();

        for (Medicine medicine : medicines) {
            map.put(medicine.code, medicine);
        }

        return map;
    }

    static TreeMap<String, Medicine> createTreeMap(
            List<Medicine> medicines) {

        TreeMap<String, Medicine> map = new TreeMap<>();

        for (Medicine medicine : medicines) {
            map.put(medicine.code, medicine);
        }

        return map;
    }

    static LinkedHashMap<String, Medicine> createLinkedHashMap(
            List<Medicine> medicines) {

        LinkedHashMap<String, Medicine> map = new LinkedHashMap<>();

        for (Medicine medicine : medicines) {
            map.put(medicine.code, medicine);
        }

        return map;
    }

    static String[] createLookupKeys(int size) {

        String[] keys = new String[LOOKUPS];

        for (int i = 0; i < LOOKUPS; i++) {
            int index = (i % size) + 1;
            keys[i] = String.format("MED%06d", index);
        }

        return keys;
    }

    static double measureHashLookup(
            HashMap<String, Medicine> map,
            String[] keys) {

        long sum = 0;
        long start = System.nanoTime();

        for (String key : keys) {
            Medicine medicine = map.get(key);
            sum += medicine.quantity;
        }

        long end = System.nanoTime();
        resultSink = sum;

        return (end - start) / 1_000_000.0;
    }

    static double measureTreeLookup(
            TreeMap<String, Medicine> map,
            String[] keys) {

        long sum = 0;
        long start = System.nanoTime();

        for (String key : keys) {
            Medicine medicine = map.get(key);
            sum += medicine.quantity;
        }

        long end = System.nanoTime();
        resultSink = sum;

        return (end - start) / 1_000_000.0;
    }

    static double measureLinkedLookup(
            LinkedHashMap<String, Medicine> map,
            String[] keys) {

        long sum = 0;
        long start = System.nanoTime();

        for (String key : keys) {
            Medicine medicine = map.get(key);
            sum += medicine.quantity;
        }

        long end = System.nanoTime();
        resultSink = sum;

        return (end - start) / 1_000_000.0;
    }

    static double measureReport(
            Map<String, Medicine> map,
            int repetitions) {

        long totalTime = 0;
        long sum = 0;

        for (int r = 0; r < repetitions; r++) {

            long start = System.nanoTime();

            for (Medicine medicine : map.values()) {
                sum += medicine.quantity;
            }

            long end = System.nanoTime();
            totalTime += end - start;
        }

        resultSink = sum;

        return totalTime / (double) repetitions / 1_000_000.0;
    }

    static double averageHashLookup(
            HashMap<String, Medicine> map,
            String[] keys,
            int repetitions) {

        double total = 0;

        for (int i = 0; i < repetitions; i++) {
            total += measureHashLookup(map, keys);
        }

        return total / repetitions;
    }

    static double averageTreeLookup(
            TreeMap<String, Medicine> map,
            String[] keys,
            int repetitions) {

        double total = 0;

        for (int i = 0; i < repetitions; i++) {
            total += measureTreeLookup(map, keys);
        }

        return total / repetitions;
    }

    static double averageLinkedLookup(
            LinkedHashMap<String, Medicine> map,
            String[] keys,
            int repetitions) {

        double total = 0;

        for (int i = 0; i < repetitions; i++) {
            total += measureLinkedLookup(map, keys);
        }

        return total / repetitions;
    }

    static boolean isCodeSorted(Map<String, Medicine> map) {

        String previous = "";

        for (String code : map.keySet()) {

            if (code.compareTo(previous) < 0) {
                return false;
            }

            previous = code;
        }

        return true;
    }

    static boolean isInsertionOrdered(
            Map<String, Medicine> map,
            List<Medicine> original) {

        Iterator<String> iterator = map.keySet().iterator();

        for (Medicine medicine : original) {

            if (!iterator.hasNext()) {
                return false;
            }

            if (!iterator.next().equals(medicine.code)) {
                return false;
            }
        }

        return !iterator.hasNext();
    }

    static void runExperiment(int size) {

        System.out.println("\n======================================");
        System.out.println("INVENTORY SIZE: " + size);
        System.out.println("======================================");

        List<Medicine> medicines = generateMedicines(size);

        HashMap<String, Medicine> hashMap =
                createHashMap(medicines);

        TreeMap<String, Medicine> treeMap =
                createTreeMap(medicines);

        LinkedHashMap<String, Medicine> linkedMap =
                createLinkedHashMap(medicines);

        String[] keys = createLookupKeys(size);

        int repetitions = 5;

        // Warm-up
        measureHashLookup(hashMap, keys);
        measureTreeLookup(treeMap, keys);
        measureLinkedLookup(linkedMap, keys);

        double hashLookup =
                averageHashLookup(hashMap, keys, repetitions);

        double treeLookup =
                averageTreeLookup(treeMap, keys, repetitions);

        double linkedLookup =
                averageLinkedLookup(linkedMap, keys, repetitions);

        double hashReport =
                measureReport(hashMap, repetitions);

        double treeReport =
                measureReport(treeMap, repetitions);

        double linkedReport =
                measureReport(linkedMap, repetitions);

        System.out.println("100,000 LOOKUPS (Average of 5 runs)");

        System.out.printf("HashMap       : %.4f ms%n", hashLookup);
        System.out.printf("TreeMap       : %.4f ms%n", treeLookup);
        System.out.printf("LinkedHashMap : %.4f ms%n", linkedLookup);

        System.out.println("\nREPORT GENERATION (Average of 5 runs)");

        System.out.printf("HashMap       : %.4f ms%n", hashReport);
        System.out.printf("TreeMap       : %.4f ms%n", treeReport);
        System.out.printf("LinkedHashMap : %.4f ms%n", linkedReport);

        System.out.println("\nORDER VERIFICATION");

        System.out.println("HashMap code sorted: "
                + isCodeSorted(hashMap));

        System.out.println("TreeMap code sorted: "
                + isCodeSorted(treeMap));

        System.out.println("LinkedHashMap insertion ordered: "
                + isInsertionOrdered(linkedMap, medicines));

        System.out.println("\nFIRST FIVE MEDICINES IN TREE MAP:");

        int count = 0;

        for (Medicine medicine : treeMap.values()) {

            if (count++ == 5) break;

            System.out.println(medicine);
        }
    }

    public static void main(String[] args) {

        System.out.println("HOSPITAL PHARMACY INVENTORY");
        System.out.println("HASHMAP VS TREEMAP VS LINKEDHASHMAP");

        int[] sizes = {1000, 10000, 50000};

        for (int size : sizes) {
            runExperiment(size);
        }

        System.out.println("\n======================================");
        System.out.println("PHARMACY INVENTORY EXPERIMENT COMPLETED");
        System.out.println("======================================");
    }
}
