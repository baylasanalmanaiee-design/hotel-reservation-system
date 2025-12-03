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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ManageRoomsScreen extends JDialog {
    private JTable roomsTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh;
    private RoomDAO roomDAO;
    
    public ManageRoomsScreen(JFrame parent) {
        super(parent, "Manage Rooms", true);
        setSize(700, 450);
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
                loadRoomsData();
            }
        });
        
        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnRefresh);
        
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
            }
            
            for (Room room : rooms) {
                Object[] row = {
                    room.getId(),
                    room.getRoomNumber(),
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
                    // التحقق من المدخلات
                    if (txtRoomNumber.getText().trim().isEmpty() || 
                        txtRoomTypeId.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(addDialog, 
                            "Please fill all required fields!", 
                            "Validation Error", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    int roomNumber = Integer.parseInt(txtRoomNumber.getText());
                    int roomTypeId = Integer.parseInt(txtRoomTypeId.getText());
                    String status = cmbStatus.getSelectedItem().toString();
                    
                    // إنشاء كائن Room جديد
                    Room newRoom = new Room();
                    newRoom.setRoomNumber(roomNumber);
                    newRoom.setRoomTypeId(roomTypeId);
                    newRoom.setStatus(status);
                    
                    // استخدام DAO لإضافة الغرفة
                    int newId = roomDAO.addRoom(newRoom);
                    
                    if (newId != -1) {
                        JOptionPane.showMessageDialog(addDialog, 
                            "Room added successfully! ID: " + newId,
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                        addDialog.dispose();
                        loadRoomsData();
                    } else {
                        JOptionPane.showMessageDialog(addDialog, 
                            "Failed to add room!", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addDialog, 
                        "Please enter valid numbers for room number and type ID!", 
                        "Input Error", 
                        JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(addDialog, 
                        "Error: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
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
        
        int roomId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        
        // جلب بيانات الغرفة من الداتابيز
        Room selectedRoom = roomDAO.getRoomById(roomId);
        if (selectedRoom == null) {
            JOptionPane.showMessageDialog(this, 
                "Room not found in database!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
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
                    // تحديث بيانات الغرفة
                    selectedRoom.setRoomNumber(Integer.parseInt(txtRoomNumber.getText()));
                    selectedRoom.setRoomTypeId(Integer.parseInt(txtRoomTypeId.getText()));
                    selectedRoom.setStatus(cmbStatus.getSelectedItem().toString());
                    
                    // استخدام DAO لتحديث الغرفة
                    boolean success = roomDAO.updateRoom(selectedRoom);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(editDialog, 
                            "Room updated successfully!", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                        editDialog.dispose();
                        loadRoomsData(); // إعادة تحميل البيانات
                    } else {
                        JOptionPane.showMessageDialog(editDialog, 
                            "Failed to update room!", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(editDialog, 
                        "Error: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
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
        
        int roomId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        int roomNumber = Integer.parseInt(tableModel.getValueAt(selectedRow, 1).toString());
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete room?\n" +
                "Room Number: " + roomNumber + "\n" +
                "Room ID: " + roomId,
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = roomDAO.deleteRoom(roomId);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Room deleted successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadRoomsData(); // إعادة تحميل البيانات
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete room!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting room: " + e.getMessage(),
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}