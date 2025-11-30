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
import java.awt.*;

public class InvoiceViewScreen extends JDialog {

    private JTextArea txtInvoiceDetails;
    private JButton btnPrint, btnExportPDF, btnClose;
    private String reservationId;

    public InvoiceViewScreen(JFrame parent, String reservationId) {
        super(parent, "Invoice Details - " + reservationId, true);
        this.reservationId = reservationId;

        setSize(600, 500);
        setLocationRelativeTo(parent);

        initUI();
        loadInvoiceData();
    }

    public InvoiceViewScreen(JFrame parent) {
        this(parent, "UNKNOWN");
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        mainPanel.add(createInvoicePanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createInvoicePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Invoice Details"));

        txtInvoiceDetails = new JTextArea(20, 50);
        txtInvoiceDetails.setEditable(false);
        txtInvoiceDetails.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(txtInvoiceDetails);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnPrint = new JButton("Print");
        btnExportPDF = new JButton("Export PDF");
        btnClose = new JButton("Close");

        styleButton(btnPrint, new Color(70, 130, 180));
        styleButton(btnExportPDF, new Color(220, 53, 69));
        styleButton(btnClose, new Color(108, 117, 125));

        btnPrint.addActionListener(e -> printInvoice());
        btnExportPDF.addActionListener(e -> exportPDF());
        btnClose.addActionListener(e -> dispose());

        panel.add(btnPrint);
        panel.add(btnExportPDF);
        panel.add(btnClose);

        return panel;
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
    }

    private void loadInvoiceData() {

        StringBuilder invoice = new StringBuilder();

        invoice.append("============================================\n");
        invoice.append("                 HOTEL INVOICE\n");
        invoice.append("============================================\n\n");

        invoice.append("Invoice No: INV-").append(reservationId).append("\n");
        invoice.append("Reservation ID: ").append(reservationId).append("\n");
        invoice.append("Date: ").append(java.time.LocalDate.now()).append("\n");
        invoice.append("Guest: John Smith\n");
        invoice.append("Room: 101 (Single)\n");
        invoice.append("Stay: 2024-01-15 to 2024-01-20 (5 nights)\n\n");

        invoice.append("--------------------------------------------\n");
        invoice.append("CHARGES\n");
        invoice.append("--------------------------------------------\n");

        invoice.append(String.format("%-30s $%.2f\n", "Room Charges (5 nights)", 500.00));
        invoice.append(String.format("%-30s $%.2f\n", "Tax 10%", 50.00));
        invoice.append(String.format("%-30s $%.2f\n", "Service Fee", 25.00));
        invoice.append(String.format("%-30s $%.2f\n", "Mini Bar", 45.50));
        invoice.append(String.format("%-30s $%.2f\n", "Late Check-out Penalty", 30.00));

        invoice.append("--------------------------------------------\n");
        invoice.append(String.format("%-30s $%.2f\n", "SUBTOTAL", 650.50));
        invoice.append(String.format("%-30s -$%.2f\n", "Discount", 50.00));
        invoice.append("--------------------------------------------\n");
        invoice.append(String.format("%-30s $%.2f\n", "TOTAL AMOUNT", 600.50));
        invoice.append(String.format("%-30s $%.2f\n", "Deposit Paid", 100.00));
        invoice.append(String.format("%-30s $%.2f\n", "BALANCE DUE", 500.50));
        invoice.append("============================================\n");
        invoice.append("Thank you for staying with us!\n");

        txtInvoiceDetails.setText(invoice.toString());
    }

    private void printInvoice() {
        try {
            if (txtInvoiceDetails.print()) {
                JOptionPane.showMessageDialog(this, "Invoice printed successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Printing was cancelled.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Print Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportPDF() {
        JOptionPane.showMessageDialog(this,
                "PDF export feature will be implemented later.\n" +
                "Reservation: " + reservationId,
                "Export PDF",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void updateReservationId(String newId) {
        this.reservationId = newId;
        setTitle("Invoice Details - " + reservationId);
        loadInvoiceData();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        InvoiceViewScreen screen = new InvoiceViewScreen(frame, "TEST123");
        screen.setVisible(true);
    }
}