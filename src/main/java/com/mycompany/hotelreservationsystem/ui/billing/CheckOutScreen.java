/*
 * Billing / Check-Out screen - Work by Aroob
 */
package com.mycompany.hotelreservationsystem.ui.billing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// DAO + model for invoices (by Aroob)
import com.mycompany.hotelreservationsystem.dao.InvoiceDAO;
import com.mycompany.hotelreservationsystem.model.Invoice;

public class CheckOutScreen extends JDialog {
    private JComboBox<String> cmbReservations;
    private JTextField txtNights, txtRoomPrice, txtDiscount, txtPenalty;
    private JTextArea txtExtraServices;
    private JButton btnGenerateInvoice, btnCompleteCheckout, btnCancel;
    private JTable chargesTable;
    private DefaultTableModel tableModel;
    
    public CheckOutScreen(JFrame parent) {
        super(parent, "Check-Out", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        initializeComponents();
    }
    
    private void initializeComponents() {
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
        };
        chargesTable = new JTable(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(chargesTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel servicesPanel = new JPanel(new BorderLayout());
        servicesPanel.setBorder(BorderFactory.createTitledBorder("Extra Services"));
        
        txtExtraServices = new JTextArea(3, 40);
        txtExtraServices.setLineWrap(true);
        txtExtraServices.setText("Mini Bar: $35.50\nRoom Service: $25.00");
        JScrollPane servicesScroll = new JScrollPane(txtExtraServices);
        servicesPanel.add(servicesScroll, BorderLayout.CENTER);
        
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
        
        btnCancel.addActionListener(e -> dispose());
        
        panel.add(btnGenerateInvoice);
        panel.add(btnCompleteCheckout);
        panel.add(btnCancel);
        
        return panel;
    }
    
    private void loadCheckedInReservations() {
        // later: load from ReservationDAO (now sample data)
        cmbReservations.addItem("RES002 - Sarah Johnson - Room 205");
        cmbReservations.addItem("RES004 - Emily Wilson - Room 104");
        
        cmbReservations.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCharges();
            }
        });
        
        if (cmbReservations.getItemCount() > 0) {
            cmbReservations.setSelectedIndex(0);
            updateCharges();
        }
    }
    
    private void updateCharges() {
        tableModel.setRowCount(0);
        
        if (cmbReservations.getSelectedItem() != null) {
            txtNights.setText("5");
            txtRoomPrice.setText("$100.00");
            
            tableModel.addRow(new Object[]{"Room Charges (5 nights)", "$500.00"});
            tableModel.addRow(new Object[]{"Tax (10%)", "$50.00"});
            tableModel.addRow(new Object[]{"Service Fee", "$25.00"});
            tableModel.addRow(new Object[]{"Mini Bar", "$35.50"});
            tableModel.addRow(new Object[]{"Room Service", "$25.00"});
            tableModel.addRow(new Object[]{"Late Check-out Penalty", "$30.00"});
            tableModel.addRow(new Object[]{"", ""});
            
            double subtotal = 500.00 + 50.00 + 25.00 + 35.50 + 25.00 + 30.00;
            double discount = Double.parseDouble(txtDiscount.getText());
            double total = subtotal - discount;
            
            tableModel.addRow(new Object[]{"SUBTOTAL", String.format("$%.2f", subtotal)});
            tableModel.addRow(new Object[]{"DISCOUNT", String.format("-$%.2f", discount)});
            tableModel.addRow(new Object[]{"TOTAL AMOUNT", String.format("$%.2f", total)});
        }
    }
    
    // helper to parse number from "$123.45"
    private double getAmountFromRow(String label) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String desc = String.valueOf(tableModel.getValueAt(i, 0));
            if (label.equals(desc)) {
                String raw = String.valueOf(tableModel.getValueAt(i, 1));
                raw = raw.replace("$", "").replace(",", "").trim();
                if (raw.startsWith("-")) {
                    raw = raw.substring(1);
                }
                try {
                    return Double.parseDouble(raw);
                } catch (NumberFormatException ex) {
                    return 0.0;
                }
            }
        }
        return 0.0;
    }
    
    // convert "RES002 - Sarah ..." -> 2 (for DB reservation_id)
    private int parseReservationId(String comboText) {
        String onlyDigits = comboText.replaceAll("\\D+", "");
        if (onlyDigits.isEmpty()) return 0;
        return Integer.parseInt(onlyDigits);
    }
    
    private void generateInvoice() {
        if (cmbReservations.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation first.");
            return;
        }

        // get totals from table
        double subtotal = getAmountFromRow("SUBTOTAL");
        double discountAmount = getAmountFromRow("DISCOUNT");
        double totalAmount = getAmountFromRow("TOTAL AMOUNT");
        double taxAmount = 0.0; // already included in subtotal in this demo

        String selectedItem = cmbReservations.getSelectedItem().toString();
        int reservationDbId = parseReservationId(selectedItem);
        
        // build invoice model (Aroob work)
        Invoice invoice = new Invoice();
        invoice.setReservationId(reservationDbId);
        invoice.setSubtotal(subtotal);
        invoice.setDiscountAmount(discountAmount);
        invoice.setTaxAmount(taxAmount);
        invoice.setTotalAmount(totalAmount);
        invoice.setCreatedAt(java.time.LocalDateTime.now().toString());
        
        int invoiceId = InvoiceDAO.insert(invoice);
        
        if (invoiceId > 0) {
            JOptionPane.showMessageDialog(this, 
                "Invoice saved successfully. ID: " + invoiceId);
            
            // show invoice screen
            new InvoiceViewScreen((JFrame) getParent(), selectedItem.split(" - ")[0])
                    .setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error while saving invoice.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void completeCheckout() {
        if (cmbReservations.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation first.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Complete check-out for selected reservation?\n" +
            "This will update room and reservation status.",
            "Confirm Check-Out",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // later: call PaymentDAO + RoomDAO + ReservationDAO
            JOptionPane.showMessageDialog(this, 
                "Check-Out completed successfully!\n" +
                "Room status will be set to 'Cleaning Required'.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
        }
    }
}
