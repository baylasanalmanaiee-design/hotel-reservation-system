/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.ui.billing;

/**
 *
 * @author abeer
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InvoiceViewScreen extends JDialog {
    private JTextArea txtInvoiceDetails;
    private JButton btnPrint, btnExportPDF, btnClose;
    private String reservationId;
    
    public InvoiceViewScreen(JFrame parent, String reservationId) {
        super(parent, "Invoice Details - " + reservationId, true);
        this.reservationId = reservationId;
        setSize(600, 500);
        setLocationRelativeTo(parent);
        
        initializeComponents();
        loadInvoiceData();
    }
    
    // Constructor بديل 
    public InvoiceViewScreen(JFrame parent) {
        this(parent, "UNKNOWN");
    }
    
    private void initializeComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Invoice Details Panel
        JPanel detailsPanel = createDetailsPanel();
        
        // Buttons Panel
        JPanel buttonPanel = createButtonPanel();
        
        mainPanel.add(detailsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Invoice Details"));
        
        txtInvoiceDetails = new JTextArea(20, 50);
        txtInvoiceDetails.setEditable(false);
        txtInvoiceDetails.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtInvoiceDetails.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(txtInvoiceDetails);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnPrint = new JButton("Print");
        btnExportPDF = new JButton("Export PDF");
        btnClose = new JButton("Close");
        
        btnPrint.setBackground(new Color(70, 130, 180));
        btnPrint.setForeground(Color.WHITE);
        btnExportPDF.setBackground(new Color(220, 53, 69));
        btnExportPDF.setForeground(Color.WHITE);
        btnClose.setBackground(new Color(108, 117, 125));
        btnClose.setForeground(Color.WHITE);
        
        btnPrint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printInvoice();
            }
        });
        
        btnExportPDF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToPDF();
            }
        });
        
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        panel.add(btnPrint);
        panel.add(btnExportPDF);
        panel.add(btnClose);
        
        return panel;
    }
    
    private void loadInvoiceData() {
        // invoice data - replace with actual database data
        StringBuilder invoice = new StringBuilder();
        invoice.append("============================================\n");
        invoice.append("              HOTEL INVOICE\n");
        invoice.append("============================================\n\n");
        invoice.append("Invoice No: INV-").append(reservationId).append("\n");
        invoice.append("Reservation ID: ").append(reservationId).append("\n");
        invoice.append("Date: ").append(java.time.LocalDate.now()).append("\n");
        invoice.append("Guest: John Smith\n");
        invoice.append("Room: 101 (Single)\n");
        invoice.append("Period: 2024-01-15 to 2024-01-20 (5 nights)\n\n");
        invoice.append("--------------------------------------------\n");
        invoice.append("CHARGES:\n");
        invoice.append("--------------------------------------------\n");
        invoice.append(String.format("%-30s $%.2f\n", "Room Charges (5 nights)", 500.00));
        invoice.append(String.format("%-30s $%.2f\n", "Tax (10%)", 50.00));
        invoice.append(String.format("%-30s $%.2f\n", "Service Fee", 25.00));
        invoice.append(String.format("%-30s $%.2f\n", "Mini Bar", 45.50));
        invoice.append(String.format("%-30s $%.2f\n", "Late Check-out Penalty", 30.00));
        invoice.append("--------------------------------------------\n");
        invoice.append(String.format("%-30s $%.2f\n", "SUBTOTAL", 650.50));
        invoice.append(String.format("%-30s $%.2f\n", "Discount", -50.00));
        invoice.append("--------------------------------------------\n");
        invoice.append(String.format("%-30s $%.2f\n", "TOTAL AMOUNT", 600.50));
        invoice.append(String.format("%-30s $%.2f\n", "Deposit Paid", 100.00));
        invoice.append(String.format("%-30s $%.2f\n", "REMAINING BALANCE", 500.50));
        invoice.append("============================================\n");
        invoice.append("Thank you for staying with us!\n");
        
        txtInvoiceDetails.setText(invoice.toString());
    }
    
    private void printInvoice() {
        try {
            if (txtInvoiceDetails.print()) {
                JOptionPane.showMessageDialog(this, "Invoice sent to printer successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Printing cancelled.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error printing invoice: " + ex.getMessage(), 
                "Print Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportToPDF() {
        // implementation for PDF export
        JOptionPane.showMessageDialog(this, 
            "PDF export feature would be implemented here!\n" +
            "Invoice for " + reservationId + " is ready for export.", 
            "Export PDF", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // دالة مساعدة إذا احتجت أحدث الفاتورة
    public void updateReservationId(String newReservationId) {
        this.reservationId = newReservationId;
        setTitle("Invoice Details - " + reservationId);
        loadInvoiceData();
    }
    
    // main method for testing 
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        InvoiceViewScreen invoiceScreen = new InvoiceViewScreen(frame, "TEST123");
        invoiceScreen.setVisible(true);
    }
}