package com.atlaslogistics.core;

import com.atlaslogistics.carrier.*;
import com.atlaslogistics.model.Shipment;
import static com.atlaslogistics.constants.ShippingConstants.*;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LogisticsManager {

    // List to store all shipment objects
    private List<Shipment> shipments;

    // List to store all transport carriers
    private List<TransportCarrier> carriers;

    // List to maintain system logs
    private List<String> systemLog;

    // Object used for route optimization
    private final RouteOptimizer routeOptimizer;

    // Private constructor for Singleton pattern
    private LogisticsManager() {

        // Initializing shipment list
        shipments = new ArrayList<>();

        // Initializing carrier list
        carriers = new ArrayList<>();

        // Initializing log list
        systemLog = new ArrayList<>();

        // Creating RouteOptimizer object
        routeOptimizer = new RouteOptimizer();

        // Adding default carriers into the system
        initDefaultCarriers();

        // Logging system initialization message
        log("LogisticsManager initialized (Singleton — Bill Pugh pattern)");
    }

    // Bill Pugh Singleton helper class
    private static class Holder {

        // Single instance of LogisticsManager
        private static final LogisticsManager INSTANCE =
                new LogisticsManager();
    }

    // Method to access singleton object
    public static LogisticsManager getInstance() {
        return Holder.INSTANCE;
    }

    // Method to add default carriers
    private void initDefaultCarriers() {

        // Adding bike carriers
        carriers.add(new BikeCarrier("C-001", 82));
        carriers.add(new BikeCarrier("C-002", 55));

        // Adding van carriers
        carriers.add(new VanCarrier("C-003", 71));
        carriers.add(new VanCarrier("C-004", 61));
        carriers.add(new VanCarrier("C-005", 38));

        // Adding plane carriers
        carriers.add(new PlaneCarrier("C-006", 90));
        carriers.add(new PlaneCarrier("C-007", 44));
    }

    // Method to add shipment
    public void addShipment(Shipment s) {

        // Add shipment into list
        shipments.add(s);

        // Log shipment details
        log("Shipment added: "
                + s.getPackageId()
                + " → "
                + s.getDestination());
    }

    // Method to remove shipment using package ID
    public boolean removeShipment(String packageId) {

        // removeIf() removes matching shipment
        boolean removed =
                shipments.removeIf(
                        s -> s.getPackageId().equals(packageId));

        // Log if shipment removed
        if (removed)
            log("Shipment removed: " + packageId);

        return removed;
    }

    // Method to return all shipments
    public List<Shipment> getAllShipments() {
        return shipments;
    }

    // Method to filter shipments using Predicate
    public List<Shipment> filterShipments(
            Predicate<Shipment> predicate) {

        // Using Java Stream API for filtering
        return shipments.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    // Method to calculate total revenue
    public double calculateTotalRevenue() {

        // Stream through all shipments
        return shipments.stream().mapToDouble(s -> {

            // Base amount calculation
            double base =
                    s.getWeightKg()
                    * BASE_RATE_PER_KM
                    * 100;

            // Priority multiplier using switch expression
            double multiplier = switch (s.getPriority()) {

                case "Express" -> 1.5;

                case "Overnight" -> 2.0;

                default -> 1.0;
            };

            // Discount applied if shipment not delivered
            double discount =
                    s.getStatus().equals("Delivered")
                            ? 0
                            : base * 0.05;

            // Final revenue calculation
            return (base * multiplier) - discount;

        }).sum(); // Sum all shipment revenues
    }

    // Method to get all carriers
    public List<TransportCarrier> getAllCarriers() {
        return carriers;
    }

    // Method to get carriers with minimum fuel
    public List<TransportCarrier> getAvailableCarriers(
            double minFuel) {

        return carriers.stream()

                // Filter available carriers
                .filter(c ->
                        c.isAvailable()
                        && c.getFuelPercent() >= minFuel)

                .collect(Collectors.toList());
    }

    // Method to get carriers eligible for weight
    public List<TransportCarrier> getEligibleCarriers(
            double weightKg) {

        List<TransportCarrier> eligible =
                carriers.stream()

                        // Filter based on availability and weight
                        .filter(c ->
                                c.isAvailable()
                                && c.canCarry(weightKg))

                        .collect(Collectors.toList());

        // Log lambda filtering result
        log("Lambda filter → "
                + eligible.size()
                + " carriers eligible for "
                + weightKg
                + " kg");

        return eligible;
    }

    // Method to dispatch shipment
    public String dispatch(
            String packageId,
            String carrierId) {

        // Find shipment using package ID
        Shipment shipment = shipments.stream()

                .filter(s ->
                        s.getPackageId().equals(packageId))

                .findFirst()

                .orElse(null);

        // Find carrier using carrier ID
        TransportCarrier carrier = carriers.stream()

                .filter(c ->
                        c.getCarrierId().equals(carrierId))

                .findFirst()

                .orElse(null);

        // Validation checks
        if (shipment == null)
            return "ERROR: Shipment not found — " + packageId;

        if (carrier == null)
            return "ERROR: Carrier not found — " + carrierId;

        if (!carrier.isAvailable())
            return "ERROR: Carrier "
                    + carrierId
                    + " is not available";

        // Deliver shipment
        String result = carrier.deliver(shipment);

        // Log dispatch details
        log("Dispatch: "
                + packageId
                + " via "
                + carrierId
                + " → "
                + result);

        return result;
    }

    // Method to optimize route
    public RouteOptimizer.RouteResult optimizeRoute(
            String from,
            String to,
            String carrierType) {

        // Calling RouteOptimizer method
        RouteOptimizer.RouteResult r =
                routeOptimizer.optimizeRoute(
                        from,
                        to,
                        carrierType);

        // Log route optimization details
        log("Route optimized: "
                + from
                + " → "
                + to
                + " ("
                + String.format("%.0f", r.distanceKm)
                + " km)");

        return r;
    }

    // Method to save system state into file
    public boolean saveState() {

        try (ObjectOutputStream oos =
                     new ObjectOutputStream(
                             new FileOutputStream(DATA_FILE))) {

            // Save shipments list
            oos.writeObject(shipments);

            // Save carriers list
            oos.writeObject(carriers);

            // Log save success
            log("State saved → " + DATA_FILE);

            return true;

        } catch (IOException e) {

            // Log save failure
            log("Save FAILED: " + e.getMessage());

            return false;
        }
    }

    // Suppress unchecked warning
    @SuppressWarnings("unchecked")

    // Method to load saved data
    public boolean loadState() {

        // File object for saved file
        File f = new File(DATA_FILE);

        // Check file existence
        if (!f.exists()) {

            log("No saved state found at "
                    + DATA_FILE);

            return false;
        }

        try (ObjectInputStream ois =
                     new ObjectInputStream(
                             new FileInputStream(f))) {

            // Read shipment list from file
            shipments =
                    (List<Shipment>) ois.readObject();

            // Read carrier list from file
            carriers =
                    (List<TransportCarrier>) ois.readObject();

            // Log successful loading
            log("State loaded: "
                    + shipments.size()
                    + " shipments, "
                    + carriers.size()
                    + " carriers from "
                    + DATA_FILE);

            return true;

        } catch (Exception e) {

            // Log loading failure
            log("Load FAILED: " + e.getMessage());

            return false;
        }
    }

    // Method for logging messages
    public void log(String msg) {

        // Create log entry with current time
        String entry =
                "["
                + new java.util.Date()
                .toString()
                .substring(11, 19)
                + "] "
                + msg;

        // Add log into list
        systemLog.add(entry);

        // Print log on console
        System.out.println(entry);
    }

    // Method to get all logs
    public List<String> getSystemLog() {
        return systemLog;
    }

    // Method to count shipments by status
    public long countByStatus(String status) {

        return shipments.stream()

                // Filter based on status
                .filter(s ->
                        s.getStatus().equals(status))

                .count();
    }

    // Method to count available carriers
    public long countAvailableCarriers() {

        return carriers.stream()

                // Method reference used
                .filter(TransportCarrier::isAvailable)

                .count();
    }
}
