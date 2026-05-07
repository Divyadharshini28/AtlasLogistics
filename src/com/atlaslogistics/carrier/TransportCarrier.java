package com.atlaslogistics.carrier;

import com.atlaslogistics.model.Shipment;
import static com.atlaslogistics.constants.ShippingConstants.*;

import java.io.Serializable;

public abstract class TransportCarrier implements Serializable {
    private static final long serialVersionUID = 2L;

    protected String  carrierId;
    protected double  fuelPercent;
    protected double  maxCapacityKg;
    protected boolean inService;

    public TransportCarrier(String carrierId, double fuelPercent, double maxCapacityKg) {
        assert carrierId     != null          : "Carrier ID required";
        assert fuelPercent   >= 0
            && fuelPercent   <= 100           : "Fuel must be 0–100";
        assert maxCapacityKg > 0              : "Capacity must be positive";

        this.carrierId      = carrierId;
        this.fuelPercent    = fuelPercent;
        this.maxCapacityKg  = maxCapacityKg;
        this.inService      = false;
    }

    public TransportCarrier(String carrierId, double maxCapacityKg) {
        this(carrierId, 100.0, maxCapacityKg);
    }



    public void refuel(double amount) {
        assert amount > 0 : "Refuel amount must be positive";
        fuelPercent = Math.min(100.0, fuelPercent + amount);
        System.out.println("[" + carrierId + "] Refuelled → " + fuelPercent + "%");
    }

    public boolean isAvailable() {
        return !inService && fuelPercent > 10.0;
    }

    public boolean canCarry(double weightKg) {
        return weightKg <= maxCapacityKg;
    }


    public abstract String deliver(Shipment shipment);


    public abstract void logEvent(String event, String... details);

    public abstract double calcCharge(double distanceKm, double weightKg);

    public abstract String getType();

    public String  getCarrierId()          { return carrierId;     }
    public double  getFuelPercent()        { return fuelPercent;   }
    public double  getMaxCapacityKg()      { return maxCapacityKg; }
    public boolean isInService()           { return inService;     }
    public void    setInService(boolean v) { inService = v;        }

    @Override
    public String toString() {
        return "[" + getType() + " " + carrierId + "]"
             + " Fuel:" + String.format("%.0f", fuelPercent) + "%"
             + " | Cap:" + maxCapacityKg + "kg"
             + " | " + (inService ? "In Service" : "Available");
    }
}
