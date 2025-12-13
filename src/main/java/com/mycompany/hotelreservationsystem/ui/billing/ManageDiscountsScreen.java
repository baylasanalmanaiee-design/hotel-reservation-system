
package com.mycompany.hotelreservationsystem.ui.billing;


import com.mycompany.hotelreservationsystem.dao.DiscountDAO;
import com.mycompany.hotelreservationsystem.model.Discount;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class ManageDiscountsScreen extends JDialog {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtCode, txtDesc, txtPct, txtStart, txtEnd;
    private JCheckBox chkActive;

    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnClose;

    private final DiscountDAO discountDAO = new DiscountDAO();

    public ManageDiscountsScreen(JFrame parent) {
        super(parent, "Manage Discounts", true);
        setSize(900, 520);
        setLocationRelativeTo(parent);
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // ====== Table ======
        model = new DefaultTableModel(
                new String[]{"ID", "Code", "Description", "Percent", "Active", "Start Date", "End Date"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(24);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromSelectedRow();
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ====== Form ======
        JPanel form = new JPanel(new GridLayout(2, 1, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Discount Details"));

        JPanel row1 = new JPanel(new GridLayout(1, 6, 10, 10));
        row1.add(new JLabel("Code:"));
        txtCode = new JTextField();
        row1.add(txtCode);

        row1.add(new JLabel("Percent (%):"));
        txtPct = new JTextField();
        row1.add(txtPct);

        row1.add(new JLabel("Active:"));
        chkActive = new JCheckBox("Enabled");
        chkActive.setSelected(true);
        row1.add(chkActive);

        JPanel row2 = new JPanel(new GridLayout(1, 6, 10, 10));
        row2.add(new JLabel("Description:"));
        txtDesc = new JTextField();
        row2.add(txtDesc);

        row2.add(new JLabel("Start (yyyy-MM-dd):"));
        txtStart = new JTextField();
        row2.add(txtStart);

        row2.add(new JLabel("End (yyyy-MM-dd):"));
        txtEnd = new JTextField();
        row2.add(txtEnd);

        form.add(row1);
        form.add(row2);

        add(form, BorderLayout.NORTH);

        // ====== Buttons ======
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear");
        btnClose = new JButton("Close");

        btnAdd.addActionListener(e -> addDiscount());
        btnUpdate.addActionListener(e -> updateDiscount());
        btnDelete.addActionListener(e -> deleteDiscount());
        btnClear.addActionListener(e -> clearForm());
        btnClose.addActionListener(e -> dispose());

        buttons.add(btnClear);
        buttons.add(btnDelete);
        buttons.add(btnUpdate);
        buttons.add(btnAdd);
        buttons.add(btnClose);

        add(buttons, BorderLayout.SOUTH);
    }

    private void loadData() {
        model.setRowCount(0);
        for (Discount d : discountDAO.getAll()) {
            model.addRow(new Object[]{
                    d.getId(),
                    d.getCode(),
                    d.getDescription(),
                    d.getPercentage(),
                    d.isActive() ? "YES" : "NO",
                    d.getStartDate(),
                    d.getEndDate()
            });
        }
    }

    private void fillFormFromSelectedRow() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        txtCode.setText(String.valueOf(model.getValueAt(r, 1)));
        txtDesc.setText(String.valueOf(model.getValueAt(r, 2)));
        txtPct.setText(String.valueOf(model.getValueAt(r, 3)));
        chkActive.setSelected("YES".equals(String.valueOf(model.getValueAt(r, 4))));
        txtStart.setText(valueOrEmpty(model.getValueAt(r, 5)));
        txtEnd.setText(valueOrEmpty(model.getValueAt(r, 6)));
    }

    private String valueOrEmpty(Object o) {
        return (o == null) ? "" : String.valueOf(o);
    }

    private void addDiscount() {
        Discount d = readFormAsDiscount(-1);
        if (d == null) return;

        if (discountDAO.insert(d)) {
            JOptionPane.showMessageDialog(this, "✅ Discount added.");
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Failed to add discount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDiscount() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a discount row first.");
            return;
        }
        int id = Integer.parseInt(String.valueOf(model.getValueAt(r, 0)));

        Discount d = readFormAsDiscount(id);
        if (d == null) return;

        if (discountDAO.update(d)) {
            JOptionPane.showMessageDialog(this, "✅ Discount updated.");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Failed to update discount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDiscount() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a discount row first.");
            return;
        }
        int id = Integer.parseInt(String.valueOf(model.getValueAt(r, 0)));

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this discount?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (discountDAO.deleteById(id)) {
            JOptionPane.showMessageDialog(this, "✅ Discount deleted.");
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Failed to delete discount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtCode.setText("");
        txtDesc.setText("");
        txtPct.setText("");
        chkActive.setSelected(true);
        txtStart.setText("");
        txtEnd.setText("");
        table.clearSelection();
    }

    private Discount readFormAsDiscount(int id) {
        String code = txtCode.getText().trim();
        String desc = txtDesc.getText().trim();
        String pctText = txtPct.getText().trim();
        String start = txtStart.getText().trim();
        String end = txtEnd.getText().trim();

        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Code is required.");
            return null;
        }

        double pct;
        try {
            pct = Double.parseDouble(pctText);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Percent must be numeric.");
            return null;
        }
        if (pct <= 0 || pct > 100) {
            JOptionPane.showMessageDialog(this, "Percent must be between 1 and 100.");
            return null;
        }

        // Validate dates if provided
        if (!start.isEmpty()) {
            try { LocalDate.parse(start); }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Start date must be yyyy-MM-dd");
                return null;
            }
        }
        if (!end.isEmpty()) {
            try { LocalDate.parse(end); }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this, "End date must be yyyy-MM-dd");
                return null;
            }
        }

        // If one date is filled, better both (seasonal). But allow single if you want.
        // Here: allow any.

        Discount d = new Discount();
        d.setId(id);
        d.setCode(code);
        d.setDescription(desc.isEmpty() ? null : desc);
        d.setPercentage(pct);
        d.setActive(chkActive.isSelected());
        d.setStartDate(start.isEmpty() ? null : start);
        d.setEndDate(end.isEmpty() ? null : end);
        return d;
    }
}
