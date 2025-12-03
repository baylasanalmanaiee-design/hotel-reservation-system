/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.ui.rooms;

/**
 *
 * @author abeer
 */

import com.mycompany.hotelreservationsystem.dao.RoomTypeDAO;
import com.mycompany.hotelreservationsystem.model.RoomType;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ManageRoomTypesScreen extends JDialog {
    private JTable typesTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh;
    private RoomTypeDAO roomTypeDAO;
    
    public ManageRoomTypesScreen(JFrame parent) {
        super(parent, "Manage Room Types", true);
        setSize(600, 400);
        setLocationRelativeTo(parent);
        
        roomTypeDAO = new RoomTypeDAO();
        initializeComponents();
        loadRoomTypesData();
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
        
        String[] columns = {"Type ID", "Type Name", "Base Price"};
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
        
        typesTable = new JTable(tableModel);
        typesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(typesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        btnAdd = new JButton("Add Room Type");
        btnEdit = new JButton("Edit Room Type");
        btnDelete = new JButton("Delete Room Type");
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
                addNewRoomType();
            }
        });
        
        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedRoomType();
            }
        });
        
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedRoomType();
            }
        });
        
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadRoomTypesData();
            }
        });
        
        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnRefresh);
        
        return panel;
    }
    
    private void loadRoomTypesData() {
        tableModel.setRowCount(0);
        
        try {
            List<RoomType> roomTypes = roomTypeDAO.getAllRoomTypes();
            if (roomTypes.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No room types found in database.", 
                    "Info", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
            for (RoomType type : roomTypes) {
                Object[] row = {
                    type.getId(),
                    type.getName(),
                    String.format("$%.2f", type.getBasePrice())
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading room types: " + e.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addNewRoomType() {
        JDialog addDialog = new JDialog(this, "Add New Room Type", true);
        addDialog.setSize(350, 200);
        addDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JTextField txtTypeName = new JTextField();
        JTextField txtBasePrice = new JTextField();
        
        panel.add(new JLabel("Type Name:"));
        panel.add(txtTypeName);
        panel.add(new JLabel("Base Price:"));
        panel.add(txtBasePrice);
        
        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
        
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // التحقق من المدخلات
                    if (txtTypeName.getText().trim().isEmpty() || 
                        txtBasePrice.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(addDialog, 
                            "Please fill all required fields!", 
                            "Validation Error", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    String typeName = txtTypeName.getText();
                    double basePrice = Double.parseDouble(txtBasePrice.getText());
                    
                    // التحقق من أن السعر موجب
                    if (basePrice <= 0) {
                        JOptionPane.showMessageDialog(addDialog, 
                            "Base price must be greater than 0!", 
                            "Input Error", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // إنشاء كائن RoomType جديد
                    RoomType roomType = new RoomType();
                    roomType.setName(typeName);
                    roomType.setBasePrice(basePrice);
                    
                    // استخدام DAO لإضافة نوع الغرفة
                    int newId = roomTypeDAO.addRoomType(roomType);
                    
                    if (newId != -1) {
                        JOptionPane.showMessageDialog(addDialog, 
                            "Room type added successfully! ID: " + newId,
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                        addDialog.dispose();
                        loadRoomTypesData();
                    } else {
                        JOptionPane.showMessageDialog(addDialog, 
                            "Failed to add room type!", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addDialog, 
                        "Please enter valid number for base price!", 
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
        
        // Empty panel for alignment
        panel.add(new JLabel());
        panel.add(new JLabel());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);
        
        addDialog.setLayout(new BorderLayout());
        addDialog.add(panel, BorderLayout.CENTER);
        addDialog.add(buttonPanel, BorderLayout.SOUTH);
        addDialog.setVisible(true);
    }
    
    private void editSelectedRoomType() {
        int selectedRow = typesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room type to edit.");
            return;
        }
        
        int typeId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        
        // جلب بيانات نوع الغرفة من الداتابيز
        RoomType selectedType = roomTypeDAO.getRoomTypeById(typeId);
        if (selectedType == null) {
            JOptionPane.showMessageDialog(this, 
                "Room type not found in database!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog editDialog = new JDialog(this, "Edit Room Type", true);
        editDialog.setSize(350, 200);
        editDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JTextField txtTypeName = new JTextField(selectedType.getName());
        JTextField txtBasePrice = new JTextField(String.valueOf(selectedType.getBasePrice()));
        
        panel.add(new JLabel("Type Name:"));
        panel.add(txtTypeName);
        panel.add(new JLabel("Base Price:"));
        panel.add(txtBasePrice);
        
        JButton btnSave = new JButton("Save Changes");
        JButton btnCancel = new JButton("Cancel");
        
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // تحديث بيانات نوع الغرفة
                    selectedType.setName(txtTypeName.getText());
                    selectedType.setBasePrice(Double.parseDouble(txtBasePrice.getText()));
                    
                    // التحقق من أن السعر موجب
                    if (selectedType.getBasePrice() <= 0) {
                        JOptionPane.showMessageDialog(editDialog, 
                            "Base price must be greater than 0!", 
                            "Input Error", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // استخدام DAO لتحديث نوع الغرفة
                    boolean success = roomTypeDAO.updateRoomType(selectedType);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(editDialog, 
                            "Room type updated successfully!", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                        editDialog.dispose();
                        loadRoomTypesData(); // إعادة تحميل البيانات
                    } else {
                        JOptionPane.showMessageDialog(editDialog, 
                            "Failed to update room type!", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(editDialog, 
                        "Please enter valid number for base price!", 
                        "Input Error", 
                        JOptionPane.WARNING_MESSAGE);
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
        
        // Empty panel for alignment
        panel.add(new JLabel());
        panel.add(new JLabel());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);
        
        editDialog.setLayout(new BorderLayout());
        editDialog.add(panel, BorderLayout.CENTER);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);
        editDialog.setVisible(true);
    }
    
    private void deleteSelectedRoomType() {
        int selectedRow = typesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room type to delete.");
            return;
        }
        
        int typeId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String typeName = tableModel.getValueAt(selectedRow, 1).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete room type?\n" +
                "Type Name: " + typeName + "\n" +
                "Type ID: " + typeId,
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = roomTypeDAO.deleteRoomType(typeId);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Room type deleted successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadRoomTypesData(); // إعادة تحميل البيانات
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete room type!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting room type: " + e.getMessage(),
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}