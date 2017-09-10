package com.gojek.parking;

import java.io.*;

/**
 *
 * @author siddharthasingh
 */
public class ParkingManager {

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
            return;
        }
        String[] split = line.split(" ");
        if (split == null || split.length == 0) {
            System.err.println("No tokens...");
        }
    }
}
