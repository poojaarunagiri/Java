import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/*
 * Campus Smart-Parking System - simple console implementation.
 * Compile: javac CampusParkingApp.java
 * Run:     java CampusParkingApp
 */
abstract class Vehicle {
    private final String registrationNumber;
    protected Vehicle(String registrationNumber) {
        if (registrationNumber == null || registrationNumber.isBlank())
            throw new IllegalArgumentException("Registration number is required.");
        this.registrationNumber = registrationNumber.trim().toUpperCase();
    }
    public String getRegistrationNumber() { return registrationNumber; }
    public abstract double hourlyRate();
    @Override public boolean equals(Object obj) {
        return obj instanceof Vehicle
                && registrationNumber.equals(((Vehicle) obj).registrationNumber);
    }
    @Override public int hashCode() { return registrationNumber.hashCode(); }
    @Override public String toString() { return getClass().getSimpleName() + "(" + registrationNumber + ")"; }
}
interface Chargeable { double chargingFee(int kWh); }

class TwoWheeler extends Vehicle {
    TwoWheeler(String reg) { super(reg); }
    public double hourlyRate() { return 15.0; }
}
class Car extends Vehicle {
    Car(String reg) { super(reg); }
    public double hourlyRate() { return 30.0; }
}
class ElectricCar extends Vehicle implements Chargeable {
    ElectricCar(String reg) { super(reg); }
    public double hourlyRate() { return 50.0; }
    public double chargingFee(int kWh) { return Math.max(0, kWh) * 2.0; }
}
class ParkingSlot {
    private final int floorNumber, slotNumber;
    private Vehicle parkedVehicle;
    ParkingSlot(int floorNumber, int slotNumber) {
        this.floorNumber = floorNumber; this.slotNumber = slotNumber;
    }
    public boolean isFree() { return parkedVehicle == null; }
    public void park(Vehicle v) {
        if (!isFree()) throw new IllegalStateException("Slot is occupied.");
        parkedVehicle = Objects.requireNonNull(v);
    }
    public void release() { parkedVehicle = null; }
    public int getFloorNumber() { return floorNumber; }
    public int getSlotNumber() { return slotNumber; }
    public Vehicle getParkedVehicle() { return parkedVehicle; }
}
class ParkingTicket {
    private static long nextTicketNumber = 1000;
    private final long ticketNumber;
    private final Vehicle vehicle;
    private final ParkingSlot slot;
    private final LocalDateTime entryTime;
    private final boolean passHolder;

    ParkingTicket(Vehicle vehicle, ParkingSlot slot, boolean passHolder) {
        this.ticketNumber = nextTicketNumber++;
        this.vehicle = Objects.requireNonNull(vehicle);
        this.slot = Objects.requireNonNull(slot);
        this.entryTime = LocalDateTime.now();
        this.passHolder = passHolder;
    }
    public long getTicketNumber() { return ticketNumber; }
    public Vehicle getVehicle() { return vehicle; }
    public ParkingSlot getSlot() { return slot; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public boolean hasPass() { return passHolder; }
}
class ParkingFacility {
    private final ParkingSlot[][] slots;
    private final Map<String, ParkingTicket> activeTickets = new HashMap<>();

    ParkingFacility(int[] slotsPerFloor) {
        slots = new ParkingSlot[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            slots[f] = new ParkingSlot[slotsPerFloor[f]];
            for (int s = 0; s < slots[f].length; s++)
                slots[f][s] = new ParkingSlot(f, s);
        }
    }
    public ParkingTicket enter(Vehicle vehicle, boolean validPass) {
        String reg = vehicle.getRegistrationNumber();
        if (activeTickets.containsKey(reg))
            throw new IllegalArgumentException("This vehicle is already parked.");
        for (ParkingSlot[] floor : slots) {
            for (ParkingSlot slot : floor) {
                if (slot.isFree()) {
                    slot.park(vehicle);
                    ParkingTicket ticket = new ParkingTicket(vehicle, slot, validPass);
                    activeTickets.put(reg, ticket);
                    return ticket;
                }
            }
        }
        throw new IllegalStateException("Parking facility is full.");
    }
    public double exit(String registrationNumber, LocalDateTime exitTime, int chargingKWh) {
        ParkingTicket ticket = activeTickets.remove(registrationNumber.toUpperCase());
        if (ticket == null) throw new IllegalArgumentException("No active ticket found.");
        long minutes = Math.max(0, Duration.between(ticket.getEntryTime(), exitTime).toMinutes());
        double fee;
        if (ticket.hasPass()) {
            fee = 0.0;
        } else {
            int hours = minutes == 0 ? 0 : (int)((minutes + 59) / 60);
            fee = hours * ticket.getVehicle().hourlyRate();
            if (ticket.getVehicle() instanceof Chargeable c)
                fee += c.chargingFee(chargingKWh);
        }
        ticket.getSlot().release();
        return fee;
    }
    public int occupancy() { return activeTickets.size(); }
}
public class CampusParkingApp {
    public static void main(String[] args) {
        ParkingFacility facility = new ParkingFacility(new int[] {3, 2, 4});
        Vehicle car = new Car("TN01AB1234");
        ParkingTicket ticket = facility.enter(car, false);
        System.out.println("Ticket: " + ticket.getTicketNumber());
        System.out.println("Slot: floor " + ticket.getSlot().getFloorNumber()
                + ", slot " + ticket.getSlot().getSlotNumber());
        // Demonstration exit after 75 minutes using a supplied time for repeatable testing.
        LocalDateTime exitTime = ticket.getEntryTime().plusMinutes(75);
        System.out.println("Parking fee: Rs " + facility.exit("TN01AB1234", exitTime, 0));
        System.out.println("Current occupancy: " + facility.occupancy());
    }
}