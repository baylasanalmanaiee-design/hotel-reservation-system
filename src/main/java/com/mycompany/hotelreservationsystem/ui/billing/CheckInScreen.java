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

public class CheckInScreen extends JDialog {
    private JComboBox<String> cmbReservations;
    private JTextField txtGuestId, txtDeposit;
    private JTextArea txtReservationDetails;
    private JButton btnConfirm, btnCancel;
    
    public CheckInScreen(JFrame parent) {
        super(parent, "Check-In Guest", true);
        setSize(600, 500);
        setLocationRelativeTo(parent);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Reservation Selection Panel
        JPanel selectionPanel = createSelectionPanel();
        
        // Details Panel
        JPanel detailsPanel = createDetailsPanel();
        
        // Buttons Panel
        JPanel buttonPanel = createButtonPanel();
        
        mainPanel.add(selectionPanel, BorderLayout.NORTH);
        mainPanel.add(detailsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        loadReservations();
    }
    
    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Select Reservation"));
        
        panel.add(new JLabel("Reservation:"));
        cmbReservations = new JComboBox<>();
        panel.add(cmbReservations);
        
        panel.add(new JLabel("Guest ID/Passport:"));
        txtGuestId = new JTextField();
        panel.add(txtGuestId);
        
        panel.add(new JLabel("Deposit Amount:"));
        txtDeposit = new JTextField();
        panel.add(txtDeposit);
        
        return panel;
    }
    
    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Reservation Details"));
        
        txtReservationDetails = new JTextArea(10, 50);
        txtReservationDetails.setEditable(false);
        txtReservationDetails.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(txtReservationDetails);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnConfirm = new JButton("Confirm Check-In");
        btnCancel = new JButton("Cancel");
        
        btnConfirm.setBackground(new Color(40, 167, 69));
        btnConfirm.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(220, 53, 69));
        btnCancel.setForeground(Color.WHITE);
        
        btnConfirm.addActionListener(e -> confirmCheckIn());
        btnCancel.addActionListener(e -> dispose());
        
        panel.add(btnCancel);
        panel.add(btnConfirm);
        
        return panel;
    }
    
    private void loadReservations() {
        // Sample reservations - replace with actual data
        cmbReservations.addItem("RES001 - John Smith - Room 101");
        cmbReservations.addItem("RES003 - Mike Davis - Room 302");
    }
    
    private void confirmCheckIn() {
        if (validateInput()) {
            int result = JOptionPane.showConfirmDialog(
                this,
                "Confirm check-in for selected reservation?",
                "Confirm Check-In",
                JOptionPane.YES_NO_OPTION
            );
            
            if (result == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Check-In Successful!");
                dispose();
            }
        }
    }
    
    private boolean validateInput() {
        if (cmbReservations.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation.");
            return false;
        }
        if (txtGuestId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter guest ID/passport.");
            return false;
        }
        return true;
    }
}