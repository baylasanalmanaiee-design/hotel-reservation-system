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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CheckOutScreen extends JDialog {
    private JComboBox<String> cmbReservations;
    private JTextField txtNights, txtRoomPrice, txtDiscount, txtPenalty;
    private JTextArea txtExtraServices;
    private JButton btnGenerateInvoice, btnCompleteCheckout, btnCancel;
    private JTable chargesTable;
    private DefaultTableModel tableModel;
    
    // Constructor ياخذJFrame
    public CheckOutScreen(JFrame parent) {
        super(parent, "Check-Out", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        initializeComponents();
    }
    
    private void initializeComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Selection Panel
        JPanel selectionPanel = createSelectionPanel();
        
        // Charges Panel
        JPanel chargesPanel = createChargesPanel();
        
        // Buttons Panel
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
        
        // Charges Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Charges Breakdown"));
        
        String[] columns = {"Description", "Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };
        chargesTable = new JTable(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(chargesTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Extra Services Panel
        JPanel servicesPanel = new JPanel(new BorderLayout());
        servicesPanel.setBorder(BorderFactory.createTitledBorder("Extra Services"));
        
        txtExtraServices = new JTextArea(3, 40);
        txtExtraServices.setLineWrap(true);
        txtExtraServices.setText("Mini Bar: $35.50\nRoom Service: $25.00");
        JScrollPane servicesScroll = new JScrollPane(txtExtraServices);
        servicesPanel.add(servicesScroll, BorderLayout.CENTER);
        
        // Discount and Penalty Panel
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
        
        // Style buttons
        btnGenerateInvoice.setBackground(new Color(70, 130, 180));
        btnGenerateInvoice.setForeground(Color.WHITE);
        btnCompleteCheckout.setBackground(new Color(40, 167, 69));
        btnCompleteCheckout.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(220, 53, 69));
        btnCancel.setForeground(Color.WHITE);
        
        // Add action listeners
        btnGenerateInvoice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateInvoice();
            }
        });
        
        btnCompleteCheckout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                completeCheckout();
            }
        });
        
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        panel.add(btnGenerateInvoice);
        panel.add(btnCompleteCheckout);
        panel.add(btnCancel);
        
        return panel;
    }
    
    private void loadCheckedInReservations() {
        // Sample data - replace with actual database data
        cmbReservations.addItem("RES002 - Sarah Johnson - Room 205");
        cmbReservations.addItem("RES004 - Emily Wilson - Room 104");
        
        // Add action listener to update charges when reservation selected
        cmbReservations.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCharges();
            }
        });
        
        // Auto-select first item if available
        if (cmbReservations.getItemCount() > 0) {
            cmbReservations.setSelectedIndex(0);
            updateCharges();
        }
    }
    
    private void updateCharges() {
        // Clear existing charges
        tableModel.setRowCount(0);
        
        // Sample charges - replace with actual calculation
        if (cmbReservations.getSelectedItem() != null) {
            txtNights.setText("5");
            txtRoomPrice.setText("$100.00");
            
            // Add sample charges
            tableModel.addRow(new Object[]{"Room Charges (5 nights)", "$500.00"});
            tableModel.addRow(new Object[]{"Tax (10%)", "$50.00"});
            tableModel.addRow(new Object[]{"Service Fee", "$25.00"});
            tableModel.addRow(new Object[]{"Mini Bar", "$35.50"});
            tableModel.addRow(new Object[]{"Room Service", "$25.00"});
            tableModel.addRow(new Object[]{"Late Check-out Penalty", "$30.00"});
            tableModel.addRow(new Object[]{"", ""});
            
            // Calculate totals
            double subtotal = 500.00 + 50.00 + 25.00 + 35.50 + 25.00 + 30.00;
            double discount = Double.parseDouble(txtDiscount.getText());
            double total = subtotal - discount;
            
            tableModel.addRow(new Object[]{"SUBTOTAL", String.format("$%.2f", subtotal)});
            tableModel.addRow(new Object[]{"DISCOUNT", String.format("-$%.2f", discount)});
            tableModel.addRow(new Object[]{"TOTAL AMOUNT", String.format("$%.2f", total)});
        }
    }
    
    private void generateInvoice() {
        if (cmbReservations.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation first.");
            return;
        }
        
        // get reservationId from combo box
        String selectedItem = cmbReservations.getSelectedItem().toString();
        String reservationId = selectedItem.split(" - ")[0]; // بيكون "RES002"
        
        // open InvoiceViewScreen
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
            // Update database: 
            // - Change reservation status to completed
            // - Change room status to Cleaning Required
            // - Save payment record
            
            // Show success message
            JOptionPane.showMessageDialog(this, 
                "Check-Out completed successfully!\n" +
                "Room status updated to 'Cleaning Required'.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
        }
    }
}