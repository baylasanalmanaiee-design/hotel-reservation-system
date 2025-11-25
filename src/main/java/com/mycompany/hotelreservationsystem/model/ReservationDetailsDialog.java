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
import java.awt.*;

public class ReservationDetailsDialog extends JDialog {

    public ReservationDetailsDialog(int id, String guestName, String roomType,
                                    String roomNo, String checkIn, String checkOut,
                                    String status) {

        setTitle("Reservation Details");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setModal(true);

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.anchor = GridBagConstraints.WEST;

        int y = 0;

        gc.gridx = 0; gc.gridy = y; p.add(new JLabel("Reservation ID:"), gc);
        gc.gridx = 1; p.add(new JLabel(String.valueOf(id)), gc); y++;

        gc.gridx = 0; gc.gridy = y; p.add(new JLabel("Guest Name:"), gc);
        gc.gridx = 1; p.add(new JLabel(guestName), gc); y++;

        gc.gridx = 0; gc.gridy = y; p.add(new JLabel("Room Type:"), gc);
        gc.gridx = 1; p.add(new JLabel(roomType), gc); y++;

        gc.gridx = 0; gc.gridy = y; p.add(new JLabel("Room No:"), gc);
        gc.gridx = 1; p.add(new JLabel(roomNo), gc); y++;

        gc.gridx = 0; gc.gridy = y; p.add(new JLabel("Check-In:"), gc);
        gc.gridx = 1; p.add(new JLabel(checkIn), gc); y++;

        gc.gridx = 0; gc.gridy = y; p.add(new JLabel("Check-Out:"), gc);
        gc.gridx = 1; p.add(new JLabel(checkOut), gc); y++;

        gc.gridx = 0; gc.gridy = y; p.add(new JLabel("Status:"), gc);
        gc.gridx = 1; p.add(new JLabel(status), gc); y++;

        JPanel btns = new JPanel();
        JButton modifyBtn = new JButton("Modify");
        JButton cancelBtn = new JButton("Cancel Reservation");
        JButton closeBtn = new JButton("Close");

        btns.add(modifyBtn);
        btns.add(cancelBtn);
        btns.add(closeBtn);

        modifyBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this,"Modify action (GUI only – no backend)."));

        cancelBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this,"Reservation cancelled (GUI only – no backend)."));

        closeBtn.addActionListener(e -> dispose());

        add(p, BorderLayout.CENTER);
        add(btns, BorderLayout.SOUTH);
    }
}

