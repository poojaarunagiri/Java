import java.util.Arrays;

public class ParkingFacility {
    // slots[floor][slot] stores a vehicle number; null means free.
    private final String[][] slots;

    public ParkingFacility(int[] slotsPerFloor) {
        slots = new String[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            slots[f] = new String[slotsPerFloor[f]];
        }
    }

    // Returns {floor, slot} for the first free slot, or null if full.
    public int[] allocate(String vehicleNo) {
        if (vehicleNo == null || vehicleNo.isBlank()) return null;
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                if (slots[f][s] == null) {
                    slots[f][s] = vehicleNo;
                    return new int[] {f, s};
                }
            }
        }
        return null;
    }

    // Frees a slot. Invalid or already-free positions return false.
    public boolean release(int floor, int slot) {
        if (floor < 0 || floor >= slots.length) return false;
        if (slot < 0 || slot >= slots[floor].length) return false;
        if (slots[floor][slot] == null) return false;
        slots[floor][slot] = null;
        return true;
    }

    public static void main(String[] args) {
        ParkingFacility p = new ParkingFacility(new int[] {2, 1});
        System.out.println(Arrays.toString(p.allocate("TN01AB1234")));
        System.out.println(Arrays.toString(p.allocate("TN02CD5678")));
        System.out.println(Arrays.toString(p.allocate("TN03EF9012")));
        System.out.println(p.allocate("TN04GH3456"));
        System.out.println(p.release(0, 1) + " " + p.release(0, 1) + " "
                + p.release(5, 0));
        System.out.println(Arrays.toString(p.allocate("TN04GH4444")));
    }
}