package com.atlaslogistics.carrier;

import com.atlaslogistics.model.Shipment;
import static com.atlaslogistics.constants.ShippingConstants.*;


public class VanCarrier extends TransportCarrier {

    private int numAxles;   //Van-specific, affects toll charges

    public VanCarrier(String carrierId, double fuelPercent, int numAxles) {
        super(carrierId, fuelPercent, VAN_MAX_KG);
        this.numAxles = numAxles;
    }

    public VanCarrier(String carrierId, double fuelPercent) {
        this(carrierId, fuelPercent, 2);
    }

    public VanCarrier(String carrierId) {

        this(carrierId, 100.0);
    }

    @Override
    public String deliver(Shipment shipment) {
        assert shipment != null : "Shipment cannot be null";

        if (!canCarry(shipment.getWeightKg()))
            return "FAIL: Van cannot carry " + shipment.getWeightKg() + " kg (max " + VAN_MAX_KG + " kg)";

        fuelPercent -= 12.0;
        inService    = true;
        shipment.setStatus("In Transit");

        logEvent("Van dispatched",
                 "Package : " + shipment.getPackageId(),
                 "To      : " + shipment.getDestination(),
                 "Weight  : " + shipment.getWeightKg() + " kg",
                 "Axles   : " + numAxles);

        return "Van " + carrierId + " dispatched → " + shipment.getPackageId();
    }

    @Override
    public void logEvent(String event, String... details) {
        System.out.println("[VAN:" + carrierId + "] " + event);
        for (String d : details)
            System.out.println("   ✦ " + d);
    }

    @Override
    public double calcCharge(double distanceKm, double weightKg) {
        double base     = BASE_RATE_PER_KM * distanceKm * weightKg;
        double axleFee  = numAxles > 2 ? base * 0.10 : 0;  //heavy vehicle surcharge
        return base + axleFee;
    }

    @Override
    public String getType() { return "Van"; }

    public int getNumAxles() { return numAxles; }
}
