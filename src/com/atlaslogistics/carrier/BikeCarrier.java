package com.atlaslogistics.carrier;

import com.atlaslogistics.model.Shipment;
import static com.atlaslogistics.constants.ShippingConstants.*;


public class BikeCarrier extends TransportCarrier {

    // Constructor with custom fuel percentage
    public BikeCarrier(String carrierId, double fuelPercent) {

        // Calling parent constructor using super()
        // Passing carrier ID, fuel percentage, and bike max weight
        super(carrierId, fuelPercent, BIKE_MAX_KG);
    }

    // Constructor with default fuel percentage
    public BikeCarrier(String carrierId) {

        // Calling another constructor in same class
        // Default fuel is set to 100%
        this(carrierId, 100.0);
    }


    // Overriding deliver() method from TransportCarrier
    @Override
    public String deliver(Shipment shipment) {

        // Assertion check to ensure shipment is not null
        assert shipment != null : "Shipment cannot be null";

        // Checking whether bike can carry shipment weight
        if (!canCarry(shipment.getWeightKg()))

            // Return failure message if overweight
            return "FAIL: Bike cannot carry "
                    + shipment.getWeightKg()
                    + " kg (max "
                    + BIKE_MAX_KG
                    + " kg)";

        // Reduce fuel after dispatch
        fuelPercent -= 5.0;

        // Mark carrier as in service
        inService = true;

        // Update shipment status
        shipment.setStatus("In Transit");

        // Logging delivery details using varargs
        logEvent(
                "Delivery started",

                "Package : "
                        + shipment.getPackageId(),

                "To      : "
                        + shipment.getDestination(),

                "Weight  : "
                        + shipment.getWeightKg()
                        + " kg"
        );

        // Return dispatch confirmation message
        return "Bike "
                + carrierId
                + " dispatched → "
                + shipment.getPackageId();
    }

    // Overriding logEvent() method
    @Override
    public void logEvent(String event, String... details) {

        // Print main event heading
        System.out.println(
                "[BIKE:"
                        + carrierId
                        + "] "
                        + event
        );

        // Loop through all detail messages
        for (String d : details)

            // Print formatted detail line
            System.out.println("   ↳ " + d);
    }

    // Overriding charge calculation method
    @Override
    public double calcCharge(
            double distanceKm,
            double weightKg) {

        // Bike delivery uses 50% base rate
        return BASE_RATE_PER_KM
                * distanceKm
                * 0.5;
    }

    // Overriding method to return carrier type
    @Override
    public String getType() {

        return "Bike";
    }
}
