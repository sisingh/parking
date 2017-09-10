package com.gojek.parking;

import com.gojek.parking.exception.IllFormedCommand;
import com.gojek.parking.model.Vehicle;
import java.io.*;
import java.util.PriorityQueue;

/**
 *
 * @author siddharthasingh
 */
public class ParkingManager {

    private Vehicle[] slots = null;
    private PriorityQueue<Integer> priorityQueueToInsert = null;

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
//                handlePark(line, split);
                    break;
                case "leave":
//                handleLeave(line, split);
                    break;
                case "status":
//                handleStatus(line, split);
                    break;
                case "registration_numbers_for_cars_with_colour":
//                handleRegistrationNumbersForCarsWithColour(line, split);
                    break;
                case "slot_numbers_for_cars_with_colour":
//                handleSlotNumbersForCarsWithColour(line, split);
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

    private Vehicle[] getSlots() {
        return slots;
    }

    private PriorityQueue<Integer> getPriorityQueue() {
        return priorityQueueToInsert;
    }
}
