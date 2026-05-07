package com.atlaslogistics.ui;

import com.atlaslogistics.carrier.*;
import com.atlaslogistics.core.LogisticsManager;
import com.atlaslogistics.model.Shipment;

import javax.swing.*;
import java.awt.*;

import static com.atlaslogistics.constants.ShippingConstants.*;
import static com.atlaslogistics.ui.UIFactory.*;

public class SettingsPanel extends JPanel {

    private final LogisticsManager mgr;
    private final AppListener      listener;
    private JTextArea serLog;

    public SettingsPanel(AppListener listener) {
        this.mgr      = LogisticsManager.getInstance();
        this.listener = listener;
        setBackground(C_BG);
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        build();
    }

    private void build() {
        JPanel card = whiteCard();
        card.setBorder(BorderFactory.createCompoundBorder(
            titledBorder("Data Persistence — Serialization"),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel desc = new JLabel(
            "<html>" +
            "<b>Serialization</b> freezes the entire state (shipments + carriers) into <b>"
            + DATA_FILE + "</b>.<br>" +
            "On next launch, <code>ObjectInputStream</code> deserializes and restores all data.<br><br>" +
            "<b>How it works:</b><br>" +
            "• <code>Shipment</code> and <code>TransportCarrier</code> implement <code>Serializable</code><br>" +
            "• <code>ObjectOutputStream.writeObject()</code> writes the full object graph to disk<br>" +
            "• <code>ObjectInputStream.readObject()</code> restores it back into memory<br>" +
            "</html>");
        desc.setFont(F_LABEL);
        desc.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnSave  = accentButton("Save State  →  " + DATA_FILE, C_SUCCESS);
        JButton btnLoad  = accentButton("Load State  ←  " + DATA_FILE, C_ACCENT);
        JButton btnClear = accentButton("Reset to Demo Data",           C_DANGER);
        btnRow.add(btnSave); btnRow.add(btnLoad); btnRow.add(btnClear);

        serLog = new JTextArea(8, 0);
        serLog.setFont(F_MONO);
        serLog.setEditable(false);
        serLog.setBackground(new Color(245, 250, 245));
        serLog.setForeground(new Color(30, 80, 30));
        JScrollPane sl = scrollPane(serLog);
        sl.setBorder(titledBorder("Serialization Log"));
        sl.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnSave.addActionListener(e -> {
            boolean ok = mgr.saveState();
            serLog.append((ok ? "[OK]   " : "[FAIL] ") +
                "Saved " + mgr.getAllShipments().size() + " shipments, "
                + mgr.getAllCarriers().size() + " carriers → " + DATA_FILE + "\n");
        });

        btnLoad.addActionListener(e -> {
            boolean ok = mgr.loadState();
            serLog.append((ok ? "[OK]   " : "[FAIL] ") +
                "Loaded from " + DATA_FILE + "\n");
            listener.refreshAll();
        });

        btnClear.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this,
                "Reset all data to demo state?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                mgr.getAllShipments().clear();
                seedDemo();
                listener.refreshAll();
                serLog.append("[INFO] Reset to demo state.\n");
            }
        });

        card.add(desc);
        card.add(btnRow);
        card.add(Box.createVerticalStrut(12));
        card.add(sl);

        add(card, BorderLayout.NORTH);
    }

    private void seedDemo() {
        Shipment s1 = new Shipment("PKG-1039", "Coimbatore", 0.8);
        s1.loadPackages("Envelope A");
        s1.setStatus("Delivered");

        Shipment s2 = new Shipment("PKG-1040", "Mumbai", 18.0, "Express", "Plane");
        s2.loadPackages("Box B", "Fragile C", "Document D");

        Shipment s3 = new Shipment("PKG-1041", "Chennai", 4.2, "Standard", "Van");
        s3.loadPackages("Parcel E", "Parcel F");

        mgr.addShipment(s1);
        mgr.addShipment(s2);
        mgr.addShipment(s3);
    }
}
