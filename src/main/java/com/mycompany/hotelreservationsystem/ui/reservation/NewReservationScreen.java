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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewReservationScreen extends JDialog{

    private JTextField txtGuestName, txtPhone, txtId;
    private JComboBox<String> cmbRoomType;
    private JTextField txtCheckInDate, txtCheckOutDate;
    private JButton btnSearchRooms, btnConfirm, btnCancel;
    private JTable roomsTable;
    private DefaultTableModel tableModel;
    
    public NewReservationScreen(JFrame parent) {
        super(parent, "New Reservation", true);
        setSize(900, 600);
        setLocationRelativeTo(parent);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Guest Information Panel
        JPanel guestPanel = createGuestPanel();
        
        // Room Selection Panel
        JPanel roomPanel = createRoomPanel();
        
        // Rooms Table
        JPanel tablePanel = createTablePanel();
        
        // Buttons Panel
        JPanel buttonPanel = createButtonPanel();
        
        mainPanel.add(guestPanel, BorderLayout.NORTH);
        mainPanel.add(roomPanel, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.SOUTH);
        
        JPanel container = new JPanel(new BorderLayout());
        container.add(mainPanel, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);
        
        add(container);
    }
    
    private JPanel createGuestPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Guest Information"));
        
        panel.add(new JLabel("Full Name:"));
        txtGuestName = new JTextField();
        panel.add(txtGuestName);
        
        panel.add(new JLabel("Phone:"));
        txtPhone = new JTextField();
        panel.add(txtPhone);
        
        panel.add(new JLabel("ID/Passport:"));
        txtId = new JTextField();
        panel.add(txtId);
        
        return panel;
    }
    
    private JPanel createRoomPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Room Selection"));
        
        panel.add(new JLabel("Room Type:"));
        cmbRoomType = new JComboBox<>(new String[]{"Single", "Double", "Suite", "Deluxe"});
        panel.add(cmbRoomType);
        
        panel.add(new JLabel("Check-in Date:"));
        txtCheckInDate = new JTextField();
        panel.add(txtCheckInDate);
        
        panel.add(new JLabel("Check-out Date:"));
        txtCheckOutDate = new JTextField();
        panel.add(txtCheckOutDate);
        
        btnSearchRooms = new JButton("Search Available Rooms");
        panel.add(btnSearchRooms);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Available Rooms"));
        
        String[] columns = {"Room No", "Type", "Floor", "Price/Night", "Select"};
        tableModel = new DefaultTableModel(columns, 0);
        roomsTable = new JTable(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(roomsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnConfirm = new JButton("Confirm Reservation");
        btnCancel = new JButton("Cancel");
        
        btnConfirm.setBackground(new Color(34, 139, 34));
        btnConfirm.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(220, 53, 69));
        btnCancel.setForeground(Color.WHITE);
        
        btnConfirm.addActionListener(e -> confirmReservation());
        btnCancel.addActionListener(e -> dispose());
        
        panel.add(btnCancel);
        panel.add(btnConfirm);
        
        return panel;
    }
    
    private void confirmReservation() {
        // Implementation for confirming reservation
        JOptionPane.showMessageDialog(this, "Reservation Created Successfully!");
        dispose();
    }
}