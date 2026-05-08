package com.atlaslogistics.carrier;

import com.atlaslogistics.model.Shipment;
import static com.atlaslogistics.constants.ShippingConstants.*;

// BikeCarrier class inherits TransportCarrier class
public class BikeCarrier extends TransportCarrier {

    // Constructor with carrier ID and fuel percentage
    public BikeCarrier(String carrierId, double fuelPercent) {

        // Calling parent constructor using super()
        // BIKE_MAX_KG imported using static import
        super(carrierId, fuelPercent, BIKE_MAX_KG);
    }

    // Overloaded constructor with default fuel = 100%
    public BikeCarrier(String carrierId) {

        // Calling another constructor using this()
        this(carrierId, 100.0);
    }

    // Overriding deliver() method from TransportCarrier
    @Override
    public String deliver(Shipment shipment) {

        // Assertion check for null shipment
        assert shipment != null : "Shipment cannot be null";

        // Checking whether bike can carry shipment weight
        if (!canCarry(shipment.getWeightKg()))

            // Return failure message if weight exceeds limit
            return "FAIL: Bike cannot carry "
                    + shipment.getWeightKg()
                    + " kg (max "
                    + BIKE_MAX_KG
                    + " kg)";

        // Reduce fuel percentage after dispatch
        fuelPercent -= 5.0;

        // Mark carrier as currently in service
        inService = true;

        // Update shipment status
        shipment.setStatus("In Transit");

        // Logging delivery details
        logEvent(
                "Delivery started",

                "Package : " + shipment.getPackageId(),

                "To      : " + shipment.getDestination(),

                "Weight  : " + shipment.getWeightKg() + " kg"
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

        // Print main event information
        System.out.println(
                "[BIKE:"
                + carrierId
                + "] "
                + event
        );

        // Enhanced for-loop for printing all details
        for (String d : details)

            System.out.println("   ↳ " + d);
    }

    // Overriding calcCharge() method
    @Override
    public double calcCharge(
            double distanceKm,
            double weightKg) {

        // Bike delivery uses 50% of base rate
        return BASE_RATE_PER_KM
                * distanceKm
                * 0.5;
    }

    // Overriding getType() method
    @Override
    public String getType() {

        // Return carrier type
        return "Bike";
    }
}
