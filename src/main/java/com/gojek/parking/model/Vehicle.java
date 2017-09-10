package com.gojek.parking.model;

/**
 *
 * @author siddharthasingh
 */
public class Vehicle {

    private final String registrationNumber;
    private final int slot;
    private final String colour;

    public enum REG_SLOT {
        REG, SLOT
    }

    public Vehicle(String registrationNumber, int slot, String colour) {
        this.registrationNumber = registrationNumber;
        this.slot = slot;
        this.colour = colour;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public int getSlot() {
        return slot;
    }

    public String getColour() {
        return colour;
    }
}
