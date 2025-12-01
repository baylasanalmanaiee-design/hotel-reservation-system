/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.ui.rooms;

/**
 *
 * @author abeer
 */

import com.mycompany.hotelreservationsystem.model.RoomType;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ManageRoomTypesScreen extends JDialog {
    private JTable typesTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh;
    private List<RoomType> roomTypesList; // List لتخزين أنواع الغرف محلياً
    
    public ManageRoomTypesScreen(JFrame parent) {
        super(parent, "Manage Room Types", true);
        setSize(600, 400);
        setLocationRelativeTo(parent);
        
        roomTypesList = new ArrayList<>();
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
        roomTypesList.clear();
        
        // بيانات تجريبية باستخدام Model الحالي
        RoomType type1 = new RoomType(1, "Single", 100.00);
        RoomType type2 = new RoomType(2, "Double", 150.00);
        RoomType type3 = new RoomType(3, "Suite", 250.00);
        RoomType type4 = new RoomType(4, "Deluxe", 350.00);
        
        roomTypesList.add(type1);
        roomTypesList.add(type2);
        roomTypesList.add(type3);
        roomTypesList.add(type4);
        
        for (RoomType type : roomTypesList) {
            Object[] row = {
                type.getId(),
                type.getName(),
                String.format("$%.2f", type.getBasePrice())
            };
            tableModel.addRow(row);
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
                    // إنشاء كائن RoomType جديد
                    int newId = roomTypesList.size() + 1;
                    String typeName = txtTypeName.getText();
                    double basePrice = Double.parseDouble(txtBasePrice.getText());
                    
                    RoomType newRoomType = new RoomType(newId, typeName, basePrice);
                    
                    // إضافة للقائمة والجدول
                    roomTypesList.add(newRoomType);
                    
                    Object[] row = {
                        newRoomType.getId(),
                        newRoomType.getName(),
                        String.format("$%.2f", newRoomType.getBasePrice())
                    };
                    tableModel.addRow(row);
                    
                    JOptionPane.showMessageDialog(addDialog, "Room type added successfully!");
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
        
        // الحصول على الـ RoomType من القائمة
        RoomType selectedType = roomTypesList.get(selectedRow);
        
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
                    // تحديث كائن RoomType
                    selectedType.setName(txtTypeName.getText());
                    selectedType.setBasePrice(Double.parseDouble(txtBasePrice.getText()));
                    
                    // تحديث الجدول
                    tableModel.setValueAt(selectedType.getName(), selectedRow, 1);
                    tableModel.setValueAt(String.format("$%.2f", selectedType.getBasePrice()), selectedRow, 2);
                    
                    JOptionPane.showMessageDialog(editDialog, "Room type updated successfully!");
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
        
        int typeId = (int) tableModel.getValueAt(selectedRow, 0);
        String typeName = tableModel.getValueAt(selectedRow, 1).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete room type?\n" +
                "Type Name: " + typeName + "\n" +
                "Type ID: " + typeId,
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // حذف من القائمة والجدول
            roomTypesList.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Room type deleted successfully!");
        }
    }
}