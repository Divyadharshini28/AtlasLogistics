package com.atlaslogistics.carrier;

import com.atlaslogistics.model.Shipment;
import static com.atlaslogistics.constants.ShippingConstants.*;

// VanCarrier class inherits TransportCarrier class
public class VanCarrier extends TransportCarrier {

    // Number of axles in van
    // Used for toll and surcharge calculation
    private int numAxles;

    // Constructor with carrier ID, fuel percentage, and axle count
    public VanCarrier(
            String carrierId,
            double fuelPercent,
            int numAxles) {

        // Calling parent constructor using super()
        super(carrierId, fuelPercent, VAN_MAX_KG);

        // Initializing axle count
        this.numAxles = numAxles;
    }

    // Overloaded constructor with default axle count
    public VanCarrier(
            String carrierId,
            double fuelPercent) {

        // Default axle count = 2
        this(carrierId, fuelPercent, 2);
    }

    // Overloaded constructor with default fuel percentage
    public VanCarrier(String carrierId) {

        // Default fuel set to 100%
        this(carrierId, 100.0);
    }

    // Overriding deliver() method from TransportCarrier
    @Override
    public String deliver(Shipment shipment) {

        // Assertion check for null shipment
        assert shipment != null
                : "Shipment cannot be null";

        // Check whether van can carry shipment weight
        if (!canCarry(shipment.getWeightKg()))

            // Return failure message if weight exceeds limit
            return "FAIL: Van cannot carry "
                    + shipment.getWeightKg()
                    + " kg (max "
                    + VAN_MAX_KG
                    + " kg)";

        // Reduce fuel percentage after dispatch
        fuelPercent -= 12.0;

        // Mark carrier as currently in service
        inService = true;

        // Update shipment status
        shipment.setStatus("In Transit");

        // Logging dispatch details
        logEvent(
                "Van dispatched",

                "Package : "
                        + shipment.getPackageId(),

                "To      : "
                        + shipment.getDestination(),

                "Weight  : "
                        + shipment.getWeightKg()
                        + " kg",

                "Axles   : "
                        + numAxles
        );

        // Return successful dispatch message
        return "Van "
                + carrierId
                + " dispatched → "
                + shipment.getPackageId();
    }

    // Overriding logEvent() method
    @Override
    public void logEvent(
            String event,
            String... details) {

        // Print main event information
        System.out.println(
                "[VAN:"
                + carrierId
                + "] "
                + event
        );

        // Enhanced for-loop to print all details
        for (String d : details)

            System.out.println("   ✦ " + d);
    }

    // Overriding calcCharge() method
    @Override
    public double calcCharge(
            double distanceKm,
            double weightKg) {

        // Base delivery charge calculation
        double base =
                BASE_RATE_PER_KM
                * distanceKm
                * weightKg;

        // Extra surcharge for heavy vehicles
        double axleFee =
                numAxles > 2
                        ? base * 0.10
                        : 0;

        // Return final delivery charge
        return base + axleFee;
    }

    // Overriding getType() method
    @Override
    public String getType() {

        // Return carrier type
        return "Van";
    }

    // Getter method for axle count
    public int getNumAxles() {

        return numAxles;
    }
}
