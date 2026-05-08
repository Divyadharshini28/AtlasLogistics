package com.atlaslogistics.ui;

import com.atlaslogistics.core.LogisticsManager;   // Logistics manager singleton
import com.atlaslogistics.core.RouteOptimizer;     // Route optimizer result model

import javax.swing.*;                              // Swing components
import java.awt.*;                                 // Layouts, colors, dimensions

import static com.atlaslogistics.constants.ShippingConstants.*; // Shipping constants
import static com.atlaslogistics.ui.UIFactory.*;                // Reusable UI utilities

public class RoutePanel extends JPanel {

    private final LogisticsManager mgr;   // Central manager
    private final AppListener listener;   // Listener for app communication

    private JTextField txtFrom, txtTo;    // Input fields for source and destination
    private JComboBox<String> cboCarrier; // Carrier selector
    private JTextArea routeLog;           // Output log area

    public RoutePanel(AppListener listener) {
        this.mgr = LogisticsManager.getInstance(); // Gets singleton manager
        this.listener = listener;                  // Stores listener
        setBackground(C_BG);                       // Background color
        setLayout(new BorderLayout(0, 12));        // Main layout
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16)); // Padding
        build();                                   // Builds UI
    }

    private void build() {
        JPanel form = whiteCard(); // Main form panel
        form.setBorder(BorderFactory.createCompoundBorder(
            titledBorder("Native Route Optimizer  —  C++ Engine (JNI Stub)"), // Form title
            BorderFactory.createEmptyBorder(10, 12, 10, 12)));                // Inner padding
        form.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6)); // Left-aligned layout

        txtFrom = styledField("From city");       // Source city input
        txtFrom.setPreferredSize(new Dimension(150, 28)); // Input size

        txtTo = styledField("To city");           // Destination city input
        txtTo.setPreferredSize(new Dimension(150, 28)); // Input size

        cboCarrier = styledCombo(new String[]{"Van", "Bike", "Plane"}); // Carrier dropdown

        JButton btnOptimize = accentButton("Optimize Route  (native)", C_ACCENT); // Optimize button
        JButton btnClear = accentButton("Clear", C_MUTED);                        // Clear button

        form.add(new JLabel("From:"));       // Source label
        form.add(txtFrom);                   // Source input
        form.add(new JLabel("To:"));         // Destination label
        form.add(txtTo);                     // Destination input
        form.add(new JLabel("Carrier:"));    // Carrier label
        form.add(cboCarrier);                // Carrier dropdown
        form.add(btnOptimize);               // Optimize button
        form.add(btnClear);                  // Clear button

        JLabel info = new JLabel(
            "<html><i style='color:gray'>" +
            "native keyword: method declared in Java, body in C++ via JNI. " +
            "In production: System.loadLibrary(\"atlasrouter\") loads atlasrouter.so / .dll. " +
            "Here, a Java stub simulates the native call." +
            "</i></html>"); // Informational note about JNI

        info.setFont(F_SMALL); // Small font
        info.setBorder(BorderFactory.createEmptyBorder(6, 4, 0, 0)); // Top padding

        routeLog = new JTextArea(14, 0);         // Route log output area
        routeLog.setFont(F_MONO);                // Monospaced font
        routeLog.setEditable(false);             // Read-only
        routeLog.setBackground(new Color(20, 22, 30)); // Dark background
        routeLog.setForeground(new Color(100, 210, 255)); // Light blue text
        routeLog.setLineWrap(true);              // Line wrap enabled

        JScrollPane logScroll = scrollPane(routeLog); // Scroll pane for output
        logScroll.setBorder(titledBorder("Optimizer Output")); // Output title

        btnOptimize.addActionListener(e -> onOptimize()); // Optimize action
        btnClear.addActionListener(e -> routeLog.setText("")); // Clear action

        JPanel top = new JPanel(new BorderLayout()); // Top section
        top.setOpaque(false);                        // Transparent
        top.add(form, BorderLayout.CENTER);          // Form in center
        top.add(info, BorderLayout.SOUTH);           // Info below form

        add(top, BorderLayout.NORTH);        // Adds top section
        add(logScroll, BorderLayout.CENTER); // Adds output log
    }

    private void onOptimize() {
        String from = txtFrom.getText().trim();       // Reads source city
        String to = txtTo.getText().trim();           // Reads destination city
        String carrier = (String) cboCarrier.getSelectedItem(); // Reads selected carrier

        if (from.isEmpty() || to.isEmpty()) {         // Validates inputs
            listener.showWarn("Enter both origin and destination.");
            return;
        }

        routeLog.append("─────────────────────────────────────────\n"); // Separator
        routeLog.append("[JAVA] Invoking native method...\n");           // Java step
        routeLog.append("[JAVA] public native double[] calculateFastestRoute(String, String, int)\n"); // Native declaration
        routeLog.append("[JNI ] Simulating C++ library: atlasrouter.so\n\n"); // JNI simulation note

        RouteOptimizer.RouteResult r = mgr.optimizeRoute(from, to, carrier); // Optimizes route
        routeLog.append(r.summary() + "\n\n"); // Appends optimization summary

        double charge = BASE_RATE_PER_KM * r.distanceKm *
            ("Plane".equals(carrier) ? 100 * 3.5 : "Van".equals(carrier) ? 50 : 5); // Charge calculation

        routeLog.append(String.format("[CALC] Estimated charge : ₹%.2f%n", charge)); // Displays charge
        routeLog.append("─────────────────────────────────────────\n"); // Separator
        routeLog.setCaretPosition(routeLog.getDocument().getLength()); // Scrolls to latest line

        listener.log("Route optimized: " + from + " → " + to); // Logs optimization action
    }
}
