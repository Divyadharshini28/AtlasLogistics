package com.atlaslogistics.core;

import com.atlaslogistics.carrier.*;
import com.atlaslogistics.model.Shipment;
import static com.atlaslogistics.constants.ShippingConstants.*;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LogisticsManager {

    private List<Shipment>         shipments;
    private List<TransportCarrier> carriers;
    private List<String>           systemLog;
    private final RouteOptimizer   routeOptimizer;

    private LogisticsManager() {
        shipments      = new ArrayList<>();
        carriers       = new ArrayList<>();
        systemLog      = new ArrayList<>();
        routeOptimizer = new RouteOptimizer();
        initDefaultCarriers();
        log("LogisticsManager initialized (Singleton — Bill Pugh pattern)");
    }

    private static class Holder {
        private static final LogisticsManager INSTANCE = new LogisticsManager();
    }

    public static LogisticsManager getInstance() {
        return Holder.INSTANCE;
    }

    private void initDefaultCarriers() {
        carriers.add(new BikeCarrier ("C-001", 82));
        carriers.add(new BikeCarrier ("C-002", 55));
        carriers.add(new VanCarrier  ("C-003", 71));
        carriers.add(new VanCarrier  ("C-004", 61));
        carriers.add(new VanCarrier  ("C-005", 38));
        carriers.add(new PlaneCarrier("C-006", 90));
        carriers.add(new PlaneCarrier("C-007", 44));
    }


    public void addShipment(Shipment s) {
        shipments.add(s);
        log("Shipment added: " + s.getPackageId() + " → " + s.getDestination());
    }

    public boolean removeShipment(String packageId) {
        boolean removed = shipments.removeIf(s -> s.getPackageId().equals(packageId));
        if (removed) log("Shipment removed: " + packageId);
        return removed;
    }

    public List<Shipment> getAllShipments() { return shipments; }

    public List<Shipment> filterShipments(Predicate<Shipment> predicate) {
        return shipments.stream()
                        .filter(predicate)
                        .collect(Collectors.toList());
    }

    public double calculateTotalRevenue() {
        return shipments.stream().mapToDouble(s -> {
            double base        = s.getWeightKg() * BASE_RATE_PER_KM * 100;
            double multiplier  = switch (s.getPriority()) {
                case "Express"   -> 1.5;
                case "Overnight" -> 2.0;
                default          -> 1.0;
            };
            double discount    = s.getStatus().equals("Delivered") ? 0 : base * 0.05;
            return (base * multiplier) - discount;
        }).sum();
    }


    public List<TransportCarrier> getAllCarriers() { return carriers; }

    public List<TransportCarrier> getAvailableCarriers(double minFuel) {
        return carriers.stream()
                       .filter(c -> c.isAvailable() && c.getFuelPercent() >= minFuel)
                       .collect(Collectors.toList());
    }

    public List<TransportCarrier> getEligibleCarriers(double weightKg) {
        List<TransportCarrier> eligible = carriers.stream()
            .filter(c -> c.isAvailable() && c.canCarry(weightKg))
            .collect(Collectors.toList());
        log("Lambda filter → " + eligible.size()
            + " carriers eligible for " + weightKg + " kg");
        return eligible;
    }

    public String dispatch(String packageId, String carrierId) {
        Shipment shipment = shipments.stream()
            .filter(s -> s.getPackageId().equals(packageId))
            .findFirst().orElse(null);

        TransportCarrier carrier = carriers.stream()
            .filter(c -> c.getCarrierId().equals(carrierId))
            .findFirst().orElse(null);

        if (shipment == null) return "ERROR: Shipment not found — " + packageId;
        if (carrier  == null) return "ERROR: Carrier not found — "  + carrierId;
        if (!carrier.isAvailable()) return "ERROR: Carrier " + carrierId + " is not available";

        String result = carrier.deliver(shipment);
        log("Dispatch: " + packageId + " via " + carrierId + " → " + result);
        return result;
    }

    public RouteOptimizer.RouteResult optimizeRoute(String from, String to, String carrierType) {
        RouteOptimizer.RouteResult r = routeOptimizer.optimizeRoute(from, to, carrierType);
        log("Route optimized: " + from + " → " + to
            + " (" + String.format("%.0f", r.distanceKm) + " km)");
        return r;
    }



    public boolean saveState() {
        try (ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(shipments);
            oos.writeObject(carriers);
            log("State saved → " + DATA_FILE);
            return true;
        } catch (IOException e) {
            log("Save FAILED: " + e.getMessage());
            return false;
        }
    }


    @SuppressWarnings("unchecked")
    public boolean loadState() {
        File f = new File(DATA_FILE);
        if (!f.exists()) { log("No saved state found at " + DATA_FILE); return false; }
        try (ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(f))) {
            shipments = (List<Shipment>)         ois.readObject();
            carriers  = (List<TransportCarrier>) ois.readObject();
            log("State loaded: " + shipments.size() + " shipments, "
                + carriers.size() + " carriers from " + DATA_FILE);
            return true;
        } catch (Exception e) {
            log("Load FAILED: " + e.getMessage());
            return false;
        }
    }


    public void log(String msg) {
        String entry = "[" + new java.util.Date().toString().substring(11, 19) + "] " + msg;
        systemLog.add(entry);
        System.out.println(entry);
    }

    public List<String>  getSystemLog()   { return systemLog; }

    public long countByStatus(String status) {
        return shipments.stream()
                        .filter(s -> s.getStatus().equals(status))
                        .count();
    }

    public long countAvailableCarriers() {
        return carriers.stream()
                       .filter(TransportCarrier::isAvailable)
                       .count();
    }
}
