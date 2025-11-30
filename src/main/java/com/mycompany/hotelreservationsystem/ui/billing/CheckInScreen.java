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

public class CheckInScreen extends JDialog {


    private JComboBox<String> cmbReservations;
    private JTextField txtGuestId;
    private JTextField txtDeposit;
    private JTextArea txtReservationDetails;
    private JButton btnConfirm;
    private JButton btnCancel;

    public CheckInScreen(JFrame parent) {
        super(parent, "Check-In Guest", true);
        setSize(600, 500);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel selectionPanel = createSelectionPanel();
        JPanel detailsPanel = createDetailsPanel();
        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(selectionPanel, BorderLayout.NORTH);
        mainPanel.add(detailsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadSampleReservations();
        addReservationChangeListener();
    }

    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Select Reservation"));

        panel.add(new JLabel("Reservation:"));
        cmbReservations = new JComboBox<>();
        panel.add(cmbReservations);

        panel.add(new JLabel("Guest ID / Passport:"));
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

        txtReservationDetails = new JTextArea(10, 40);
        txtReservationDetails.setEditable(false);
        txtReservationDetails.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtReservationDetails.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(txtReservationDetails);
        panel.add(scroll, BorderLayout.CENTER);

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

    private void loadSampleReservations() {
        // Sample data (can be replaced with database data later)
        cmbReservations.addItem("RES001 - John Smith - Room 101");
        cmbReservations.addItem("RES002 - Sarah Johnson - Room 205");
        cmbReservations.addItem("RES003 - Mike Davis - Room 302");

        if (cmbReservations.getItemCount() > 0) {
            cmbReservations.setSelectedIndex(0);
            updateReservationDetails();
        }
    }

    private void addReservationChangeListener() {
        cmbReservations.addActionListener(e -> updateReservationDetails());
    }

    private void updateReservationDetails() {
        String selected = (String) cmbReservations.getSelectedItem();
        if (selected == null) {
            txtReservationDetails.setText("");
            return;
        }

        String[] parts = selected.split(" - ");
        String resId = parts[0];
        String guestName = parts.length > 1 ? parts[1] : "Unknown";
        String roomInfo = parts.length > 2 ? parts[2] : "Unknown";

        StringBuilder sb = new StringBuilder();
        sb.append("Reservation ID : ").append(resId).append("\n");
        sb.append("Guest Name     : ").append(guestName).append("\n");
        sb.append("Room           : ").append(roomInfo).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Check-In Date  : 2024-01-15\n");
        sb.append("Check-Out Date : 2024-01-20\n");
        sb.append("Nights         : 5\n");
        sb.append("Status         : BOOKED\n");

        txtReservationDetails.setText(sb.toString());
    }

    private void confirmCheckIn() {
        if (!validateInput()) {
            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Confirm check-in for this reservation?",
                "Confirm Check-In",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (answer == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(
                    this,
                    "Check-In completed successfully.\nRoom status changed to OCCUPIED.",
                    "Check-In Done",
                    JOptionPane.INFORMATION_MESSAGE
            );
            dispose();
        }
    }

    private boolean validateInput() {
        if (cmbReservations.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation.");
            return false;
        }

        String id = txtGuestId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter guest ID / passport.");
            return false;
        }

        String depositText = txtDeposit.getText().trim();
        if (depositText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter deposit amount (0 if none).");
            return false;
        }

        try {
            double deposit = Double.parseDouble(depositText);
            if (deposit < 0) {
                JOptionPane.showMessageDialog(this, "Deposit cannot be negative.");
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Deposit must be a numeric value.");
            return false;
        }

        return true;
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame();
        CheckInScreen screen = new CheckInScreen(frame);
        screen.setVisible(true);
    }
}
