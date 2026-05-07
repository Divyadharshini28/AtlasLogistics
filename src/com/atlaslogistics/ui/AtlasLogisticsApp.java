package com.atlaslogistics.ui;

import com.atlaslogistics.carrier.*;
import com.atlaslogistics.core.LogisticsManager;
import com.atlaslogistics.model.Shipment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static com.atlaslogistics.constants.ShippingConstants.*;
import static com.atlaslogistics.ui.UIFactory.*;


public class AtlasLogisticsApp extends JFrame implements AppListener {

    private final LogisticsManager mgr = LogisticsManager.getInstance();

    private DashboardPanel  dashboardPanel;
    private ShipmentsPanel  shipmentsPanel;
    private CarriersPanel   carriersPanel;
    private RoutePanel      routePanel;
    private SettingsPanel   settingsPanel;

    public AtlasLogisticsApp() {
        super(APP_TITLE);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { onExit(); }
        });
        setSize(980, 700);
        setLocationRelativeTo(null);

        seedDemoData();
        mgr.loadState();

        buildUI();
        refreshAll();
        setVisible(true);
        mgr.log("GUI launched — AtlasLogistics ready");
    }

    private void seedDemoData() {
        if (!mgr.getAllShipments().isEmpty()) return;

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

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(C_BG);
        root.add(buildTitleBar(), BorderLayout.NORTH);

        dashboardPanel = new DashboardPanel(this);
        shipmentsPanel = new ShipmentsPanel(this);
        carriersPanel  = new CarriersPanel(this);
        routePanel     = new RoutePanel(this);
        settingsPanel  = new SettingsPanel(this);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(F_LABEL);
        tabs.setBackground(new Color(235, 235, 242));
        tabs.addTab("  Dashboard  ",       dashboardPanel);
        tabs.addTab("  Shipments  ",       shipmentsPanel);
        tabs.addTab("  Carriers   ",       carriersPanel);
        tabs.addTab("  Route Optimizer  ", routePanel);
        tabs.addTab("  Settings   ",       settingsPanel);

        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(C_ACCENT);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        JLabel title = new JLabel("  AtlasLogistics");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Digital Post Office — Control Panel  ");
        sub.setFont(F_SMALL);
        sub.setForeground(new Color(180, 210, 240));

        bar.add(title, BorderLayout.WEST);
        bar.add(sub,   BorderLayout.EAST);
        return bar;
    }


    @Override
    public void refreshAll() {
        dashboardPanel.refresh();
        shipmentsPanel.refresh();
        carriersPanel.refresh();
    }

    @Override
    public void log(String message) {
        mgr.log(message);
        dashboardPanel.refresh();
    }

    @Override
    public void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Info",
            JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error",
            JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showWarn(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning",
            JOptionPane.WARNING_MESSAGE);
    }

    private void onExit() {
        int opt = JOptionPane.showConfirmDialog(this,
            "Save state before exit?", "Exit AtlasLogistics",
            JOptionPane.YES_NO_CANCEL_OPTION);
        if      (opt == JOptionPane.YES_OPTION) { mgr.saveState(); System.exit(0); }
        else if (opt == JOptionPane.NO_OPTION)  { System.exit(0); }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(AtlasLogisticsApp::new);
    }
}
