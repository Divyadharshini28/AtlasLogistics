package com.atlaslogistics.ui;

import com.atlaslogistics.carrier.*;                 // Imports carrier-related classes
import com.atlaslogistics.core.LogisticsManager;    // Imports logistics manager singleton
import com.atlaslogistics.model.Shipment;           // Imports shipment model

import javax.swing.*;                               // Swing GUI components
import java.awt.*;                                  // Layouts, colors, fonts
import java.awt.event.*;                            // Event handling classes

import static com.atlaslogistics.constants.ShippingConstants.*; // App constants
import static com.atlaslogistics.ui.UIFactory.*;                // UI factory constants/components


public class AtlasLogisticsApp extends JFrame implements AppListener {

    private final LogisticsManager mgr = LogisticsManager.getInstance(); // Single manager object

    private DashboardPanel  dashboardPanel;   // Dashboard screen
    private ShipmentsPanel  shipmentsPanel;   // Shipment management screen
    private CarriersPanel   carriersPanel;    // Carrier management screen
    private RoutePanel      routePanel;       // Route optimizer screen
    private SettingsPanel   settingsPanel;    // Settings screen

    public AtlasLogisticsApp() {
        super(APP_TITLE);                     // Sets frame title
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Prevents immediate close
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { onExit(); } // Custom exit handling
        });
        setSize(980, 700);                    // Frame size
        setLocationRelativeTo(null);          // Opens frame at center of screen

        seedDemoData();                       // Adds sample shipment data if empty
        mgr.loadState();                      // Loads saved state from file

        buildUI();                            // Creates all UI components
        refreshAll();                         // Refreshes all panels
        setVisible(true);                     // Makes window visible
        mgr.log("GUI launched — AtlasLogistics ready"); // Logs app start
    }

    private void seedDemoData() {
        if (!mgr.getAllShipments().isEmpty()) return; // Skip if shipment list already has data

        Shipment s1 = new Shipment("PKG-1039", "Coimbatore", 0.8); // Creates shipment 1
        s1.loadPackages("Envelope A");          // Adds package
        s1.setStatus("Delivered");              // Sets delivery status

        Shipment s2 = new Shipment("PKG-1040", "Mumbai", 18.0, "Express", "Plane"); // Shipment 2
        s2.loadPackages("Box B", "Fragile C", "Document D"); // Multiple packages

        Shipment s3 = new Shipment("PKG-1041", "Chennai", 4.2, "Standard", "Van"); // Shipment 3
        s3.loadPackages("Parcel E", "Parcel F"); // Adds packages

        mgr.addShipment(s1);                    // Adds shipment 1 to manager
        mgr.addShipment(s2);                    // Adds shipment 2 to manager
        mgr.addShipment(s3);                    // Adds shipment 3 to manager
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout()); // Main container panel
        root.setBackground(C_BG);                     // Background color
        root.add(buildTitleBar(), BorderLayout.NORTH); // Adds title bar at top

        dashboardPanel = new DashboardPanel(this); // Creates dashboard panel
        shipmentsPanel = new ShipmentsPanel(this); // Creates shipments panel
        carriersPanel  = new CarriersPanel(this);  // Creates carriers panel
        routePanel     = new RoutePanel(this);     // Creates route panel
        settingsPanel  = new SettingsPanel(this);  // Creates settings panel

        JTabbedPane tabs = new JTabbedPane();      // Creates tabbed pane
        tabs.setFont(F_LABEL);                     // Sets tab font
        tabs.setBackground(new Color(235, 235, 242)); // Tab background

        tabs.addTab("  Dashboard  ",       dashboardPanel); // Dashboard tab
        tabs.addTab("  Shipments  ",       shipmentsPanel); // Shipments tab
        tabs.addTab("  Carriers   ",       carriersPanel);  // Carriers tab
        tabs.addTab("  Route Optimizer  ", routePanel);     // Route tab
        tabs.addTab("  Settings   ",       settingsPanel);  // Settings tab

        root.add(tabs, BorderLayout.CENTER); // Adds tabs to center
        setContentPane(root);                // Sets root as frame content
    }

    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout()); // Title bar panel
        bar.setBackground(C_ACCENT);                 // Accent color background
        bar.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18)); // Padding

        JLabel title = new JLabel("  AtlasLogistics"); // Main title label
        title.setFont(new Font("Arial", Font.BOLD, 16)); // Title font
        title.setForeground(Color.WHITE);               // Title color

        JLabel sub = new JLabel("Digital Post Office — Control Panel  "); // Subtitle
        sub.setFont(F_SMALL);                           // Subtitle font
        sub.setForeground(new Color(180, 210, 240));   // Subtitle color

        bar.add(title, BorderLayout.WEST); // Places title on left
        bar.add(sub,   BorderLayout.EAST); // Places subtitle on right
        return bar;                        // Returns title bar
    }

    @Override
    public void refreshAll() {
        dashboardPanel.refresh(); // Refresh dashboard
        shipmentsPanel.refresh(); // Refresh shipments
        carriersPanel.refresh();  // Refresh carriers
    }

    @Override
    public void log(String message) {
        mgr.log(message);         // Adds message to log
        dashboardPanel.refresh(); // Updates dashboard log view
    }

    @Override
    public void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Info",
            JOptionPane.INFORMATION_MESSAGE); // Information popup
    }

    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error",
            JOptionPane.ERROR_MESSAGE); // Error popup
    }

    @Override
    public void showWarn(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning",
            JOptionPane.WARNING_MESSAGE); // Warning popup
    }

    private void onExit() {
        int opt = JOptionPane.showConfirmDialog(this,
            "Save state before exit?", "Exit AtlasLogistics",
            JOptionPane.YES_NO_CANCEL_OPTION); // Exit confirmation dialog

        if      (opt == JOptionPane.YES_OPTION) { 
            mgr.saveState();                    // Saves state
            System.exit(0);                     // Exits application
        }
        else if (opt == JOptionPane.NO_OPTION)  { 
            System.exit(0);                     // Exits without saving
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // Native look and feel
        } catch (Exception ignored) {} // Ignore errors if look and feel fails

        SwingUtilities.invokeLater(AtlasLogisticsApp::new); // Launches GUI safely
    }
}
