package com.atlaslogistics.ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class UIFactory {

    public static final Color C_BG       = new Color(245, 245, 248);
    public static final Color C_PANEL    = Color.WHITE;
    public static final Color C_ACCENT   = new Color(24,  95,  165);
    public static final Color C_SUCCESS  = new Color(59,  109, 17);
    public static final Color C_DANGER   = new Color(163, 45,  45);
    public static final Color C_AMBER    = new Color(186, 117, 23);
    public static final Color C_PURPLE   = new Color(100, 60,  160);
    public static final Color C_TEXT     = new Color(30,  30,  35);
    public static final Color C_MUTED    = new Color(100, 100, 110);
    public static final Color C_BORDER   = new Color(210, 210, 218);
    public static final Color C_ROW_ALT  = new Color(248, 248, 252);

    public static final Font F_TITLE    = new Font("Arial", Font.BOLD,  15);
    public static final Font F_LABEL    = new Font("Arial", Font.PLAIN, 12);
    public static final Font F_SMALL    = new Font("Arial", Font.PLAIN, 11);
    public static final Font F_MONO     = new Font("Monospaced", Font.PLAIN, 11);
    public static final Font F_STAT_VAL = new Font("Arial", Font.BOLD,  22);
    public static final Font F_STAT_LBL = new Font("Arial", Font.PLAIN, 11);

    public static JButton accentButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(F_SMALL);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        return btn;
    }

    public static JTextField styledField(String placeholder) {
        JTextField f = new JTextField(placeholder);
        f.setFont(F_LABEL);
        f.setForeground(C_MUTED);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDER),
            BorderFactory.createEmptyBorder(3, 6, 3, 6)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(placeholder)) {
                    f.setText(""); f.setForeground(C_TEXT);
                }
            }
            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) {
                    f.setText(placeholder); f.setForeground(C_MUTED);
                }
            }
        });
        return f;
    }

    public static JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(F_LABEL);
        return cb;
    }

    public static JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row))
                    c.setBackground(row % 2 == 0 ? C_PANEL : C_ROW_ALT);
                return c;
            }
        };
        table.setFont(F_LABEL);
        table.setRowHeight(26);
        table.getTableHeader().setFont(F_LABEL);
        table.getTableHeader().setBackground(new Color(230, 232, 240));
        table.getTableHeader().setForeground(C_TEXT);
        table.setGridColor(C_BORDER);
        table.setSelectionBackground(new Color(200, 220, 245));
        table.setSelectionForeground(C_TEXT);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        return table;
    }

    public static JScrollPane scrollPane(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createLineBorder(C_BORDER));
        return sp;
    }

    public static TitledBorder titledBorder(String title) {
        TitledBorder b = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(C_BORDER), title);
        b.setTitleFont(F_SMALL);
        b.setTitleColor(C_MUTED);
        return b;
    }

    public static JPanel whiteCard() {
        JPanel p = new JPanel();
        p.setBackground(C_PANEL);
        p.setBorder(BorderFactory.createLineBorder(C_BORDER));
        return p;
    }

    public static JPanel statCard(String label, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(C_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(accentColor, 2, true),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        JLabel lbl = new JLabel(label);
        lbl.setFont(F_STAT_LBL);
        lbl.setForeground(C_MUTED);
        valueLabel.setFont(F_STAT_VAL);
        valueLabel.setForeground(accentColor);
        card.add(lbl,        BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public static void addFormRow(JPanel form, GridBagConstraints gbc,
                                  int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3; gbc.gridwidth = 1;
        JLabel lbl = new JLabel(label);
        lbl.setFont(F_LABEL);
        lbl.setForeground(C_MUTED);
        form.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(field, gbc);
    }
}
