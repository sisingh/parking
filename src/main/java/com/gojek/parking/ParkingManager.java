package com.gojek.parking;

import com.gojek.parking.exception.IllFormedCommand;
import com.gojek.parking.model.Vehicle;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.PriorityQueue;

/**
 *
 * @author siddharthasingh
 */
public class ParkingManager {

    private Vehicle[] slots = null;
    private PriorityQueue<Integer> priorityQueueToInsert = null;

    private final HashMap<String, HashMap<Vehicle.REG_SLOT, LinkedHashSet<String>>> colorVehicles = new HashMap<>();
    private final HashMap<String, Integer> registrationSlot = new HashMap<>();

    private Vehicle[] getSlots() {
        return slots;
    }

    private PriorityQueue<Integer> getPriorityQueue() {
        return priorityQueueToInsert;
    }

    private HashMap<String, HashMap<Vehicle.REG_SLOT, LinkedHashSet<String>>> getColorVehicles() {
        return colorVehicles;
    }

    private HashMap<String, Integer> getRegistrationSlot() {
        return registrationSlot;
    }


    public static void main(String[] args) throws FileNotFoundException {
        if (args == null || args[0].trim().isEmpty()) {
            throw new FileNotFoundException("File name not mentioned or null");
        }
        new ParkingManager().parseFile(args[0]);
    }

