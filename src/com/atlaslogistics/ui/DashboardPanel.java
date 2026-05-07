package com.atlaslogistics.ui;

import com.atlaslogistics.core.LogisticsManager;
import com.atlaslogistics.model.Shipment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import static com.atlaslogistics.ui.UIFactory.*;

public class DashboardPanel extends JPanel {

    private final LogisticsManager mgr;
    private final AppListener      listener;

    private JLabel         lblTotal, lblCarriers, lblPending, lblDelivered;
    private DefaultTableModel tableModel;
    private JTextArea      logArea;

    public DashboardPanel(AppListener listener) {
        this.mgr      = LogisticsManager.getInstance();
        this.listener = listener;
        setBackground(C_BG);
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        build();
    }

    private void build() {
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 10, 0));
        statsRow.setOpaque(false);
        lblTotal     = new JLabel("0");
        lblCarriers  = new JLabel("0");
        lblPending   = new JLabel("0");
        lblDelivered = new JLabel("0");
        statsRow.add(statCard("Total Shipments",  lblTotal,     C_ACCENT));
        statsRow.add(statCard("Active Carriers",  lblCarriers,  C_SUCCESS));
        statsRow.add(statCard("Pending Delivery", lblPending,   C_AMBER));
        statsRow.add(statCard("Delivered",        lblDelivered, C_DANGER));

        String[] cols = {"Package ID", "Destination", "Carrier", "Weight (kg)", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(tableModel);
        JScrollPane tableScroll = scrollPane(table);
        tableScroll.setBorder(titledBorder("Recent Shipments"));

        logArea = new JTextArea(8, 0);
        logArea.setFont(F_MONO);
        logArea.setEditable(false);
        logArea.setBackground(new Color(30, 30, 36));
        logArea.setForeground(new Color(160, 220, 160));
        logArea.setLineWrap(true);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(titledBorder("System Log"));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, logScroll);
        split.setDividerLocation(220);
        split.setOpaque(false);

        JButton btnRefresh = accentButton("Refresh", C_ACCENT);
        btnRefresh.addActionListener(e -> listener.refreshAll());
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.setOpaque(false);
        btnRow.add(btnRefresh);

        add(statsRow, BorderLayout.NORTH);
        add(split,    BorderLayout.CENTER);
        add(btnRow,   BorderLayout.SOUTH);
    }

    public void refresh() {
        lblTotal.setText(String.valueOf(mgr.getAllShipments().size()));
        lblCarriers.setText(String.valueOf(mgr.countAvailableCarriers()));
        lblPending.setText(String.valueOf(
            mgr.countByStatus("Pending") + mgr.countByStatus("In Transit")));
        lblDelivered.setText(String.valueOf(mgr.countByStatus("Delivered")));

        tableModel.setRowCount(0);
        List<Shipment> all = mgr.getAllShipments();
        int start = Math.max(0, all.size() - 10);
        for (int i = start; i < all.size(); i++) {
            Shipment s = all.get(i);
            tableModel.addRow(new Object[]{
                s.getPackageId(), s.getDestination(),
                s.getCarrierType(), s.getWeightKg() + " kg", s.getStatus()
            });
        }

        StringBuilder sb = new StringBuilder();
        List<String> logs = mgr.getSystemLog();
        int ls = Math.max(0, logs.size() - 30);
        for (int i = ls; i < logs.size(); i++) sb.append(logs.get(i)).append("\n");
        logArea.setText(sb.toString());
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
