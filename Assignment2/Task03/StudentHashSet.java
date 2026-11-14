
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

// Original faulty implementation
class FaultyStudent {
    String regNo;
    String name;

    FaultyStudent(String regNo, String name) {
        this.regNo = regNo;
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FaultyStudent)) {
            return false;
        }

        FaultyStudent other = (FaultyStudent) obj;
        return regNo.equals(other.regNo);
    }

    // hashCode() is intentionally missing
}

// Corrected implementation
class Student {
    private final String regNo;
    private final String name;

    Student(String regNo, String name) {
        this.regNo = regNo;
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Student)) return false;

        Student other = (Student) obj;
        return Objects.equals(regNo, other.regNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regNo);
    }

    @Override
    public String toString() {
        return regNo + " - " + name;
    }
}

// Mutable example
class MutableStudent {
    String regNo;

    MutableStudent(String regNo) {
        this.regNo = regNo;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MutableStudent)) return false;

        MutableStudent other = (MutableStudent) obj;
        return regNo.equals(other.regNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regNo);
    }
}

public class StudentHashSet {

    public static void main(String[] args) {

        System.out.println("HASHCODE AND EQUALS INVESTIGATION");
        System.out.println("=================================");

        // Test 1: Original faulty implementation
        Set<FaultyStudent> faultySet = new HashSet<>();

        faultySet.add(new FaultyStudent("192421099", "Arun"));
        faultySet.add(new FaultyStudent("192421099", "Arun Kumar"));

        System.out.println("\nTEST 1: WITHOUT hashCode()");
        System.out.println("Set size: " + faultySet.size());

        // Test 2: Corrected implementation
        Set<Student> correctedSet = new HashSet<>();

        correctedSet.add(new Student("192421099", "Arun"));
        correctedSet.add(new Student("192421099", "Arun Kumar"));

        System.out.println("\nTEST 2: WITH hashCode()");
        System.out.println("Unique students: " + correctedSet.size());
        System.out.println("Students: " + correctedSet);

        // Test 3: Different students and repeated scans
        Set<Student> attendance = new HashSet<>();

        attendance.add(new Student("REG101", "Asha"));
        attendance.add(new Student("REG102", "Bala"));
        attendance.add(new Student("REG101", "Asha Duplicate"));
        attendance.add(new Student("REG103", "Charan"));
        attendance.add(new Student("REG102", "Bala Duplicate"));

        System.out.println("\nTEST 3: MULTIPLE ATTENDANCE SCANS");
        System.out.println("Total scans: 5");
        System.out.println("Unique students: " + attendance.size());

        // Demonstrating modification of hashCode field
        Set<MutableStudent> mutableSet = new HashSet<>();

        MutableStudent student = new MutableStudent("REG201");
        mutableSet.add(student);

        System.out.println("\nMUTABLE FIELD DEMONSTRATION");
        System.out.println("Before modification: "
                + mutableSet.contains(student));

        student.regNo = "REG202";

        System.out.println("After modification: "
                + mutableSet.contains(student));

        System.out.println("\nInvestigation completed.");
    }
}