package com.atlaslogistics.carrier;

import com.atlaslogistics.model.Shipment;
import static com.atlaslogistics.constants.ShippingConstants.*;

// PlaneCarrier class inherits TransportCarrier class
public class PlaneCarrier extends TransportCarrier {

    // Variable to store airline code
    private String airlineCode;

    // Constructor with carrier ID, fuel percentage, and airline code
    public PlaneCarrier(
            String carrierId,
            double fuelPercent,
            String airlineCode) {

        // Calling parent constructor using super()
        super(carrierId, fuelPercent, PLANE_MAX_KG);

        // Initializing airline code
        this.airlineCode = airlineCode;
    }

    // Overloaded constructor with default airline code
    public PlaneCarrier(
            String carrierId,
            double fuelPercent) {

        // Calling another constructor using this()
        this(carrierId, fuelPercent, "ATLAS");
    }

    // Overloaded constructor with default fuel percentage
    public PlaneCarrier(String carrierId) {

        // Default fuel set to 100%
        this(carrierId, 100.0);
    }

    // Overriding deliver() method from TransportCarrier
    @Override
    public String deliver(Shipment shipment) {

        // Assertion check for null shipment
        assert shipment != null : "Shipment cannot be null";

        // Check whether plane can carry shipment weight
        if (!canCarry(shipment.getWeightKg()))

            // Return failure message if weight exceeds limit
            return "FAIL: Plane limit exceeded ("
                    + PLANE_MAX_KG
                    + " kg max)";

        // Reduce fuel percentage after dispatch
        fuelPercent -= 30.0;

        // Mark carrier as currently in service
        inService = true;

        // Update shipment status
        shipment.setStatus("In Transit");

        // Logging dispatch details
        logEvent(
                "Flight dispatched",

                "Flight  : "
                        + airlineCode
                        + "-"
                        + carrierId,

                "Cargo   : "
                        + shipment.getPackageId(),

                "To      : "
                        + shipment.getDestination(),

                "Payload : "
                        + shipment.getWeightKg()
                        + " kg",

                "Priority: "
                        + shipment.getPriority()
        );

        // Return successful dispatch message
        return "Plane "
                + carrierId
                + " ("
                + airlineCode
                + ") airborne → "
                + shipment.getPackageId();
    }

    // Overriding logEvent() method
    @Override
    public void logEvent(
            String event,
            String... details) {

        // Print main event information
        System.out.println(
                "[PLANE:"
                + carrierId
                + "/"
                + airlineCode
                + "] "
                + event
        );

        // Enhanced for-loop to print all details
        for (String d : details)

            System.out.println("   ✈ " + d);
    }

    // Overriding calcCharge() method
    @Override
    public double calcCharge(
            double distanceKm,
            double weightKg) {

        // Base charge calculation
        double base =
                BASE_RATE_PER_KM
                * distanceKm
                * weightKg
                * 3.5;

        // Extra surcharge for heavy cargo
        double heavySurcharge =
                weightKg > 1000
                        ? base * 0.15
                        : 0;

        // Return total delivery charge
        return base + heavySurcharge;
    }

    // Overriding getType() method
    @Override
    public String getType() {

        // Return carrier type
        return "Plane";
    }

    // Getter method for airline code
    public String getAirlineCode() {

        return airlineCode;
    }
}