    private void parseFile(String file) throws FileNotFoundException {
        if (file == null || file.trim().isEmpty()) {
            throw new FileNotFoundException("File name not mentioned or null");
        }
        Reader in = null;
        try {
            in = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(in);
            bufReader.lines().forEach(this::parseALine);
        } catch (FileNotFoundException ex) {
            System.out.println("Exception " + ex.getMessage());
            throw ex;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    System.out.println("Exception " + ex.getMessage());
                }
            }
        }
    }

    private void parseALine(String line) {
        if (line.trim().isEmpty()) {
            System.err.println("Empty line...");
            throw new RuntimeException("Empty line...");
        }
        String[] split = line.split(" ");
        if (split == null || split.length == 0) {
            System.err.println("No tokens...");
            throw new RuntimeException("Empty line...");
        }
        if (handleCases(line, split) == false) {
            throw new RuntimeException();
        }
    }

    private boolean handleCases(String line, String[] split) {
        try {
            switch (split[0]) {
                case "create_parking_lot":
                    handleCreateParkingLot(line, split);
                    break;
                case "park":
                    handlePark(line, split);
                    break;
                case "leave":
                    handleLeave(line, split);
                    break;
                case "status":
                    handleStatus(line, split);
                    break;
                case "registration_numbers_for_cars_with_colour":
                    handleRegistrationNumbersForCarsWithColour(line, split);
                    break;
                case "slot_numbers_for_cars_with_colour":
                    handleSlotNumbersForCarsWithColour(line, split);
                    break;
                case "slot_number_for_registration_number":
//                handleSlotNumberForRegistrationNumber(line, split);
                    break;
                default:
                    System.err.println("Unknown token : " + split[0]);
                    return false;
            }
        } catch (IllFormedCommand ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        return true;
    }

    private void handleLeave(String line, String[] split) throws IllFormedCommand {
        if (slots == null) {
            throw new IllFormedCommand("create_parking_lot should've been called before");
        }
        if (split.length != 2) {
            throw new IllFormedCommand("Illformed leave command " + line);
        }
        Integer slot = null;
        try {
            slot = Integer.valueOf(split[1]);
        } catch (NumberFormatException e) {
            System.err.println("leave command should contain a proper integer value : " + line);
            throw new IllFormedCommand("leave command should contain a proper integer value : " + line);
        }
        validateSlots(slot);
        clearSlot(slot);
        System.out.println("Slot number " + slot + " is free");
    }

    private void clearSlot(Integer slot) {
        Vehicle vehicle = slots[slot - 1];
        //clear from all data structures
        HashMap<Vehicle.REG_SLOT, LinkedHashSet<String>> colouredVehicle = colorVehicles.get(vehicle.getColour());
        if (colouredVehicle == null || colouredVehicle.isEmpty()) {
            return;
        }
        //Get registration deleted
        LinkedHashSet<String> registeredVehicles = colouredVehicle.get(Vehicle.REG_SLOT.REG);
        if (registeredVehicles == null
            || registeredVehicles.isEmpty()
            || !registeredVehicles.contains(vehicle.getRegistrationNumber())) {
            return;
        }
        if (registeredVehicles.contains(vehicle.getRegistrationNumber())) {
            registeredVehicles.remove(vehicle.getRegistrationNumber());
        }
        //Get slots deleted
        LinkedHashSet<String> slottedVehicles = colouredVehicle.get(Vehicle.REG_SLOT.SLOT);
        if (slottedVehicles == null
            || slottedVehicles.isEmpty()
            || !slottedVehicles.contains(String.valueOf(vehicle.getSlot()))) {
            return;
        }
        if (slottedVehicles.contains(String.valueOf(vehicle.getSlot()))) {
            slottedVehicles.remove(String.valueOf(vehicle.getSlot()));
        }

        if (registrationSlot.containsKey(vehicle.getRegistrationNumber())) {
            registrationSlot.remove(vehicle.getRegistrationNumber());
        }
        //clear from actual slots as well, which is '0' indexed
        slots[slot - 1] = null;

        //And make this slot available
        priorityQueueToInsert.add(slot - 1);
    }

    private void validateSlots(Integer slot) {
        if (slot == null || slot < 1 || slot > this.slots.length) {
            System.err.println("Invalid slot value : " + slot);
            System.exit(1);
        }
    }
    private void handleCreateParkingLot(String line, String[] split) throws IllFormedCommand {
        if (split.length != 2) {
            throw new IllFormedCommand("Ill formed command " + line);
        }
        if (slots == null) {
            Integer n = null;
            try {
                n = Integer.parseInt(split[1]);
            } catch (NumberFormatException e) {
                String errorMessage = "NumberFormatException in parsing slots : " + e.getMessage();
                throw new IllFormedCommand(errorMessage + " : " + line);
            }
            slots = new Vehicle[n];
            priorityQueueToInsert = new PriorityQueue<>(n);
            for (int i = 0; i < n; ++i) {
                priorityQueueToInsert.add(i);
            }
        } else {
            throw new IllFormedCommand("create_parking_lot occurred multiple times");
        }
        System.out.println("Created a parking lot with " + slots.length + (slots.length > 1 ? " slots" : " slot"));
    }


    private void handlePark(String line, String[] split) throws IllFormedCommand {
        if (slots == null) {
            throw new IllFormedCommand("create_parking_lot should've been called before");
        }
        if (split.length != 3) {
            throw new IllFormedCommand("Illformed park command " + line);

        }
        Integer slot = getSlot();
        if (slot == null) {
            System.out.println("Sorry, parking lot is full");
            return;
        }
        String regisrationNumber = split[1];
        String colour = split[2];
        Vehicle vehicle = new Vehicle(regisrationNumber, slot, colour);
        //Fixed size slots with Vehicle
        slots[slot] = vehicle;
        if (registrationSlot.get(regisrationNumber) != null) {
            throw new IllFormedCommand("Car is already parked with registration number " + regisrationNumber);
        } else {
            // Key == Registration Number, Value = Slot number
            registrationSlot.put(regisrationNumber, slot);
            // Key = Colour, HashMap of REG and SLOT with their linked hash set of numbers
            HashMap<Vehicle.REG_SLOT, LinkedHashSet<String>> regAndSlot = colorVehicles.get(colour);
            if (regAndSlot == null) {
                regAndSlot = new HashMap<>();
                LinkedHashSet<String> registrationNumbers = new LinkedHashSet<>();
                registrationNumbers.add(vehicle.getRegistrationNumber());
                LinkedHashSet<String> slotNumbers = new LinkedHashSet<>();
                slotNumbers.add(String.valueOf(vehicle.getSlot()));
                regAndSlot.put(Vehicle.REG_SLOT.REG, registrationNumbers);
                regAndSlot.put(Vehicle.REG_SLOT.SLOT, slotNumbers);
                colorVehicles.put(vehicle.getColour(), regAndSlot);
            } else {
                LinkedHashSet<String> registrationNumbers = regAndSlot.get(Vehicle.REG_SLOT.REG);
                if (registrationNumbers.contains(vehicle.getRegistrationNumber())) {
                    throw new IllFormedCommand("Duplicate registration found... Invalid record");
                }
                registrationNumbers.add(vehicle.getRegistrationNumber());
                LinkedHashSet<String> slotNumbers = regAndSlot.get(Vehicle.REG_SLOT.SLOT);
                if (slotNumbers.contains(String.valueOf(vehicle.getSlot()))) {
                    throw new IllFormedCommand("Duplicate slot found... Invalid record");
                }
                slotNumbers.add(String.valueOf(vehicle.getSlot()));
                colorVehicles.put(vehicle.getColour(), regAndSlot);
            }
        }
        System.out.println("Allocated slot number: " + (slot + 1));
    }

    private Integer getSlot() {
        return priorityQueueToInsert.poll();
    }

    private void handleStatus(String line, String[] split) throws IllFormedCommand {
        if (slots == null) {
            throw new IllFormedCommand("create_parking_lot should've been called before");
        }
        if (split.length != 1) {
            throw new IllFormedCommand("Illformed status command " + line);
        }

        for (int i = 0; i < slots.length; ++i) {
            if (slots[i] != null) {
                if (i == 0) {
                    System.out.println("Slot No.\t Registration No\t Colour");
                }
                System.out.println((i + 1) + "\t" + slots[i].getRegistrationNumber() + "\t"
                    + slots[i].getColour());
            }
        }
    }

    private void handleRegistrationNumbersForCarsWithColour(String line, String[] split) throws IllFormedCommand {
        if (slots == null) {
            throw new IllFormedCommand("create_parking_lot should've been called before");
        }
        if (split.length != 2) {
            throw new IllFormedCommand("Illformed registration_numbers_for_cars_with_colour command " + line);
        }
        HashMap<Vehicle.REG_SLOT, LinkedHashSet<String>> colouredVehicles = colorVehicles.get(split[1]);
        if (colouredVehicles == null || colouredVehicles.isEmpty()) {
            return;
        }
        LinkedHashSet<String> registrationNumbers = colouredVehicles.get(Vehicle.REG_SLOT.REG);
        if (registrationNumbers == null || registrationNumbers.isEmpty()) {
            return;
        }

        Iterator<String> iterator = registrationNumbers.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            System.out.print(iterator.next());
            ++i;
            if (i < registrationNumbers.size()) {
                System.out.print(", ");
            }
        }
        System.out.println("");
    }

    private void handleSlotNumbersForCarsWithColour(String line, String[] split) throws IllFormedCommand {
        if (slots == null) {
            throw new IllFormedCommand("create_parking_lot should've been called before");
        }
        if (split.length != 2) {
            throw new IllFormedCommand("Illformed slot_numbers_for_cars_with_colour command " + line);
        }
        HashMap<Vehicle.REG_SLOT, LinkedHashSet<String>> colouredVehicles = colorVehicles.get(split[1]);
        if (colouredVehicles == null || colouredVehicles.isEmpty()) {
            return;
        }
        LinkedHashSet<String> slotNumbers = colouredVehicles.get(Vehicle.REG_SLOT.SLOT);
        if (slotNumbers == null || slotNumbers.isEmpty()) {
            return;
        }
        Iterator<String> iterator = slotNumbers.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            System.out.print(Integer.parseInt(iterator.next()) + 1);
            ++i;
            if (i < slotNumbers.size()) {
                System.out.print(", ");
            }
        }
        System.out.println("");
    }
}
