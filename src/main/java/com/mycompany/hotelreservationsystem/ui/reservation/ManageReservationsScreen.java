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

public class ManageReservationsScreen extends JDialog {
    private JTextField txtSearch;
    private JButton btnSearch, btnView, btnEdit, btnCancel, btnCheckIn, btnCheckOut;
    private JTable reservationsTable;
    private DefaultTableModel tableModel;
    
    public ManageReservationsScreen(JFrame parent) {
        super(parent, "Manage Reservations", true);
        setSize(1000, 600);
        setLocationRelativeTo(parent);
        
        initializeComponents();
        loadSampleData();
    }
    
    private void initializeComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Search Panel
        JPanel searchPanel = createSearchPanel();
        
        // Table Panel
        JPanel tablePanel = createTablePanel();
        
        // Buttons Panel
        JPanel buttonPanel = createButtonPanel();
        
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Search Reservations"));
        
        panel.add(new JLabel("Search:"));
        txtSearch = new JTextField(30);
        panel.add(txtSearch);
        
        btnSearch = new JButton("Search");
        btnSearch.setBackground(new Color(70, 130, 180));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchReservations();
            }
        });
        panel.add(btnSearch);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"Reservation ID", "Guest Name", "Room No", "Check-in", "Check-out", "Status"};
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
        reservationsTable = new JTable(tableModel);
        reservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        btnView = new JButton("View Details");
        btnEdit = new JButton("Edit");
        btnCancel = new JButton("Cancel Reservation");
        btnCheckIn = new JButton("Go to Check-In");
        btnCheckOut = new JButton("Go to Check-Out");
        
        // Style buttons
        styleActionButton(btnView);
        styleActionButton(btnEdit);
        btnCancel.setBackground(Color.ORANGE);
        btnCancel.setForeground(Color.BLACK);
        btnCheckIn.setBackground(new Color(40, 167, 69));
        btnCheckIn.setForeground(Color.WHITE);
        btnCheckOut.setBackground(new Color(23, 162, 184));
        btnCheckOut.setForeground(Color.WHITE);
        
        // Add action listeners
        btnView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewReservationDetails();
            }
        });
        
        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editReservation();
            }
        });
        
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelReservation();
            }
        });
        
        btnCheckIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToCheckIn();
            }
        });
        
        btnCheckOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToCheckOut();
            }
        });
        
        panel.add(btnView);
        panel.add(btnEdit);
        panel.add(btnCancel);
        panel.add(btnCheckIn);
        panel.add(btnCheckOut);
        
        return panel;
    }
    
    private void styleActionButton(JButton button) {
        button.setBackground(new Color(108, 117, 125));
        button.setForeground(Color.WHITE);
    }
    
    private void loadSampleData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Sample data - replace with actual database data
        Object[] row1 = {"RES001", "John Smith", "101", "2024-01-15", "2024-01-20", "Booked"};
        Object[] row2 = {"RES002", "Sarah Johnson", "205", "2024-01-16", "2024-01-18", "Checked-in"};
        Object[] row3 = {"RES003", "Mike Davis", "302", "2024-01-17", "2024-01-22", "Booked"};
        Object[] row4 = {"RES004", "Emily Wilson", "104", "2024-01-18", "2024-01-19", "Checked-in"};
        
        tableModel.addRow(row1);
        tableModel.addRow(row2);
        tableModel.addRow(row3);
        tableModel.addRow(row4);
    }
    
    private void searchReservations() {
        String searchTerm = txtSearch.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadSampleData();
            return;
        }
        
        // Simple search implementation
        tableModel.setRowCount(0);
        
        Object[][] allData = {
            {"RES001", "John Smith", "101", "2024-01-15", "2024-01-20", "Booked"},
            {"RES002", "Sarah Johnson", "205", "2024-01-16", "2024-01-18", "Checked-in"},
            {"RES003", "Mike Davis", "302", "2024-01-17", "2024-01-22", "Booked"},
            {"RES004", "Emily Wilson", "104", "2024-01-18", "2024-01-19", "Checked-in"}
        };
        
        for (Object[] row : allData) {
            boolean match = false;
            for (Object cell : row) {
                if (cell.toString().toLowerCase().contains(searchTerm)) {
                    match = true;
                    break;
                }
            }
            if (match) {
                tableModel.addRow(row);
            }
        }
    }
    
    private void viewReservationDetails() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow != -1) {
            String reservationId = tableModel.getValueAt(selectedRow, 0).toString();
            JOptionPane.showMessageDialog(this, 
                "Viewing details for reservation: " + reservationId + "\n" +
                "Guest: " + tableModel.getValueAt(selectedRow, 1) + "\n" +
                "Room: " + tableModel.getValueAt(selectedRow, 2) + "\n" +
                "Status: " + tableModel.getValueAt(selectedRow, 5),
                "Reservation Details",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a reservation first.");
        }
    }
    
    private void editReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow != -1) {
            String reservationId = tableModel.getValueAt(selectedRow, 0).toString();
            JOptionPane.showMessageDialog(this, 
                "Edit reservation: " + reservationId + "\n(Edit feature would be implemented here)");
        } else {
            JOptionPane.showMessageDialog(this, "Please select a reservation first.");
        }
    }
    
    private void cancelReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow != -1) {
            String reservationId = tableModel.getValueAt(selectedRow, 0).toString();
            String guestName = tableModel.getValueAt(selectedRow, 1).toString();
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel reservation?\n" +
                "Reservation: " + reservationId + "\n" +
                "Guest: " + guestName,
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                // Update status in table
                tableModel.setValueAt("Cancelled", selectedRow, 5);
                JOptionPane.showMessageDialog(this, "Reservation cancelled successfully!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a reservation first.");
        }
    }
    
    private void goToCheckIn() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow != -1) {
            String status = tableModel.getValueAt(selectedRow, 5).toString();
            if (!status.equals("Booked")) {
                JOptionPane.showMessageDialog(this, 
                    "Only 'Booked' reservations can be checked in.\nCurrent status: " + status);
                return;
            }
            
            // open Check-In screen
            JOptionPane.showMessageDialog(this, 
                "Opening Check-In screen for selected reservation.\n" +
                "(Check-In screen would open here)");
                
        } else {
            JOptionPane.showMessageDialog(this, "Please select a reservation first.");
        }
    }
    
    private void goToCheckOut() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow != -1) {
            String status = tableModel.getValueAt(selectedRow, 5).toString();
            if (!status.equals("Checked-in")) {
                JOptionPane.showMessageDialog(this, 
                    "Only 'Checked-in' reservations can be checked out.\nCurrent status: " + status);
                return;
            }
            
            // open Check-Out screen
            try {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                new com.mycompany.hotelreservationsystem.ui.billing.CheckOutScreen(parentFrame).setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Check-Out screen: " + e.getMessage() + "\n" +
                    "Make sure CheckOutScreen is in the correct package.");
            }
            
        } else {
            JOptionPane.showMessageDialog(this, "Please select a reservation first.");
        }
    }
    
    // main method for test if I need
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        ManageReservationsScreen screen = new ManageReservationsScreen(frame);
        screen.setVisible(true);
    }
}