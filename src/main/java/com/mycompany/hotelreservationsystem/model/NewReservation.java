/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.model;

/**
 *
 * @author Bilsan
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class NewReservation extends JFrame {

    JTextField guestNameFld = new JTextField(15);
    JTextField phoneFld = new JTextField(15);
    JTextField emailFld = new JTextField(15);

    JComboBox<String> roomTypeBox = new JComboBox<>(new String[]{"Single", "Double", "Suite"});

    JTextField checkInFld = new JTextField("yyyy-MM-dd", 10);
    JTextField checkOutFld = new JTextField("yyyy-MM-dd", 10);

    JButton searchAvailBtn = new JButton("Search Availability");
    JButton confirmBtn = new JButton("Confirm Reservation");
    JButton cancelBtn = new JButton("Cancel");

    DefaultTableModel availModel = new DefaultTableModel(new Object[]{"Room No"}, 0);
    JTable availTable = new JTable(availModel);

    public NewReservation() {

        setTitle("New Reservation");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.anchor = GridBagConstraints.WEST;

        int y = 0;
        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Guest Name:"), gc);
        gc.gridx = 1; form.add(guestNameFld, gc); y++;

        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Phone:"), gc);
        gc.gridx = 1; form.add(phoneFld, gc); y++;

        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Email:"), gc);
        gc.gridx = 1; form.add(emailFld, gc); y++;

        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Room Type:"), gc);
        gc.gridx = 1; form.add(roomTypeBox, gc); y++;

        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Check-In:"), gc);
        gc.gridx = 1; form.add(checkInFld, gc); y++;

        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Check-Out:"), gc);
        gc.gridx = 1; form.add(checkOutFld, gc); y++;

        gc.gridx = 0; gc.gridy = y; form.add(searchAvailBtn, gc);
        gc.gridx = 1; form.add(confirmBtn, gc);
        gc.gridx = 2; form.add(cancelBtn, gc);

        setLayout(new BorderLayout());
        add(form, BorderLayout.NORTH);
        add(new JScrollPane(availTable), BorderLayout.CENTER);

            searchAvailBtn.addActionListener(e -> {
            availModel.setRowCount(0);
            availModel.addRow(new Object[]{101});
            availModel.addRow(new Object[]{102});
            availModel.addRow(new Object[]{103});

            JOptionPane.showMessageDialog(this, "Available rooms displayed ");
        });

        confirmBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Reservation confirmed ");
        });

        cancelBtn.addActionListener(e -> {
            guestNameFld.setText("");
            phoneFld.setText("");
            emailFld.setText("");
            checkInFld.setText("yyyy-MM-dd");
            checkOutFld.setText("yyyy-MM-dd");
            availModel.setRowCount(0);
        });
    }
      public static void main(String[] args) {
        new NewReservation().setVisible(true);
    }
}

  

