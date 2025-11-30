/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.ui.billing;

/**
 *
 * @author Aroob
 */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CheckOutScreen extends JDialog {


    private JComboBox<String> cmbReservations;
    private JTextField txtNights;
    private JTextField txtRoomPrice;
    private JTextField txtDiscount;
    private JTextField txtPenalty;
    private JTextArea txtExtraServices;
    private JButton btnGenerateInvoice;
    private JButton btnCompleteCheckout;
    private JButton btnCancel;
    private JTable chargesTable;
    private DefaultTableModel tableModel;

    public CheckOutScreen(JFrame parent) {
        super(parent, "Check-Out", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel selectionPanel = createSelectionPanel();
        JPanel chargesPanel = createChargesPanel();
        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(selectionPanel, BorderLayout.NORTH);
        mainPanel.add(chargesPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadCheckedInReservations();
    }

    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Select Reservation for Check-Out"));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Reservation:"));
        cmbReservations = new JComboBox<>();
        cmbReservations.setPreferredSize(new Dimension(300, 25));
        topPanel.add(cmbReservations);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        bottomPanel.add(new JLabel("Nights:"));
        txtNights = new JTextField();
        txtNights.setEditable(false);
        bottomPanel.add(txtNights);

        bottomPanel.add(new JLabel("Room Price/Night:"));
        txtRoomPrice = new JTextField();
        txtRoomPrice.setEditable(false);
        bottomPanel.add(txtRoomPrice);

        panel.add(topPanel);
        panel.add(bottomPanel);

        return panel;
    }

    private JPanel createChargesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Charges Breakdown"));

        String[] columns = {"Description", "Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        chargesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(chargesTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Extra services
        JPanel servicesPanel = new JPanel(new BorderLayout());
        servicesPanel.setBorder(BorderFactory.createTitledBorder("Extra Services"));

        txtExtraServices = new JTextArea(3, 40);
        txtExtraServices.setLineWrap(true);
        txtExtraServices.setText("Mini Bar: 35.50\nRoom Service: 25.00");
        JScrollPane servicesScroll = new JScrollPane(txtExtraServices);
        servicesPanel.add(servicesScroll, BorderLayout.CENTER);

        // Discount + penalty
        JPanel discountPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        discountPanel.add(new JLabel("Discount:"));
        txtDiscount = new JTextField("50.00");
        discountPanel.add(txtDiscount);

        discountPanel.add(new JLabel("Late Penalty:"));
        txtPenalty = new JTextField("30.00");
        discountPanel.add(txtPenalty);

        panel.add(tablePanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(discountPanel, BorderLayout.NORTH);
        southPanel.add(servicesPanel, BorderLayout.SOUTH);

        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnGenerateInvoice = new JButton("Generate Invoice");
        btnCompleteCheckout = new JButton("Complete Check-Out");
        btnCancel = new JButton("Cancel");

        btnGenerateInvoice.setBackground(new Color(70, 130, 180));
        btnGenerateInvoice.setForeground(Color.WHITE);
        btnCompleteCheckout.setBackground(new Color(40, 167, 69));
        btnCompleteCheckout.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(220, 53, 69));
        btnCancel.setForeground(Color.WHITE);

        btnGenerateInvoice.addActionListener(e -> generateInvoice());
        btnCompleteCheckout.addActionListener(e -> completeCheckout());
        btnCancel.addActionListener(e -> dispose());

        panel.add(btnGenerateInvoice);
        panel.add(btnCompleteCheckout);
        panel.add(btnCancel);

        return panel;
    }

    private void loadCheckedInReservations() {
        cmbReservations.addItem("RES002 - Sarah Johnson - Room 205");
        cmbReservations.addItem("RES004 - Emily Wilson - Room 104");

        cmbReservations.addActionListener(e -> updateCharges());

        if (cmbReservations.getItemCount() > 0) {
            cmbReservations.setSelectedIndex(0);
            updateCharges();
        }
    }

    private void updateCharges() {
        tableModel.setRowCount(0);

        if (cmbReservations.getSelectedItem() == null) {
            txtNights.setText("");
            txtRoomPrice.setText("");
            return;
        }

        txtNights.setText("5");
        txtRoomPrice.setText("100.00");

        double roomCharges = 5 * 100.0;
        double tax = roomCharges * 0.10;
        double serviceFee = 25.0;
        double miniBar = 35.50;
        double roomService = 25.0;
        double latePenalty = parseDoubleSafe(txtPenalty.getText(), 30.0);

        tableModel.addRow(new Object[]{"Room Charges (5 nights)", formatAmount(roomCharges)});
        tableModel.addRow(new Object[]{"Tax (10%)", formatAmount(tax)});
        tableModel.addRow(new Object[]{"Service Fee", formatAmount(serviceFee)});
        tableModel.addRow(new Object[]{"Mini Bar", formatAmount(miniBar)});
        tableModel.addRow(new Object[]{"Room Service", formatAmount(roomService)});
        tableModel.addRow(new Object[]{"Late Check-out Penalty", formatAmount(latePenalty)});
        tableModel.addRow(new Object[]{"", ""});

        double subtotal = roomCharges + tax + serviceFee + miniBar + roomService + latePenalty;
        double discount = parseDoubleSafe(txtDiscount.getText(), 0.0);
        double total = subtotal - discount;

        tableModel.addRow(new Object[]{"SUBTOTAL", formatAmount(subtotal)});
        tableModel.addRow(new Object[]{"DISCOUNT", "-" + formatAmount(discount)});
        tableModel.addRow(new Object[]{"TOTAL AMOUNT", formatAmount(total)});
    }

    private String formatAmount(double value) {
        return String.format("$%.2f", value);
    }

    private double parseDoubleSafe(String text, double defaultValue) {
        try {
            return Double.parseDouble(text.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private void generateInvoice() {
        if (cmbReservations.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation first.");
            return;
        }

        String selectedItem = cmbReservations.getSelectedItem().toString();
        String reservationId = selectedItem.split(" - ")[0]; // e.g. "RES002"

        new InvoiceViewScreen((JFrame) getParent(), reservationId).setVisible(true);
    }

    private void completeCheckout() {
        if (cmbReservations.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Complete check-out for selected reservation?\n" +
                        "This will update room status and mark reservation as completed.",
                "Confirm Check-Out",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(
                    this,
                    "Check-Out completed successfully!\nRoom status updated to 'Cleaning Required'.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
            dispose();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        CheckOutScreen screen = new CheckOutScreen(frame);
        screen.setVisible(true);
    }
}
