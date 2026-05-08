package com.atlaslogistics.carrier;

import com.atlaslogistics.model.Shipment;
import static com.atlaslogistics.constants.ShippingConstants.*;

import java.io.Serializable;

// Abstract parent class for all transport carriers
public abstract class TransportCarrier
        implements Serializable {

    // Serializable version ID
    private static final long serialVersionUID = 2L;

    // Carrier unique ID
    protected String carrierId;

    // Current fuel percentage
    protected double fuelPercent;

    // Maximum weight capacity in kilograms
    protected double maxCapacityKg;

    // Indicates whether carrier is currently busy
    protected boolean inService;

    // Constructor with all carrier details
    public TransportCarrier(
            String carrierId,
            double fuelPercent,
            double maxCapacityKg) {

        // Assertion check for carrier ID
        assert carrierId != null
                : "Carrier ID required";

        // Fuel percentage must be between 0 and 100
        assert fuelPercent >= 0
                && fuelPercent <= 100
                : "Fuel must be 0–100";

        // Capacity must be positive
        assert maxCapacityKg > 0
                : "Capacity must be positive";

        // Initializing variables
        this.carrierId = carrierId;

        this.fuelPercent = fuelPercent;

        this.maxCapacityKg = maxCapacityKg;

        // Initially carrier is not in service
        this.inService = false;
    }

    // Overloaded constructor with default fuel percentage
    public TransportCarrier(
            String carrierId,
            double maxCapacityKg) {

        // Default fuel set to 100%
        this(carrierId, 100.0, maxCapacityKg);
    }

    // Method to refuel carrier
    public void refuel(double amount) {

        // Refuel amount must be positive
        assert amount > 0
                : "Refuel amount must be positive";

        // Prevent fuel from exceeding 100%
        fuelPercent =
                Math.min(
                        100.0,
                        fuelPercent + amount
                );

        // Print refuel status
        System.out.println(
                "["
                + carrierId
                + "] Refuelled → "
                + fuelPercent
                + "%"
        );
    }

    // Method to check carrier availability
    public boolean isAvailable() {

        // Carrier must not be in service
        // Fuel must be greater than 10%
        return !inService
                && fuelPercent > 10.0;
    }

    // Method to check weight carrying capacity
    public boolean canCarry(double weightKg) {

        // Return true if weight is within limit
        return weightKg <= maxCapacityKg;
    }

    // Abstract method for delivery process
    public abstract String deliver(
            Shipment shipment);

    // Abstract method for event logging
    public abstract void logEvent(
            String event,
            String... details);

    // Abstract method for charge calculation
    public abstract double calcCharge(
            double distanceKm,
            double weightKg);

    // Abstract method to return carrier type
    public abstract String getType();

    // Getter method for carrier ID
    public String getCarrierId() {

        return carrierId;
    }

    // Getter method for fuel percentage
    public double getFuelPercent() {

        return fuelPercent;
    }

    // Getter method for maximum capacity
    public double getMaxCapacityKg() {

        return maxCapacityKg;
    }

    // Getter method for service status
    public boolean isInService() {

        return inService;
    }

    // Setter method for service status
    public void setInService(boolean v) {

        inService = v;
    }

    // Overriding toString() method
    @Override
    public String toString() {

        // Return formatted carrier information
        return "["
                + getType()
                + " "
                + carrierId
                + "]"

                + " Fuel:"
                + String.format("%.0f", fuelPercent)
                + "%"

                + " | Cap:"
                + maxCapacityKg
                + "kg"

                + " | "
                + (inService
                    ? "In Service"
                    : "Available");
    }
}
