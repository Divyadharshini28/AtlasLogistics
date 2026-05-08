package com.atlaslogistics.ui;

import com.atlaslogistics.core.LogisticsManager;   // Logistics manager singleton
import com.atlaslogistics.model.Shipment;          // Shipment model

import javax.swing.*;                              // Swing GUI components
import javax.swing.table.DefaultTableModel;        // Table model
import java.awt.*;                                 // Layouts, colors, fonts
import java.util.List;                             // List collection

import static com.atlaslogistics.ui.UIFactory.*;   // Reusable UI styles/components

public class DashboardPanel extends JPanel {

    private final LogisticsManager mgr;   // Central manager
    private final AppListener listener;   // Listener to notify main app

    private JLabel lblTotal, lblCarriers, lblPending, lblDelivered; // Statistic labels
    private DefaultTableModel tableModel; // Shipment table model
    private JTextArea logArea;            // System log area

    public DashboardPanel(AppListener listener) {
        this.mgr = LogisticsManager.getInstance(); // Gets singleton manager
        this.listener = listener;                  // Stores listener
        setBackground(C_BG);                       // Background color
        setLayout(new BorderLayout(0, 14));        // Main layout
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16)); // Padding
        build();                                   // Builds UI
    }

    private void build() {
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 10, 0)); // Top stats row
        statsRow.setOpaque(false);                                  // Transparent background

        lblTotal     = new JLabel("0"); // Total shipments label
        lblCarriers  = new JLabel("0"); // Active carriers label
        lblPending   = new JLabel("0"); // Pending shipments label
        lblDelivered = new JLabel("0"); // Delivered shipments label

        statsRow.add(statCard("Total Shipments",  lblTotal,     C_ACCENT));  // Stat card
        statsRow.add(statCard("Active Carriers",  lblCarriers,  C_SUCCESS)); // Stat card
        statsRow.add(statCard("Pending Delivery", lblPending,   C_AMBER));   // Stat card
        statsRow.add(statCard("Delivered",        lblDelivered, C_DANGER));  // Stat card

        String[] cols = {"Package ID", "Destination", "Carrier", "Weight (kg)", "Status"}; // Table columns

        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; } // Makes table read-only
        };

        JTable table = styledTable(tableModel);      // Styled shipment table
        JScrollPane tableScroll = scrollPane(table); // Table scroll pane
        tableScroll.setBorder(titledBorder("Recent Shipments")); // Table title

        logArea = new JTextArea(8, 0);               // Log area
        logArea.setFont(F_MONO);                     // Monospaced font
        logArea.setEditable(false);                  // Read-only
        logArea.setBackground(new Color(30, 30, 36)); // Dark background
        logArea.setForeground(new Color(160, 220, 160)); // Green text
        logArea.setLineWrap(true);                   // Wrap lines

        JScrollPane logScroll = new JScrollPane(logArea); // Scroll pane for logs
        logScroll.setBorder(titledBorder("System Log"));  // Log title

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, logScroll); // Split table and logs
        split.setDividerLocation(220); // Divider position
        split.setOpaque(false);        // Transparent

        JButton btnRefresh = accentButton("Refresh", C_ACCENT); // Refresh button
        btnRefresh.addActionListener(e -> listener.refreshAll()); // Refresh action

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Button row
        btnRow.setOpaque(false);                                      // Transparent
        btnRow.add(btnRefresh);                                       // Adds button

        add(statsRow, BorderLayout.NORTH);  // Adds stats at top
        add(split, BorderLayout.CENTER);    // Adds split pane at center
        add(btnRow, BorderLayout.SOUTH);    // Adds button row at bottom
    }

    public void refresh() {
        lblTotal.setText(String.valueOf(mgr.getAllShipments().size())); // Updates total shipments
        lblCarriers.setText(String.valueOf(mgr.countAvailableCarriers())); // Updates available carriers

        lblPending.setText(String.valueOf(
            mgr.countByStatus("Pending") + mgr.countByStatus("In Transit"))); // Updates pending count

        lblDelivered.setText(String.valueOf(mgr.countByStatus("Delivered"))); // Updates delivered count

        tableModel.setRowCount(0); // Clears table

        List<Shipment> all = mgr.getAllShipments(); // Gets all shipments
        int start = Math.max(0, all.size() - 10);   // Last 10 shipments only

        for (int i = start; i < all.size(); i++) {
            Shipment s = all.get(i); // Current shipment

            tableModel.addRow(new Object[]{
                s.getPackageId(),              // Package ID
                s.getDestination(),            // Destination
                s.getCarrierType(),            // Carrier type
                s.getWeightKg() + " kg",       // Weight
                s.getStatus()                  // Shipment status
            });
        }

        StringBuilder sb = new StringBuilder(); // Builds log text
        List<String> logs = mgr.getSystemLog(); // Gets system logs
        int ls = Math.max(0, logs.size() - 30); // Last 30 logs only

        for (int i = ls; i < logs.size(); i++) {
            sb.append(logs.get(i)).append("\n"); // Appends log lines
        }

        logArea.setText(sb.toString()); // Updates log area
        logArea.setCaretPosition(logArea.getDocument().getLength()); // Scrolls to latest log
    }
}
