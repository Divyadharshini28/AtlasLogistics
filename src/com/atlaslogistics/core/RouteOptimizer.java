package com.atlaslogistics.core;

import static com.atlaslogistics.constants.ShippingConstants.*;

public class RouteOptimizer {

    // Native method declaration
    // Normally implemented using C/C++ through JNI
    public native double[] calculateFastestRoute(
            String from,
            String to,
            int carrierTypeCode);

    // Method to optimize route
    public RouteResult optimizeRoute(
            String from,
            String to,
            String carrierType) {

        // Assertion to check origin city
        assert from != null && !from.isEmpty()
                : "Origin city required";

        // Assertion to check destination city
        assert to != null && !to.isEmpty()
                : "Destination city required";

        // Simulating native method execution
        System.out.println(
                "[native] RouteOptimizer.calculateFastestRoute() called");

        // Displaying route information
        System.out.println(
                "[native] Simulating C++ execution: "
                        + from
                        + " → "
                        + to);

        // Generate random-like distance using hash codes
        double distanceKm =
                Math.abs(
                        200
                        + (from.hashCode()
                        ^ to.hashCode()) % 600);

        // Speed based on carrier type
        double speedKmh = switch (carrierType) {

            // Plane speed
            case "Plane" -> 800.0;

            // Van speed
            case "Van" -> 80.0;

            // Default speed for bike
            default -> 25.0;
        };

        // Estimated travel time calculation
        double estimatedHours =
                distanceKm / speedKmh;

        // Generate intermediate waypoints
        String[] waypoints =
                generateWaypoints(from, to);

        // Return RouteResult object
        return new RouteResult(
                from,
                to,
                distanceKm,
                estimatedHours,
                waypoints);
    }

    // Method to generate route waypoints
    private String[] generateWaypoints(
            String from,
            String to) {

        // List of possible midpoint cities
        String[] midpoints = {
                "Salem",
                "Vellore",
                "Hosur",
                "Tirupur",
                "Erode",
                "Madurai"
        };

        // Generate index using hashcode
        int idx =
                Math.abs(
                        (from + to).hashCode())
                        % midpoints.length;

        // Return route path
        return new String[]{
                from,
                midpoints[idx],
                to
        };
    }

    // Inner class to store route details
    public static class RouteResult {

        // Starting location
        public final String from;

        // Destination location
        public final String to;

        // Total distance in kilometers
        public final double distanceKm;

        // Estimated travel time
        public final double estimatedHours;

        // Route waypoints
        public final String[] waypoints;

        // Constructor for RouteResult
        public RouteResult(
                String from,
                String to,
                double distKm,
                double hrs,
                String[] wp) {

            // Initialize fields
            this.from = from;
            this.to = to;
            this.distanceKm = distKm;
            this.estimatedHours = hrs;
            this.waypoints = wp;
        }

        // Method to display route summary
        public String summary() {

            // Returning formatted route details
            return String.format(

                    "[native] Distance      : %.0f km%n"
                    + "[native] Estimated time: %.1f hrs%n"
                    + "[native] Waypoints     : %s",

                    distanceKm,
                    estimatedHours,

                    // Join waypoints using arrow symbol
                    String.join(" → ", waypoints));
        }
    }
}
