/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.ui.rooms;

/**
 *
 * @author abeer
 */


import com.mycompany.hotelreservationsystem.model.Room;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ManageRoomsScreen extends JDialog {
    private JTable roomsTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh;
    private List<Room> roomsList; // List لتخزين الغرف محلياً
    
    public ManageRoomsScreen(JFrame parent) {
        super(parent, "Manage Rooms", true);
        setSize(700, 450);
        setLocationRelativeTo(parent);
        
        roomsList = new ArrayList<>();
        initializeComponents();
        loadSampleData();
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
        
        String[] columns = {"Room ID", "Room Number", "Room Type ID", "Status"};
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
        roomsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(roomsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        btnAdd = new JButton("Add Room");
        btnEdit = new JButton("Edit Room");
        btnDelete = new JButton("Delete Room");
        btnRefresh = new JButton("Refresh");
        
        // Style buttons
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        btnEdit.setBackground(new Color(255, 193, 7));
        btnEdit.setForeground(Color.BLACK);
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        btnRefresh.setBackground(new Color(108, 117, 125));
        btnRefresh.setForeground(Color.WHITE);
        
        // Add action listeners
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewRoom();
            }
        });
        
        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedRoom();
            }
        });
        
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedRoom();
            }
        });
        
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSampleData();
            }
        });
        
        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnRefresh);
        
        return panel;
    }
    
    private void loadSampleData() {
        tableModel.setRowCount(0);
        roomsList.clear();
        
        // بيانات تجريبية باستخدام Model الحالي
        Room room1 = new Room(1, 101, 1, "Available");
        Room room2 = new Room(2, 102, 1, "Occupied");
        Room room3 = new Room(3, 201, 2, "Available");
        Room room4 = new Room(4, 202, 2, "Cleaning Required");
        Room room5 = new Room(5, 301, 3, "Maintenance");
        
        roomsList.add(room1);
        roomsList.add(room2);
        roomsList.add(room3);
        roomsList.add(room4);
        roomsList.add(room5);
        
        for (Room room : roomsList) {
            Object[] row = {
                room.getId(),
                room.getRoomNumber(),
                room.getRoomTypeId(),
                room.getStatus()
            };
            tableModel.addRow(row);
        }
    }
    
    private void addNewRoom() {
        JDialog addDialog = new JDialog(this, "Add New Room", true);
        addDialog.setSize(350, 200);
        addDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JTextField txtRoomNumber = new JTextField();
        JTextField txtRoomTypeId = new JTextField();
        
        panel.add(new JLabel("Room Number:"));
        panel.add(txtRoomNumber);
        panel.add(new JLabel("Room Type ID:"));
        panel.add(txtRoomTypeId);
        panel.add(new JLabel("Status:"));
        
        JComboBox<String> cmbStatus = new JComboBox<>(new String[]{
            "Available", "Occupied", "Cleaning Required", "Maintenance"
        });
        panel.add(cmbStatus);
        
        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
        
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // إنشاء كائن Room جديد
                    int newId = roomsList.size() + 1;
                    int roomNumber = Integer.parseInt(txtRoomNumber.getText());
                    int roomTypeId = Integer.parseInt(txtRoomTypeId.getText());
                    String status = cmbStatus.getSelectedItem().toString();
                    
                    Room newRoom = new Room(newId, roomNumber, roomTypeId, status);
                    
                    // إضافة للقائمة والجدول
                    roomsList.add(newRoom);
                    
                    Object[] row = {
                        newRoom.getId(),
                        newRoom.getRoomNumber(),
                        newRoom.getRoomTypeId(),
                        newRoom.getStatus()
                    };
                    tableModel.addRow(row);
                    
                    JOptionPane.showMessageDialog(addDialog, "Room added successfully!");
                    addDialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(addDialog, "Error: " + ex.getMessage());
                }
            }
        });
        
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDialog.dispose();
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);
        
        addDialog.setLayout(new BorderLayout());
        addDialog.add(panel, BorderLayout.CENTER);
        addDialog.add(buttonPanel, BorderLayout.SOUTH);
        addDialog.setVisible(true);
    }
    
    private void editSelectedRoom() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to edit.");
            return;
        }
        
        // الحصول على الـ Room من القائمة
        Room selectedRoom = roomsList.get(selectedRow);
        
        JDialog editDialog = new JDialog(this, "Edit Room", true);
        editDialog.setSize(350, 200);
        editDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JTextField txtRoomNumber = new JTextField(String.valueOf(selectedRoom.getRoomNumber()));
        JTextField txtRoomTypeId = new JTextField(String.valueOf(selectedRoom.getRoomTypeId()));
        
        panel.add(new JLabel("Room Number:"));
        panel.add(txtRoomNumber);
        panel.add(new JLabel("Room Type ID:"));
        panel.add(txtRoomTypeId);
        panel.add(new JLabel("Status:"));
        
        JComboBox<String> cmbStatus = new JComboBox<>(new String[]{
            "Available", "Occupied", "Cleaning Required", "Maintenance"
        });
        cmbStatus.setSelectedItem(selectedRoom.getStatus());
        panel.add(cmbStatus);
        
        JButton btnSave = new JButton("Save Changes");
        JButton btnCancel = new JButton("Cancel");
        
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // تحديث كائن Room
                    selectedRoom.setRoomNumber(Integer.parseInt(txtRoomNumber.getText()));
                    selectedRoom.setRoomTypeId(Integer.parseInt(txtRoomTypeId.getText()));
                    selectedRoom.setStatus(cmbStatus.getSelectedItem().toString());
                    
                    // تحديث الجدول
                    tableModel.setValueAt(selectedRoom.getRoomNumber(), selectedRow, 1);
                    tableModel.setValueAt(selectedRoom.getRoomTypeId(), selectedRow, 2);
                    tableModel.setValueAt(selectedRoom.getStatus(), selectedRow, 3);
                    
                    JOptionPane.showMessageDialog(editDialog, "Room updated successfully!");
                    editDialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(editDialog, "Error: " + ex.getMessage());
                }
            }
        });
        
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editDialog.dispose();
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);
        
        editDialog.setLayout(new BorderLayout());
        editDialog.add(panel, BorderLayout.CENTER);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);
        editDialog.setVisible(true);
    }
    
    private void deleteSelectedRoom() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to delete.");
            return;
        }
        
        int roomId = (int) tableModel.getValueAt(selectedRow, 0);
        int roomNumber = (int) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete room?\n" +
                "Room Number: " + roomNumber + "\n" +
                "Room ID: " + roomId,
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // حذف من القائمة والجدول
            roomsList.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Room deleted successfully!");
        }
    }
}