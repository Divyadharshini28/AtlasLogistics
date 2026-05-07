package com.atlaslogistics.ui;

import com.atlaslogistics.carrier.TransportCarrier;
import com.atlaslogistics.core.LogisticsManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.atlaslogistics.ui.UIFactory.*;

public class CarriersPanel extends JPanel {

    private final LogisticsManager mgr;
    private final AppListener      listener;

    private DefaultTableModel tableModel;
    private JTable            table;
    private JTextField        txtAssignPkg, txtAssignCarrier;

    public CarriersPanel(AppListener listener) {
        this.mgr      = LogisticsManager.getInstance();
        this.listener = listener;
        setBackground(C_BG);
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        build();
    }

    private void build() {
        String[] cols = {"Carrier ID","Type","Capacity (kg)","Fuel %","Status","In Service"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = styledTable(tableModel);
        JScrollPane scroll = scrollPane(table);
        scroll.setBorder(titledBorder("Fleet Overview"));

        JPanel bar = whiteCard();
        bar.setBorder(BorderFactory.createCompoundBorder(
            titledBorder("Assign Carrier to Shipment"),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        bar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));

        txtAssignPkg     = styledField("Package ID");
        txtAssignPkg.setPreferredSize(new Dimension(130, 28));
        txtAssignCarrier = styledField("Carrier ID");
        txtAssignCarrier.setPreferredSize(new Dimension(100, 28));

        JButton btnDispatch = accentButton("Dispatch",              C_ACCENT);
        JButton btnFuel50   = accentButton("Lambda: Fuel > 50%",    C_PURPLE);
        JButton btnAvail    = accentButton("Available Only",        C_SUCCESS);
        JButton btnAll      = accentButton("Show All",              C_MUTED);

        bar.add(new JLabel("Package:")); bar.add(txtAssignPkg);
        bar.add(new JLabel("Carrier:")); bar.add(txtAssignCarrier);
        bar.add(btnDispatch);
        bar.add(Box.createHorizontalStrut(16));
        bar.add(btnFuel50); bar.add(btnAvail); bar.add(btnAll);

        btnDispatch.addActionListener(e -> onDispatch());

        btnFuel50.addActionListener(e -> {
            List<TransportCarrier> filtered = mgr.getAvailableCarriers(50.0);
            mgr.log("Lambda filter: fuel > 50% → " + filtered.size() + " carriers");
            populate(filtered);
        });

        btnAvail.addActionListener(e -> populate(
            mgr.getAllCarriers().stream()
               .filter(TransportCarrier::isAvailable)
               .collect(Collectors.toList())));

        btnAll.addActionListener(e -> populate(mgr.getAllCarriers()));

        add(scroll, BorderLayout.CENTER);
        add(bar,    BorderLayout.SOUTH);
    }

    private void onDispatch() {
        String pkg     = txtAssignPkg.getText().trim();
        String carrier = txtAssignCarrier.getText().trim();
        if (pkg.isEmpty() || carrier.isEmpty()) {
            listener.showWarn("Enter both Package ID and Carrier ID."); return;
        }
        String result = mgr.dispatch(pkg, carrier);
        if (result.startsWith("ERROR")) listener.showError(result);
        else                            listener.showInfo(result);
        listener.refreshAll();
    }

    public void refresh() { populate(mgr.getAllCarriers()); }

    private void populate(List<TransportCarrier> list) {
        tableModel.setRowCount(0);
        for (TransportCarrier c : list) {
            tableModel.addRow(new Object[]{
                c.getCarrierId(), c.getType(),
                c.getMaxCapacityKg() + " kg",
                String.format("%.0f%%", c.getFuelPercent()),
                c.isAvailable() ? "Available" : "Busy",
                c.isInService()  ? "Yes"       : "No"
            });
        }
    }
}
