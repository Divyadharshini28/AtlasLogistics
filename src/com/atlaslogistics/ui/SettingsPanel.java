package com.atlaslogistics.ui;

import com.atlaslogistics.carrier.*;              // Carrier-related classes
import com.atlaslogistics.core.LogisticsManager;  // Logistics manager singleton
import com.atlaslogistics.model.Shipment;         // Shipment model

import javax.swing.*;                             // Swing components
import java.awt.*;                                // Layouts, colors, dimensions

import static com.atlaslogistics.constants.ShippingConstants.*; // Shipping constants
import static com.atlaslogistics.ui.UIFactory.*;                // Reusable UI utilities

public class SettingsPanel extends JPanel {

    private final LogisticsManager mgr;   // Central logistics manager
    private final AppListener listener;   // Listener to communicate with main app
    private JTextArea serLog;             // Serialization log area

    public SettingsPanel(AppListener listener) {
        this.mgr = LogisticsManager.getInstance(); // Gets singleton manager
        this.listener = listener;                  // Stores listener
        setBackground(C_BG);                       // Background color
        setLayout(new BorderLayout(0, 12));        // Main layout
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16)); // Padding
        build();                                   // Builds UI
    }

    private void build() {
        JPanel card = whiteCard(); // Main card panel
        card.setBorder(BorderFactory.createCompoundBorder(
            titledBorder("Data Persistence — Serialization"), // Card title
            BorderFactory.createEmptyBorder(16, 16, 16, 16))); // Inner padding
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS)); // Vertical layout

        JLabel desc = new JLabel(
            "<html>" +
            "<b>Serialization</b> freezes the entire state (shipments + carriers) into <b>"
            + DATA_FILE + "</b>.<br>" +
            "On next launch, <code>ObjectInputStream</code> deserializes and restores all data.<br><br>" +
            "<b>How it works:</b><br>" +
            "• <code>Shipment</code> and <code>TransportCarrier</code> implement <code>Serializable</code><br>" +
            "• <code>ObjectOutputStream.writeObject()</code> writes the full object graph to disk<br>" +
            "• <code>ObjectInputStream.readObject()</code> restores it back into memory<br>" +
            "</html>"); // Description of serialization

        desc.setFont(F_LABEL); // Label font
        desc.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0)); // Bottom spacing
        desc.setAlignmentX(Component.LEFT_ALIGNMENT); // Left aligned

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); // Button row
        btnRow.setOpaque(false); // Transparent
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT); // Left aligned

        JButton btnSave  = accentButton("Save State  →  " + DATA_FILE, C_SUCCESS); // Save button
        JButton btnLoad  = accentButton("Load State  ←  " + DATA_FILE, C_ACCENT);  // Load button
        JButton btnClear = accentButton("Reset to Demo Data", C_DANGER);           // Reset button

        btnRow.add(btnSave);  // Adds save button
        btnRow.add(btnLoad);  // Adds load button
        btnRow.add(btnClear); // Adds reset button

        serLog = new JTextArea(8, 0);              // Serialization log text area
        serLog.setFont(F_MONO);                    // Monospaced font
        serLog.setEditable(false);                 // Read-only
        serLog.setBackground(new Color(245, 250, 245)); // Light background
        serLog.setForeground(new Color(30, 80, 30));    // Dark green text

        JScrollPane sl = scrollPane(serLog);       // Scroll pane for log
        sl.setBorder(titledBorder("Serialization Log")); // Log title
        sl.setAlignmentX(Component.LEFT_ALIGNMENT); // Left aligned

        btnSave.addActionListener(e -> {
            boolean ok = mgr.saveState(); // Saves current state

            serLog.append((ok ? "[OK]   " : "[FAIL] ") +
                "Saved " + mgr.getAllShipments().size() + " shipments, "
                + mgr.getAllCarriers().size() + " carriers → " + DATA_FILE + "\n"); // Save log
        });

        btnLoad.addActionListener(e -> {
            boolean ok = mgr.loadState(); // Loads saved state

            serLog.append((ok ? "[OK]   " : "[FAIL] ") +
                "Loaded from " + DATA_FILE + "\n"); // Load log

            listener.refreshAll(); // Refreshes all panels
        });

        btnClear.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this,
                "Reset all data to demo state?", "Confirm", JOptionPane.YES_NO_OPTION); // Confirmation dialog

            if (c == JOptionPane.YES_OPTION) {
                mgr.getAllShipments().clear(); // Clears existing shipments
                seedDemo();                    // Recreates demo shipments
                listener.refreshAll();         // Refreshes UI
                serLog.append("[INFO] Reset to demo state.\n"); // Reset log
            }
        });

        card.add(desc);                      // Adds description
        card.add(btnRow);                    // Adds button row
        card.add(Box.createVerticalStrut(12)); // Vertical space
        card.add(sl);                        // Adds log area

        add(card, BorderLayout.NORTH);       // Adds card to top
    }

    private void seedDemo() {
        Shipment s1 = new Shipment("PKG-1039", "Coimbatore", 0.8); // Demo shipment 1
        s1.loadPackages("Envelope A");                             // Adds package
        s1.setStatus("Delivered");                                 // Delivered status

        Shipment s2 = new Shipment("PKG-1040", "Mumbai", 18.0, "Express", "Plane"); // Demo shipment 2
        s2.loadPackages("Box B", "Fragile C", "Document D");      // Adds packages

        Shipment s3 = new Shipment("PKG-1041", "Chennai", 4.2, "Standard", "Van"); // Demo shipment 3
        s3.loadPackages("Parcel E", "Parcel F");                  // Adds packages

        mgr.addShipment(s1); // Adds shipment 1
        mgr.addShipment(s2); // Adds shipment 2
        mgr.addShipment(s3); // Adds shipment 3
    }
}
