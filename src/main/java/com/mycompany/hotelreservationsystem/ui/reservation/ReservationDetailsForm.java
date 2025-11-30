/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.ui.reservation;

/**
 *
 * @author Bilsan
 */
import javax.swing.*;

public class ReservationDetailsForm extends JFrame {

    public ReservationDetailsForm() {

        setTitle("Reservation Details");
        setSize(420, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel idLabel = new JLabel("Reservation ID:");
        idLabel.setBounds(40, 40, 300, 25);

        JLabel guestLabel = new JLabel("Guest:");
        guestLabel.setBounds(40, 80, 300, 25);

        JLabel roomLabel = new JLabel("Room Type:");
        roomLabel.setBounds(40, 120, 300, 25);

        JLabel dateLabel = new JLabel("Stay:");
        dateLabel.setBounds(40, 160, 300, 25);

        JButton modifyBtn = new JButton("Modify");
        modifyBtn.setBounds(80, 210, 100, 30);
        modifyBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Modify action (to be implemented)") );// ارجع اعدل عليه بعدين

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBounds(200, 210, 100, 30);
        cancelBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Cancel action (to be implemented)"));// ارجع اعدل عليه 

        add(idLabel);
        add(guestLabel);
        add(roomLabel);
        add(dateLabel);
        add(modifyBtn);
        add(cancelBtn);

        setVisible(true);
    }
}
