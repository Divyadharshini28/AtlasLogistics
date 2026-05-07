package com.atlaslogistics.carrier;

import com.atlaslogistics.model.Shipment;
import static com.atlaslogistics.constants.ShippingConstants.*;

public class PlaneCarrier extends TransportCarrier {

    private String airlineCode;

    public PlaneCarrier(String carrierId, double fuelPercent, String airlineCode) {
        super(carrierId, fuelPercent, PLANE_MAX_KG);
        this.airlineCode = airlineCode;
    }

    public PlaneCarrier(String carrierId, double fuelPercent) {
        this(carrierId, fuelPercent, "ATLAS");
    }

    public PlaneCarrier(String carrierId) {
        this(carrierId, 100.0);
    }

    @Override
    public String deliver(Shipment shipment) {
        assert shipment != null : "Shipment cannot be null";

        if (!canCarry(shipment.getWeightKg()))
            return "FAIL: Plane limit exceeded (" + PLANE_MAX_KG + " kg max)";

        fuelPercent -= 30.0;
        inService    = true;
        shipment.setStatus("In Transit");

        logEvent("Flight dispatched",
                 "Flight  : " + airlineCode + "-" + carrierId,
                 "Cargo   : " + shipment.getPackageId(),
                 "To      : " + shipment.getDestination(),
                 "Payload : " + shipment.getWeightKg() + " kg",
                 "Priority: " + shipment.getPriority());

        return "Plane " + carrierId + " (" + airlineCode + ") airborne → " + shipment.getPackageId();
    }

    @Override
    public void logEvent(String event, String... details) {
        System.out.println("[PLANE:" + carrierId + "/" + airlineCode + "] " + event);
        for (String d : details)
            System.out.println("   ✈ " + d);
    }

    @Override
    public double calcCharge(double distanceKm, double weightKg) {
        double base        = BASE_RATE_PER_KM * distanceKm * weightKg * 3.5;
        double heavySurcharge = weightKg > 1000 ? base * 0.15 : 0;
        return base + heavySurcharge;
    }

    @Override
    public String getType() { return "Plane"; }

    public String getAirlineCode() { return airlineCode; }
}
