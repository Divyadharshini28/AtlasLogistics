package com.atlaslogistics.ui;

import com.atlaslogistics.carrier.TransportCarrier;   // Carrier model
import com.atlaslogistics.core.LogisticsManager;      // Logistics manager singleton

import javax.swing.*;                                 // Swing components
import javax.swing.table.DefaultTableModel;           // Table model
import java.awt.*;                                    // Layouts and dimensions
import java.util.List;                                // List collection
import java.util.stream.Collectors;                   // Stream collector

import static com.atlaslogistics.ui.UIFactory.*;      // Reusable UI styles/components

public class CarriersPanel extends JPanel {

    private final LogisticsManager mgr;   // Central logistics manager
    private final AppListener listener;   // Listener to communicate with main app

    private DefaultTableModel tableModel; // Table data model
    private JTable table;                 // Carrier table
    private JTextField txtAssignPkg, txtAssignCarrier; // Input fields

    public CarriersPanel(AppListener listener) {
        this.mgr = LogisticsManager.getInstance(); // Gets manager instance
        this.listener = listener;                  // Stores app listener
        setBackground(C_BG);                       // Background color
        setLayout(new BorderLayout(0, 12));        // Main layout
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16)); // Padding
        build();                                   // Builds UI
    }

    private void build() {
        String[] cols = {"Carrier ID","Type","Capacity (kg)","Fuel %","Status","In Service"}; // Column names

        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; } // Makes table non-editable
        };

        table = styledTable(tableModel);          // Styled table
        JScrollPane scroll = scrollPane(table);   // Scroll pane for table
        scroll.setBorder(titledBorder("Fleet Overview")); // Table title

        JPanel bar = whiteCard();                 // Bottom action panel
        bar.setBorder(BorderFactory.createCompoundBorder(
            titledBorder("Assign Carrier to Shipment"), // Outer titled border
            BorderFactory.createEmptyBorder(10, 12, 10, 12))); // Inner padding
        bar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6)); // Left-aligned layout

        txtAssignPkg = styledField("Package ID");      // Package ID input
        txtAssignPkg.setPreferredSize(new Dimension(130, 28)); // Input size

        txtAssignCarrier = styledField("Carrier ID");  // Carrier ID input
        txtAssignCarrier.setPreferredSize(new Dimension(100, 28)); // Input size

        JButton btnDispatch = accentButton("Dispatch", C_ACCENT);           // Dispatch button
        JButton btnFuel50   = accentButton("Lambda: Fuel > 50%", C_PURPLE); // Fuel filter button
        JButton btnAvail    = accentButton("Available Only", C_SUCCESS);    // Available carriers button
        JButton btnAll      = accentButton("Show All", C_MUTED);            // Show all carriers button

        bar.add(new JLabel("Package:"));      // Label
        bar.add(txtAssignPkg);                // Package input
        bar.add(new JLabel("Carrier:"));      // Label
        bar.add(txtAssignCarrier);            // Carrier input
        bar.add(btnDispatch);                 // Dispatch button
        bar.add(Box.createHorizontalStrut(16)); // Space
        bar.add(btnFuel50);                   // Fuel filter button
        bar.add(btnAvail);                    // Available filter button
        bar.add(btnAll);                      // Show all button

        btnDispatch.addActionListener(e -> onDispatch()); // Dispatch action

        btnFuel50.addActionListener(e -> {
            List<TransportCarrier> filtered = mgr.getAvailableCarriers(50.0); // Gets carriers with fuel > 50
            mgr.log("Lambda filter: fuel > 50% → " + filtered.size() + " carriers"); // Logs action
            populate(filtered); // Updates table
        });

        btnAvail.addActionListener(e -> populate(
            mgr.getAllCarriers().stream()          // Gets all carriers
               .filter(TransportCarrier::isAvailable) // Keeps only available ones
               .collect(Collectors.toList())));    // Converts stream to list

        btnAll.addActionListener(e -> populate(mgr.getAllCarriers())); // Shows all carriers

        add(scroll, BorderLayout.CENTER); // Adds table in center
        add(bar, BorderLayout.SOUTH);     // Adds controls at bottom
    }

    private void onDispatch() {
        String pkg = txtAssignPkg.getText().trim();         // Reads package ID
        String carrier = txtAssignCarrier.getText().trim(); // Reads carrier ID

        if (pkg.isEmpty() || carrier.isEmpty()) {           // Validates inputs
            listener.showWarn("Enter both Package ID and Carrier ID.");
            return;
        }

        String result = mgr.dispatch(pkg, carrier); // Dispatches package

        if (result.startsWith("ERROR")) listener.showError(result); // Error popup
        else listener.showInfo(result);                            // Success popup

        listener.refreshAll(); // Refreshes UI
    }

    public void refresh() {
        populate(mgr.getAllCarriers()); // Refreshes table with all carriers
    }

    private void populate(List<TransportCarrier> list) {
        tableModel.setRowCount(0); // Clears existing rows

        for (TransportCarrier c : list) { // Loops through carrier list
            tableModel.addRow(new Object[]{
                c.getCarrierId(),                       // Carrier ID
                c.getType(),                            // Carrier type
                c.getMaxCapacityKg() + " kg",          // Capacity
                String.format("%.0f%%", c.getFuelPercent()), // Fuel percentage
                c.isAvailable() ? "Available" : "Busy",     // Status
                c.isInService() ? "Yes" : "No"              // Service state
            });
        }
    }
}
