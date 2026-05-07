package com.atlaslogistics.carrier;

import com.atlaslogistics.model.Shipment;
import static com.atlaslogistics.constants.ShippingConstants.*;


public class BikeCarrier extends TransportCarrier {

    public BikeCarrier(String carrierId, double fuelPercent) {
        super(carrierId, fuelPercent, BIKE_MAX_KG);   // static import
    }

    public BikeCarrier(String carrierId) {
        this(carrierId, 100.0);
    }


    @Override
    public String deliver(Shipment shipment) {
        assert shipment != null : "Shipment cannot be null";

        if (!canCarry(shipment.getWeightKg()))
            return "FAIL: Bike cannot carry " + shipment.getWeightKg() + " kg (max " + BIKE_MAX_KG + " kg)";

        fuelPercent -= 5.0;
        inService    = true;
        shipment.setStatus("In Transit");

        logEvent("Delivery started",
                 "Package : " + shipment.getPackageId(),
                 "To      : " + shipment.getDestination(),
                 "Weight  : " + shipment.getWeightKg() + " kg");

        return "Bike " + carrierId + " dispatched → " + shipment.getPackageId();
    }

    @Override
    public void logEvent(String event, String... details) {
        System.out.println("[BIKE:" + carrierId + "] " + event);
        for (String d : details)
            System.out.println("   ↳ " + d);
    }

    @Override
    public double calcCharge(double distanceKm, double weightKg) {
        return BASE_RATE_PER_KM * distanceKm * 0.5;
    }

    @Override
    public String getType() { return "Bike"; }
}
