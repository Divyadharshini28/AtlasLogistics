package com.atlaslogistics.model;

import com.atlaslogistics.constants.ShippingConstants;
import static com.atlaslogistics.constants.ShippingConstants.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Shipment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String       packageId;
    private String       destination;
    private double       weightKg;
    private String       priority;
    private String       status;
    private String       carrierType;
    private List<String> packageItems;

    public Shipment(String packageId, String destination,
                    double weightKg, String priority, String carrierType) {
        assert packageId   != null && !packageId.isEmpty()    : "Package ID cannot be empty";
        assert destination != null && !destination.isEmpty()  : "Destination cannot be empty";
        assert weightKg    > 0                                : "Weight must be positive";

        this.packageId    = packageId;
        this.destination  = destination;
        this.weightKg     = weightKg;
        this.priority     = priority;
        this.carrierType  = carrierType;
        this.status       = "Pending";
        this.packageItems = new ArrayList<>();
    }

    public Shipment(String packageId, String destination,
                    double weightKg, String carrierType) {
        this(packageId, destination, weightKg, "Standard", carrierType);
    }

    public Shipment(String packageId, String destination, double weightKg) {
        this(packageId, destination, weightKg,
             weightKg <= BIKE_MAX_KG  ? "Bike"  :
             weightKg <= VAN_MAX_KG   ? "Van"   : "Plane");
    }

    public void loadPackages(String... items) {
        for (String item : items) {
            if (item != null && !item.trim().isEmpty())
                packageItems.add(item.trim());
        }
    }

    public String       getPackageId()   { return packageId;   }
    public String       getDestination() { return destination; }
    public double       getWeightKg()    { return weightKg;    }
    public String       getPriority()    { return priority;    }
    public String       getStatus()      { return status;      }
    public String       getCarrierType() { return carrierType; }
    public List<String> getItems()       { return packageItems;}

    public void setStatus(String status)           { this.status      = status;      }
    public void setCarrierType(String carrierType) { this.carrierType = carrierType; }

    @Override
    public String toString() {
        return packageId + " → " + destination
             + " (" + weightKg + " kg) [" + status + "]";
    }
}
