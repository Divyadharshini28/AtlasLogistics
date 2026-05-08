package com.atlaslogistics.ui;

import javax.swing.*;               // Swing components
import javax.swing.border.*;        // Border classes
import javax.swing.table.*;         // Table-related classes
import java.awt.*;                  // Colors, fonts, layouts
import java.awt.event.*;            // Focus and mouse events

public class UIFactory {

    public static final Color C_BG       = new Color(245, 245, 248); // Main background color
    public static final Color C_PANEL    = Color.WHITE;              // Panel background
    public static final Color C_ACCENT   = new Color(24,  95,  165); // Primary accent color
    public static final Color C_SUCCESS  = new Color(59,  109, 17);  // Success green
    public static final Color C_DANGER   = new Color(163, 45,  45);  // Danger red
    public static final Color C_AMBER    = new Color(186, 117, 23);  // Amber warning color
    public static final Color C_PURPLE   = new Color(100, 60,  160); // Purple accent
    public static final Color C_TEXT     = new Color(30,  30,  35);  // Main text color
    public static final Color C_MUTED    = new Color(100, 100, 110); // Muted text color
    public static final Color C_BORDER   = new Color(210, 210, 218); // Border color
    public static final Color C_ROW_ALT  = new Color(248, 248, 252); // Alternate row color

    public static final Font F_TITLE    = new Font("Arial", Font.BOLD, 15);   // Title font
    public static final Font F_LABEL    = new Font("Arial", Font.PLAIN, 12);  // Label font
    public static final Font F_SMALL    = new Font("Arial", Font.PLAIN, 11);  // Small font
    public static final Font F_MONO     = new Font("Monospaced", Font.PLAIN, 11); // Monospaced font
    public static final Font F_STAT_VAL = new Font("Arial", Font.BOLD, 22);   // Statistic value font
    public static final Font F_STAT_LBL = new Font("Arial", Font.PLAIN, 11);  // Statistic label font

    public static JButton accentButton(String text, Color bg) {
        JButton btn = new JButton(text);                 // Creates button
        btn.setFont(F_SMALL);                            // Button font
        btn.setBackground(bg);                           // Background color
        btn.setForeground(Color.WHITE);                  // Text color
        btn.setFocusPainted(false);                      // Removes focus border
        btn.setBorderPainted(false);                     // Removes default border
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));   // Hand cursor on hover
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14)); // Padding
        return btn;                                      // Returns styled button
    }

    public static JTextField styledField(String placeholder) {
        JTextField f = new JTextField(placeholder);      // Creates text field with placeholder
        f.setFont(F_LABEL);                              // Field font
        f.setForeground(C_MUTED);                        // Placeholder color

        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDER),    // Outer border
            BorderFactory.createEmptyBorder(3, 6, 3, 6))); // Inner padding

        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(placeholder)) {   // Clears placeholder when focused
                    f.setText("");
                    f.setForeground(C_TEXT);
                }
            }

            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) {             // Restores placeholder if empty
                    f.setText(placeholder);
                    f.setForeground(C_MUTED);
                }
            }
        });

        return f;                                        // Returns styled field
    }

    public static JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);   // Creates combo box
        cb.setFont(F_LABEL);                             // Sets font
        return cb;                                       // Returns styled combo box
    }

    public static JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col); // Default cell rendering

                if (!isRowSelected(row))                          // Alternate row colors
                    c.setBackground(row % 2 == 0 ? C_PANEL : C_ROW_ALT);

                return c;
            }
        };

        table.setFont(F_LABEL);                           // Table font
        table.setRowHeight(26);                           // Row height

        table.getTableHeader().setFont(F_LABEL);          // Header font
        table.getTableHeader().setBackground(new Color(230, 232, 240)); // Header background
        table.getTableHeader().setForeground(C_TEXT);     // Header text color

        table.setGridColor(C_BORDER);                     // Grid line color
        table.setSelectionBackground(new Color(200, 220, 245)); // Selection background
        table.setSelectionForeground(C_TEXT);             // Selection text color

        table.setShowHorizontalLines(true);               // Show horizontal lines
        table.setShowVerticalLines(false);                // Hide vertical lines

        return table;                                     // Returns styled table
    }

    public static JScrollPane scrollPane(Component c) {
        JScrollPane sp = new JScrollPane(c);              // Creates scroll pane
        sp.setBorder(BorderFactory.createLineBorder(C_BORDER)); // Border
        return sp;                                        // Returns scroll pane
    }

    public static TitledBorder titledBorder(String title) {
        TitledBorder b = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(C_BORDER), title); // Creates titled border

        b.setTitleFont(F_SMALL);                          // Title font
        b.setTitleColor(C_MUTED);                         // Title color

        return b;                                         // Returns border
    }

    public static JPanel whiteCard() {
        JPanel p = new JPanel();                          // Creates panel
        p.setBackground(C_PANEL);                         // White background
        p.setBorder(BorderFactory.createLineBorder(C_BORDER)); // Border
        return p;                                         // Returns panel
    }

    public static JPanel statCard(String label, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());     // Card panel
        card.setBackground(C_PANEL);                      // White background

        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(accentColor, 2, true),        // Colored rounded border
            BorderFactory.createEmptyBorder(12, 16, 12, 16))); // Padding

        JLabel lbl = new JLabel(label);                   // Label text
        lbl.setFont(F_STAT_LBL);                          // Label font
        lbl.setForeground(C_MUTED);                       // Label color

        valueLabel.setFont(F_STAT_VAL);                   // Value font
        valueLabel.setForeground(accentColor);            // Value color

        card.add(lbl, BorderLayout.NORTH);                // Label at top
        card.add(valueLabel, BorderLayout.CENTER);        // Value in center

        return card;                                      // Returns stat card
    }

    public static void addFormRow(JPanel form, GridBagConstraints gbc,
                                  int row, String label, JComponent field) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        gbc.gridwidth = 1;                                // Label constraints

        JLabel lbl = new JLabel(label);                   // Creates label
        lbl.setFont(F_LABEL);                             // Label font
        lbl.setForeground(C_MUTED);                       // Label color
        form.add(lbl, gbc);                               // Adds label

        gbc.gridx = 1;
        gbc.weightx = 1.0;                                // Field constraints
        form.add(field, gbc);                             // Adds input field
    }
}
