
import java.util.*;

public class StringBenchmark {

    static volatile long resultSink;

    static String buildUsingString(int fields) {
        String ticket = "";

        for (int i = 1; i <= fields; i++) {
            ticket = ticket + "Field" + i + ":Value" + i + " | ";
        }

        return ticket;
    }

    static String buildUsingStringBuilder(int fields) {
        StringBuilder ticket = new StringBuilder();

        for (int i = 1; i <= fields; i++) {
            ticket.append("Field").append(i)
                  .append(":Value").append(i).append(" | ");
        }

        return ticket.toString();
    }

    static String buildUsingStringBuffer(int fields) {
        StringBuffer ticket = new StringBuffer();

        for (int i = 1; i <= fields; i++) {
            ticket.append("Field").append(i)
                  .append(":Value").append(i).append(" | ");
        }

        return ticket.toString();
    }

    static long testString(int tickets, int fields) {
        long checksum = 0;

        for (int i = 0; i < tickets; i++) {
            checksum += buildUsingString(fields).length();
        }

        resultSink = checksum;
        return checksum;
    }

    static long testBuilder(int tickets, int fields) {
        long checksum = 0;

        for (int i = 0; i < tickets; i++) {
            checksum += buildUsingStringBuilder(fields).length();
        }

        resultSink = checksum;
        return checksum;
    }

    static long testBuffer(int tickets, int fields) {
        long checksum = 0;

        for (int i = 0; i < tickets; i++) {
            checksum += buildUsingStringBuffer(fields).length();
        }

        resultSink = checksum;
        return checksum;
    }

    static double benchmark(int method, int tickets,
                            int fields, int repetitions) {

        long totalTime = 0;

        for (int r = 0; r < repetitions; r++) {
            long start = System.nanoTime();

            if (method == 1) {
                testString(tickets, fields);
            } else if (method == 2) {
                testBuilder(tickets, fields);
            } else {
                testBuffer(tickets, fields);
            }

            long end = System.nanoTime();
            totalTime += (end - start);
        }

        return totalTime / (double) repetitions / 1_000_000.0;
    }

    public static void main(String[] args) {

        int[][] workloads = {
            {1, 5},
            {1000, 10},
            {10000, 50}
        };

        String[] names = {
            "Short Ticket",
            "Medium Workload",
            "Heavy Workload"
        };

        int warmup = 2;
        int repetitions = 5;

        System.out.println("RAILWAY E-TICKET STRING BENCHMARK");
        System.out.println("---------------------------------");

        for (int w = 0; w < workloads.length; w++) {

            int tickets = workloads[w][0];
            int fields = workloads[w][1];

            System.out.println("\nWorkload: " + names[w]);
            System.out.println("Tickets: " + tickets);
            System.out.println("Fields per ticket: " + fields);

            // Warm-up
            for (int i = 0; i < warmup; i++) {
                testString(tickets, fields);
                testBuilder(tickets, fields);
                testBuffer(tickets, fields);
            }

            double stringTime =
                benchmark(1, tickets, fields, repetitions);

            double builderTime =
                benchmark(2, tickets, fields, repetitions);

            double bufferTime =
                benchmark(3, tickets, fields, repetitions);

            System.out.printf(
                "String (+)       : %.4f ms%n", stringTime);

            System.out.printf(
                "StringBuilder    : %.4f ms%n", builderTime);

            System.out.printf(
                "StringBuffer     : %.4f ms%n", bufferTime);

            System.out.println(
                "Intermediate String estimate (String +): "
                + (long) tickets * (fields - 1));

            System.out.println(
                "Final ticket Strings created per run: " + tickets);
        }

        System.out.println("\nBenchmark completed.");
    }
}