package com.atlaslogistics.ui;

import com.atlaslogistics.core.LogisticsManager;
import com.atlaslogistics.model.Shipment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import static com.atlaslogistics.constants.ShippingConstants.*;
import static com.atlaslogistics.ui.UIFactory.*;

public class ShipmentsPanel extends JPanel {

    private final LogisticsManager mgr;
    private final AppListener      listener;

    private JTextField    txtPkgId, txtDest, txtWeight, txtItems;
    private JComboBox<String> cboPriority, cboCarrier;
    private DefaultTableModel tableModel;
    private JTable        table;

    public ShipmentsPanel(AppListener listener) {
        this.mgr      = LogisticsManager.getInstance();
        this.listener = listener;
        setBackground(C_BG);
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        build();
    }

    private void build() {
        JPanel form = whiteCard();
        form.setBorder(BorderFactory.createCompoundBorder(
            titledBorder("Add New Shipment"),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        form.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        txtPkgId    = styledField("e.g. PKG-1050");
        txtDest     = styledField("e.g. Bangalore");
        txtWeight   = styledField("e.g. 3.5");
        cboPriority = styledCombo(new String[]{"Standard", "Express", "Overnight"});
        cboCarrier  = styledCombo(new String[]{"Auto", "Bike", "Van", "Plane"});
        txtItems    = styledField("e.g. Box A, Envelope B, Parcel C  (ellipsis)");

        addFormRow(form, gbc, 0, "Package ID",       txtPkgId);
        addFormRow(form, gbc, 1, "Destination",      txtDest);
        addFormRow(form, gbc, 2, "Weight (kg)",      txtWeight);
        addFormRow(form, gbc, 3, "Priority",         cboPriority);
        addFormRow(form, gbc, 4, "Carrier Type",     cboCarrier);
        addFormRow(form, gbc, 5, "Items (ellipsis)", txtItems);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        JButton btnAdd    = accentButton("Add Shipment",    C_ACCENT);
        JButton btnDelete = accentButton("Delete Selected", C_DANGER);
        JButton btnMark   = accentButton("Mark Delivered",  C_SUCCESS);
        btnRow.add(btnAdd); btnRow.add(btnDelete); btnRow.add(btnMark);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 6, 2, 6);
        form.add(btnRow, gbc);

        String[] cols = {"Package ID","Destination","Weight","Priority","Carrier","Status","Items"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = styledTable(tableModel);
        JScrollPane scroll = scrollPane(table);
        scroll.setBorder(titledBorder("All Shipments"));

        btnAdd.addActionListener(e    -> onAdd());
        btnDelete.addActionListener(e -> onDelete());
        btnMark.addActionListener(e   -> onMarkDelivered());

        add(form,   BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void onAdd() {
        try {
            String id     = txtPkgId.getText().trim();
            String dest   = txtDest.getText().trim();
            String wStr   = txtWeight.getText().trim();

            if (id.isEmpty() || dest.isEmpty() || wStr.isEmpty())
                throw new IllegalArgumentException("Package ID, Destination and Weight are required.");

            double weight;
            try { weight = Double.parseDouble(wStr); }
            catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Weight must be a valid number."); }

            if (weight <= 0)
                throw new IllegalArgumentException("Weight must be greater than 0.");

            String priority = (String) cboPriority.getSelectedItem();
            String carrier  = (String) cboCarrier.getSelectedItem();

            Shipment s = "Auto".equals(carrier)
                ? new Shipment(id, dest, weight, priority,
                    weight <= BIKE_MAX_KG ? "Bike" : weight <= VAN_MAX_KG ? "Van" : "Plane")
                : new Shipment(id, dest, weight, priority, carrier);

            String itemsStr = txtItems.getText().trim();
            if (!itemsStr.isEmpty())
                s.loadPackages(itemsStr.split(","));   // ← ellipsis in action

            mgr.addShipment(s);
            listener.refreshAll();

            txtPkgId.setText(""); txtDest.setText("");
            txtWeight.setText(""); txtItems.setText("");
            listener.showInfo("Shipment " + id + " added successfully!");

        } catch (IllegalArgumentException ex) {
            listener.showError("Input Error: " + ex.getMessage());
        } catch (AssertionError ae) {
            listener.showError("Assertion failed: " + ae.getMessage());
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) { listener.showWarn("Select a shipment to delete."); return; }
        String id = (String) tableModel.getValueAt(row, 0);
        int ok = JOptionPane.showConfirmDialog(this,
            "Delete shipment " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) { mgr.removeShipment(id); listener.refreshAll(); }
    }

    private void onMarkDelivered() {
        int row = table.getSelectedRow();
        if (row < 0) { listener.showWarn("Select a shipment to mark delivered."); return; }
        String id = (String) tableModel.getValueAt(row, 0);
        mgr.getAllShipments().stream()
           .filter(s -> s.getPackageId().equals(id)).findFirst()
           .ifPresent(s -> { s.setStatus("Delivered"); mgr.log("Marked delivered: " + id); });
        listener.refreshAll();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        for (Shipment s : mgr.getAllShipments()) {
            tableModel.addRow(new Object[]{
                s.getPackageId(), s.getDestination(),
                s.getWeightKg() + " kg", s.getPriority(),
                s.getCarrierType(), s.getStatus(),
                s.getItems().isEmpty() ? "—" : String.join(", ", s.getItems())
            });
        }
    }
}
