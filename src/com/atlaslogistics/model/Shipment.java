package com.atlaslogistics.model;

import com.atlaslogistics.constants.ShippingConstants;
import static com.atlaslogistics.constants.ShippingConstants.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Shipment class implements Serializable
// Allows shipment objects to be saved into files
public class Shipment implements Serializable {

    // Serializable version ID
    private static final long serialVersionUID = 1L;

    // Unique package ID
    private String packageId;

    // Shipment destination
    private String destination;

    // Weight of shipment in kilograms
    private double weightKg;

    // Shipment priority level
    private String priority;

    // Current shipment status
    private String status;

    // Type of carrier assigned
    private String carrierType;

    // List storing package item names
    private List<String> packageItems;

    // Main constructor with all shipment details
    public Shipment(
            String packageId,
            String destination,
            double weightKg,
            String priority,
            String carrierType) {

        // Assertion check for package ID
        assert packageId != null
                && !packageId.isEmpty()
                : "Package ID cannot be empty";

        // Assertion check for destination
        assert destination != null
                && !destination.isEmpty()
                : "Destination cannot be empty";

        // Weight must be positive
        assert weightKg > 0
                : "Weight must be positive";

        // Initializing variables
        this.packageId = packageId;

        this.destination = destination;

        this.weightKg = weightKg;

        this.priority = priority;

        this.carrierType = carrierType;

        // Default shipment status
        this.status = "Pending";

        // Creating empty package item list
        this.packageItems = new ArrayList<>();
    }

    // Overloaded constructor with default priority
    public Shipment(
            String packageId,
            String destination,
            double weightKg,
            String carrierType) {

        // Default priority = Standard
        this(
                packageId,
                destination,
                weightKg,
                "Standard",
                carrierType
        );
    }

    // Overloaded constructor with automatic carrier selection
    public Shipment(
            String packageId,
            String destination,
            double weightKg) {

        // Carrier type selected using ternary operator
        this(
                packageId,
                destination,
                weightKg,

                weightKg <= BIKE_MAX_KG
                        ? "Bike"

                        : weightKg <= VAN_MAX_KG
                        ? "Van"

                        : "Plane"
        );
    }

    // Method to load package items
    public void loadPackages(String... items) {

        // Enhanced for-loop for item iteration
        for (String item : items) {

            // Ignore null or empty item names
            if (item != null
                    && !item.trim().isEmpty())

                // Add cleaned item into list
                packageItems.add(item.trim());
        }
    }

    // Getter method for package ID
    public String getPackageId() {

        return packageId;
    }

    // Getter method for destination
    public String getDestination() {

        return destination;
    }

    // Getter method for weight
    public double getWeightKg() {

        return weightKg;
    }

    // Getter method for priority
    public String getPriority() {

        return priority;
    }

    // Getter method for shipment status
    public String getStatus() {

        return status;
    }

    // Getter method for carrier type
    public String getCarrierType() {

        return carrierType;
    }

    // Getter method for package item list
    public List<String> getItems() {

        return packageItems;
    }

    // Setter method for shipment status
    public void setStatus(String status) {

        this.status = status;
    }

    // Setter method for carrier type
    public void setCarrierType(String carrierType) {

        this.carrierType = carrierType;
    }

    // Overriding toString() method
    @Override
    public String toString() {

        // Return formatted shipment details
        return packageId
                + " → "
                + destination

                + " ("
                + weightKg
                + " kg) ["

                + status
                + "]";
    }
}
