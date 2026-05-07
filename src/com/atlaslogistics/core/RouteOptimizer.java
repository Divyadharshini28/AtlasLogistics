package com.atlaslogistics.core;

import static com.atlaslogistics.constants.ShippingConstants.*;

public class RouteOptimizer {

    public native double[] calculateFastestRoute(String from, String to, int carrierTypeCode);

    public RouteResult optimizeRoute(String from, String to, String carrierType) {
        assert from != null && !from.isEmpty() : "Origin city required";
        assert to   != null && !to.isEmpty()   : "Destination city required";

        System.out.println("[native] RouteOptimizer.calculateFastestRoute() called");
        System.out.println("[native] Simulating C++ execution: " + from + " → " + to);

        double distanceKm = Math.abs(200 + (from.hashCode() ^ to.hashCode()) % 600);

        double speedKmh = switch (carrierType) {
            case "Plane" -> 800.0;
            case "Van"   -> 80.0;
            default      -> 25.0;   // Bike
        };

        double estimatedHours = distanceKm / speedKmh;
        String[] waypoints    = generateWaypoints(from, to);

        return new RouteResult(from, to, distanceKm, estimatedHours, waypoints);
    }

    private String[] generateWaypoints(String from, String to) {
        String[] midpoints = {"Salem", "Vellore", "Hosur", "Tirupur", "Erode", "Madurai"};
        int idx = Math.abs((from + to).hashCode()) % midpoints.length;
        return new String[]{from, midpoints[idx], to};
    }

    public static class RouteResult {
        public final String   from;
        public final String   to;
        public final double   distanceKm;
        public final double   estimatedHours;
        public final String[] waypoints;

        public RouteResult(String from, String to,
                           double distKm, double hrs, String[] wp) {
            this.from           = from;
            this.to             = to;
            this.distanceKm     = distKm;
            this.estimatedHours = hrs;
            this.waypoints      = wp;
        }

        public String summary() {
            return String.format(
                "[native] Distance      : %.0f km%n" +
                "[native] Estimated time: %.1f hrs%n" +
                "[native] Waypoints     : %s",
                distanceKm, estimatedHours,
                String.join(" → ", waypoints));
        }
    }
}
