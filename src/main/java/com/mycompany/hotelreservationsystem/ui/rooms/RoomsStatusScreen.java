/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.ui.rooms;

/**
 *
 * @author abeer
 */

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoomsStatusScreen extends JDialog {
    private JTable roomsTable;
    private DefaultTableModel tableModel;
    private JButton btnRefresh, btnUpdateStatus;
    
    public RoomsStatusScreen(JFrame parent) {
        super(parent, "Rooms Status", true);
        setSize(800, 500);
        setLocationRelativeTo(parent);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Table Panel
        JPanel tablePanel = createTablePanel();
        
        // Buttons Panel
        JPanel buttonPanel = createButtonPanel();
        
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        loadRoomsData();
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"Room No", "Type", "Floor", "Status", "Last Update"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        roomsTable = new JTable(tableModel);
        roomsTable.setRowHeight(25);
        roomsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set custom renderer for status column
        roomsTable.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());
        
        JScrollPane scrollPane = new JScrollPane(roomsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnRefresh = new JButton("Refresh");
        btnUpdateStatus = new JButton("Update Status");
        
        btnRefresh.setBackground(new Color(108, 117, 125));
        btnRefresh.setForeground(Color.WHITE);
        btnUpdateStatus.setBackground(new Color(40, 167, 69));
        btnUpdateStatus.setForeground(Color.WHITE);
        
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });
        
        btnUpdateStatus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRoomStatus();
            }
        });
        
        panel.add(btnRefresh);
        panel.add(btnUpdateStatus);
        
        return panel;
    }
    
    private void loadRoomsData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Sample data - replace with actual database data
        Object[][] sampleData = {
            {"101", "Single", "1", "Available", "2024-01-15 10:30"},
            {"102", "Single", "1", "Occupied", "2024-01-15 14:20"},
            {"103", "Double", "1", "Cleaning Required", "2024-01-15 11:45"},
            {"104", "Double", "1", "Available", "2024-01-15 09:15"},
            {"105", "Suite", "1", "Maintenance", "2024-01-14 16:00"},
            {"201", "Double", "2", "Available", "2024-01-15 08:00"},
            {"202", "Suite", "2", "Occupied", "2024-01-15 12:30"},
            {"203", "Suite", "2", "Available", "2024-01-15 07:45"},
            {"301", "Single", "3", "Cleaning Required", "2024-01-15 13:15"},
            {"302", "Double", "3", "Occupied", "2024-01-15 15:00"}
        };
        
        for (Object[] row : sampleData) {
            tableModel.addRow(row);
        }
    }
    
    private void refreshData() {
        loadRoomsData();
        JOptionPane.showMessageDialog(this, "Rooms data refreshed successfully!", "Refresh", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateRoomStatus() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a room first to update its status.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String roomNo = tableModel.getValueAt(selectedRow, 0).toString();
        String currentStatus = tableModel.getValueAt(selectedRow, 3).toString();
        String roomType = tableModel.getValueAt(selectedRow, 1).toString();
        
        String[] statusOptions = {"Available", "Occupied", "Cleaning Required", "Maintenance"};
        String newStatus = (String) JOptionPane.showInputDialog(
            this,
            "Update status for Room " + roomNo + " (" + roomType + ")\nCurrent Status: " + currentStatus,
            "Update Room Status - Room " + roomNo,
            JOptionPane.QUESTION_MESSAGE,
            null,
            statusOptions,
            currentStatus
        );
        
        if (newStatus != null && !newStatus.equals(currentStatus)) {
            // Update the status in the table
            tableModel.setValueAt(newStatus, selectedRow, 3);
            tableModel.setValueAt(java.time.LocalDateTime.now().toString().replace("T", " ").substring(0, 16), selectedRow, 4);
            
            JOptionPane.showMessageDialog(this, 
                "Room " + roomNo + " status updated to: " + newStatus, 
                "Status Updated", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Custom renderer for status column
    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected) {
                String status = value.toString();
                switch (status) {
                    case "Available":
                        c.setBackground(new Color(144, 238, 144)); // Light green
                        c.setForeground(Color.BLACK);
                        break;
                    case "Occupied":
                        c.setBackground(new Color(255, 182, 193)); // Light red
                        c.setForeground(Color.BLACK);
                        break;
                    case "Cleaning Required":
                        c.setBackground(new Color(255, 255, 153)); // Light yellow
                        c.setForeground(Color.BLACK);
                        break;
                    case "Maintenance":
                        c.setBackground(new Color(173, 216, 230)); // Light blue
                        c.setForeground(Color.BLACK);
                        break;
                    default:
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                }
            } else {
                c.setBackground(table.getSelectionBackground());
                c.setForeground(table.getSelectionForeground());
            }
            
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            
            return c;
        }
    }
    
    // Method to get room status count (useful for statistics)
    public java.util.Map<String, Integer> getRoomStatusCount() {
        java.util.Map<String, Integer> statusCount = new java.util.HashMap<>();
        statusCount.put("Available", 0);
        statusCount.put("Occupied", 0);
        statusCount.put("Cleaning Required", 0);
        statusCount.put("Maintenance", 0);
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String status = tableModel.getValueAt(i, 3).toString();
            statusCount.put(status, statusCount.get(status) + 1);
        }
        
        return statusCount;
    }
    
    // main method for testing
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        RoomsStatusScreen roomsScreen = new RoomsStatusScreen(frame);
        roomsScreen.setVisible(true);
    }
}