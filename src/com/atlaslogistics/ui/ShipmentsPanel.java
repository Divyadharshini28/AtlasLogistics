package com.atlaslogistics.ui;

import com.atlaslogistics.core.LogisticsManager;   // Logistics manager singleton
import com.atlaslogistics.model.Shipment;          // Shipment model

import javax.swing.*;                              // Swing components
import javax.swing.table.DefaultTableModel;        // Table model
import java.awt.*;                                 // Layouts, dimensions, insets

import static com.atlaslogistics.constants.ShippingConstants.*; // Shipping constants
import static com.atlaslogistics.ui.UIFactory.*;                // Reusable UI utilities

public class ShipmentsPanel extends JPanel {

    private final LogisticsManager mgr;   // Central logistics manager
    private final AppListener listener;   // Listener for main app communication

    private JTextField txtPkgId, txtDest, txtWeight, txtItems; // Input fields
    private JComboBox<String> cboPriority, cboCarrier;         // Dropdowns
    private DefaultTableModel tableModel;                      // Table model
    private JTable table;                                      // Shipments table

    public ShipmentsPanel(AppListener listener) {
        this.mgr = LogisticsManager.getInstance(); // Gets singleton manager
        this.listener = listener;                  // Stores listener
        setBackground(C_BG);                       // Background color
        setLayout(new BorderLayout(0, 12));        // Main layout
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16)); // Padding
        build();                                   // Builds UI
    }

    private void build() {
        JPanel form = whiteCard(); // Shipment form panel
        form.setBorder(BorderFactory.createCompoundBorder(
            titledBorder("Add New Shipment"),               // Form title
            BorderFactory.createEmptyBorder(10, 12, 10, 12))); // Inner padding
        form.setLayout(new GridBagLayout()); // Flexible form layout

        GridBagConstraints gbc = new GridBagConstraints(); // Layout constraints
        gbc.insets = new Insets(5, 6, 5, 6);              // Spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;         // Horizontal fill

        txtPkgId = styledField("e.g. PKG-1050");   // Package ID field
        txtDest = styledField("e.g. Bangalore");   // Destination field
        txtWeight = styledField("e.g. 3.5");       // Weight field

        cboPriority = styledCombo(new String[]{"Standard", "Express", "Overnight"}); // Priority dropdown
        cboCarrier = styledCombo(new String[]{"Auto", "Bike", "Van", "Plane"});      // Carrier dropdown

        txtItems = styledField("e.g. Box A, Envelope B, Parcel C  (ellipsis)"); // Items field

        addFormRow(form, gbc, 0, "Package ID", txtPkgId);       // Row 1
        addFormRow(form, gbc, 1, "Destination", txtDest);       // Row 2
        addFormRow(form, gbc, 2, "Weight (kg)", txtWeight);     // Row 3
        addFormRow(form, gbc, 3, "Priority", cboPriority);      // Row 4
        addFormRow(form, gbc, 4, "Carrier Type", cboCarrier);   // Row 5
        addFormRow(form, gbc, 5, "Items (ellipsis)", txtItems); // Row 6

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); // Button row
        btnRow.setOpaque(false); // Transparent

        JButton btnAdd = accentButton("Add Shipment", C_ACCENT);       // Add button
        JButton btnDelete = accentButton("Delete Selected", C_DANGER); // Delete button
        JButton btnMark = accentButton("Mark Delivered", C_SUCCESS);   // Delivered button

        btnRow.add(btnAdd);    // Adds add button
        btnRow.add(btnDelete); // Adds delete button
        btnRow.add(btnMark);   // Adds delivered button

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;                       // Spans 2 columns
        gbc.insets = new Insets(10, 6, 2, 6);   // Top spacing
        form.add(btnRow, gbc);                   // Adds buttons

        String[] cols = {"Package ID","Destination","Weight","Priority","Carrier","Status","Items"}; // Table columns

        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; } // Makes table read-only
        };

        table = styledTable(tableModel);      // Styled table
        JScrollPane scroll = scrollPane(table); // Scroll pane
        scroll.setBorder(titledBorder("All Shipments")); // Table title

        btnAdd.addActionListener(e -> onAdd());             // Add action
        btnDelete.addActionListener(e -> onDelete());       // Delete action
        btnMark.addActionListener(e -> onMarkDelivered());  // Delivered action

        add(form, BorderLayout.NORTH);   // Form at top
        add(scroll, BorderLayout.CENTER); // Table at center
    }

    private void onAdd() {
        try {
            String id = txtPkgId.getText().trim();    // Reads package ID
            String dest = txtDest.getText().trim();   // Reads destination
            String wStr = txtWeight.getText().trim(); // Reads weight

            if (id.isEmpty() || dest.isEmpty() || wStr.isEmpty())
                throw new IllegalArgumentException("Package ID, Destination and Weight are required."); // Validation

            double weight;
            try {
                weight = Double.parseDouble(wStr); // Converts weight to number
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Weight must be a valid number.");
            }

            if (weight <= 0)
                throw new IllegalArgumentException("Weight must be greater than 0."); // Positive check

            String priority = (String) cboPriority.getSelectedItem(); // Selected priority
            String carrier = (String) cboCarrier.getSelectedItem();   // Selected carrier

            Shipment s = "Auto".equals(carrier)
                ? new Shipment(id, dest, weight, priority,
                    weight <= BIKE_MAX_KG ? "Bike" : weight <= VAN_MAX_KG ? "Van" : "Plane") // Auto carrier selection
                : new Shipment(id, dest, weight, priority, carrier); // Manual carrier selection

            String itemsStr = txtItems.getText().trim(); // Reads items

            if (!itemsStr.isEmpty())
                s.loadPackages(itemsStr.split(","));   // Splits items using comma (ellipsis method)

            mgr.addShipment(s);     // Adds shipment to manager
            listener.refreshAll();  // Refreshes UI

            txtPkgId.setText("");   // Clears package field
            txtDest.setText("");    // Clears destination field
            txtWeight.setText("");  // Clears weight field
            txtItems.setText("");   // Clears items field

            listener.showInfo("Shipment " + id + " added successfully!"); // Success message

        } catch (IllegalArgumentException ex) {
            listener.showError("Input Error: " + ex.getMessage()); // Input error popup
        } catch (AssertionError ae) {
            listener.showError("Assertion failed: " + ae.getMessage()); // Assertion error popup
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow(); // Selected table row

        if (row < 0) {
            listener.showWarn("Select a shipment to delete."); // No selection warning
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0); // Selected package ID

        int ok = JOptionPane.showConfirmDialog(this,
            "Delete shipment " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION); // Confirmation dialog

        if (ok == JOptionPane.YES_OPTION) {
            mgr.removeShipment(id); // Deletes shipment
            listener.refreshAll();  // Refreshes UI
        }
    }

    private void onMarkDelivered() {
        int row = table.getSelectedRow(); // Selected row

        if (row < 0) {
            listener.showWarn("Select a shipment to mark delivered."); // No selection warning
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0); // Selected package ID

        mgr.getAllShipments().stream()
           .filter(s -> s.getPackageId().equals(id)) // Finds matching shipment
           .findFirst()
           .ifPresent(s -> {
               s.setStatus("Delivered");             // Updates status
               mgr.log("Marked delivered: " + id);   // Logs action
           });

        listener.refreshAll(); // Refreshes UI
    }

    public void refresh() {
        tableModel.setRowCount(0); // Clears table

        for (Shipment s : mgr.getAllShipments()) { // Iterates all shipments
            tableModel.addRow(new Object[]{
                s.getPackageId(),                          // Package ID
                s.getDestination(),                        // Destination
                s.getWeightKg() + " kg",                  // Weight
                s.getPriority(),                           // Priority
                s.getCarrierType(),                        // Carrier type
                s.getStatus(),                             // Status
                s.getItems().isEmpty() ? "—" : String.join(", ", s.getItems()) // Items list
            });
        }
    }
}
