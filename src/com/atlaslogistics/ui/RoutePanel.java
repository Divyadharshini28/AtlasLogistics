package com.atlaslogistics.ui;

import com.atlaslogistics.core.LogisticsManager;
import com.atlaslogistics.core.RouteOptimizer;

import javax.swing.*;
import java.awt.*;

import static com.atlaslogistics.constants.ShippingConstants.*;
import static com.atlaslogistics.ui.UIFactory.*;

public class RoutePanel extends JPanel {

    private final LogisticsManager mgr;
    private final AppListener      listener;

    private JTextField        txtFrom, txtTo;
    private JComboBox<String> cboCarrier;
    private JTextArea         routeLog;

    public RoutePanel(AppListener listener) {
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
            titledBorder("Native Route Optimizer  —  C++ Engine (JNI Stub)"),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        form.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));

        txtFrom    = styledField("From city");
        txtFrom.setPreferredSize(new Dimension(150, 28));
        txtTo      = styledField("To city");
        txtTo.setPreferredSize(new Dimension(150, 28));
        cboCarrier = styledCombo(new String[]{"Van", "Bike", "Plane"});

        JButton btnOptimize = accentButton("Optimize Route  (native)", C_ACCENT);
        JButton btnClear    = accentButton("Clear", C_MUTED);

        form.add(new JLabel("From:")); form.add(txtFrom);
        form.add(new JLabel("To:"));   form.add(txtTo);
        form.add(new JLabel("Carrier:")); form.add(cboCarrier);
        form.add(btnOptimize); form.add(btnClear);

        JLabel info = new JLabel(
            "<html><i style='color:gray'>" +
            "native keyword: method declared in Java, body in C++ via JNI. " +
            "In production: System.loadLibrary(\"atlasrouter\") loads atlasrouter.so / .dll. " +
            "Here, a Java stub simulates the native call." +
            "</i></html>");
        info.setFont(F_SMALL);
        info.setBorder(BorderFactory.createEmptyBorder(6, 4, 0, 0));

        routeLog = new JTextArea(14, 0);
        routeLog.setFont(F_MONO);
        routeLog.setEditable(false);
        routeLog.setBackground(new Color(20, 22, 30));
        routeLog.setForeground(new Color(100, 210, 255));
        routeLog.setLineWrap(true);
        JScrollPane logScroll = scrollPane(routeLog);
        logScroll.setBorder(titledBorder("Optimizer Output"));

        btnOptimize.addActionListener(e -> onOptimize());
        btnClear.addActionListener(e    -> routeLog.setText(""));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(form, BorderLayout.CENTER);
        top.add(info, BorderLayout.SOUTH);

        add(top,       BorderLayout.NORTH);
        add(logScroll, BorderLayout.CENTER);
    }

    private void onOptimize() {
        String from    = txtFrom.getText().trim();
        String to      = txtTo.getText().trim();
        String carrier = (String) cboCarrier.getSelectedItem();

        if (from.isEmpty() || to.isEmpty()) {
            listener.showWarn("Enter both origin and destination."); return;
        }

        routeLog.append("─────────────────────────────────────────\n");
        routeLog.append("[JAVA] Invoking native method...\n");
        routeLog.append("[JAVA] public native double[] calculateFastestRoute(String, String, int)\n");
        routeLog.append("[JNI ] Simulating C++ library: atlasrouter.so\n\n");

        RouteOptimizer.RouteResult r = mgr.optimizeRoute(from, to, carrier);
        routeLog.append(r.summary() + "\n\n");

        double charge = BASE_RATE_PER_KM * r.distanceKm *
            ("Plane".equals(carrier) ? 100 * 3.5 : "Van".equals(carrier) ? 50 : 5);
        routeLog.append(String.format("[CALC] Estimated charge : ₹%.2f%n", charge));
        routeLog.append("─────────────────────────────────────────\n");
        routeLog.setCaretPosition(routeLog.getDocument().getLength());

        listener.log("Route optimized: " + from + " → " + to);
    }
}
