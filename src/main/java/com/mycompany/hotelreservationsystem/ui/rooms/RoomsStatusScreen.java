/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.ui.rooms;

/**
 *
 * @author abeer
 */

import com.mycompany.hotelreservationsystem.dao.RoomDAO;
import com.mycompany.hotelreservationsystem.model.Room;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RoomsStatusScreen extends JDialog {
    private JTable roomsTable;
    private DefaultTableModel tableModel;
    private JButton btnRefresh, btnUpdateStatus;
    private RoomDAO roomDAO;
    
    public RoomsStatusScreen(JFrame parent) {
        super(parent, "Rooms Status", true);
        setSize(800, 500);
        setLocationRelativeTo(parent);
        
        roomDAO = new RoomDAO();
        initializeComponents();
        loadRoomsData();
    }
    
    private void initializeComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Table Panel
        JPanel tablePanel = createTablePanel();
        
        // Buttons Panel
        JPanel buttonPanel = createButtonPanel();
        
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"Room No", "Room ID", "Room Type ID", "Status"};
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
        tableModel.setRowCount(0);
        
        try {
            List<Room> rooms = roomDAO.getAllRooms();
            if (rooms.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No rooms found in database.", 
                    "Info", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            for (Room room : rooms) {
                Object[] row = {
                    room.getRoomNumber(),
                    room.getId(),
                    room.getRoomTypeId(),
                    room.getStatus()
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading rooms: " + e.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshData() {
        loadRoomsData();
        JOptionPane.showMessageDialog(this, 
            "Rooms data refreshed successfully!", 
            "Refresh", 
            JOptionPane.INFORMATION_MESSAGE);
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
        
        int roomNumber = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        int roomId = Integer.parseInt(tableModel.getValueAt(selectedRow, 1).toString());
        String currentStatus = tableModel.getValueAt(selectedRow, 3).toString();
        int roomTypeId = Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString());
        
        String[] statusOptions = {"Available", "Occupied", "Cleaning Required", "Maintenance"};
        String newStatus = (String) JOptionPane.showInputDialog(
            this,
            "Update status for Room " + roomNumber + 
            "\nRoom ID: " + roomId + 
            "\nType ID: " + roomTypeId +
            "\nCurrent Status: " + currentStatus,
            "Update Room Status - Room " + roomNumber,
            JOptionPane.QUESTION_MESSAGE,
            null,
            statusOptions,
            currentStatus
        );
        
        if (newStatus != null && !newStatus.equals(currentStatus)) {
            try {
                // استخدام DAO لتحديث حالة الغرفة
                boolean success = roomDAO.updateRoomStatus(roomNumber, newStatus);
                
                if (success) {
                    // تحديث الجدول
                    tableModel.setValueAt(newStatus, selectedRow, 3);
                    
                    // تحديث وقت التحديث
                    String currentTime = java.time.LocalDateTime.now()
                        .toString().replace("T", " ").substring(0, 16);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Room " + roomNumber + " status updated to: " + newStatus + 
                        "\nUpdated at: " + currentTime, 
                        "Status Updated", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to update room status!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error updating status: " + e.getMessage(),
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
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
    
    // Method to get room status count
    public java.util.Map<String, Integer> getRoomStatusCount() {
        java.util.Map<String, Integer> statusCount = new java.util.HashMap<>();
        statusCount.put("Available", 0);
        statusCount.put("Occupied", 0);
        statusCount.put("Cleaning Required", 0);
        statusCount.put("Maintenance", 0);
        
        try {
            List<Room> rooms = roomDAO.getAllRooms();
            for (Room room : rooms) {
                String status = room.getStatus();
                statusCount.put(status, statusCount.getOrDefault(status, 0) + 1);
            }
        } catch (Exception e) {
            System.out.println("Error getting room status count: " + e.getMessage());
        }
        
        return statusCount;
    }
}