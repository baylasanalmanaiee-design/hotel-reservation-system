/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.ui.reservation;

/**
 *
 * @author abeer
 */

import javax.swing.*;
import java.awt.*;

public class ReservationDetailsScreen extends JDialog{
   private JTextArea txtDetails;
    private JButton btnClose, btnPrint, btnEdit;
    
    public ReservationDetailsScreen(JFrame parent, String reservationId) {
        super(parent, "Reservation Details - " + reservationId, true);
        setSize(500, 400);
        setLocationRelativeTo(parent);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Details Area
        txtDetails = new JTextArea(15, 40);
        txtDetails.setEditable(false);
        txtDetails.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(txtDetails);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnEdit = new JButton("Edit");
        btnPrint = new JButton("Print");
        btnClose = new JButton("Close");
        
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnPrint);
        buttonPanel.add(btnClose);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        loadReservationDetails(reservationId);
    }
    
    private void loadReservationDetails(String reservationId) {
        // code for upload Reception_Info from database
        StringBuilder details = new StringBuilder();
        details.append("Reservation ID: ").append(reservationId).append("\n");
        details.append("Guest: John Smith\n");
        details.append("Room: 101 (Single)\n");
        details.append("Check-in: 2024-01-15\n");
        details.append("Check-out: 2024-01-20\n");
        details.append("Status: Booked\n");
        details.append("Total Amount: $500.00\n");
        
        txtDetails.setText(details.toString());
    }
}