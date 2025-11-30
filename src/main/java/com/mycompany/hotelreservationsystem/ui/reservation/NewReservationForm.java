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

public class NewReservationForm extends JFrame {

    public NewReservationForm() {

        setTitle("New Reservation");
        setSize(420, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel guestLabel = new JLabel("Guest Name:");
        guestLabel.setBounds(40, 30, 120, 25);

        JTextField guestField = new JTextField();
        guestField.setBounds(160, 30, 180, 25);

        JLabel typeLabel = new JLabel("Room Type:");
        typeLabel.setBounds(40, 70, 120, 25);

        JComboBox<String> typeBox = new JComboBox<>();
        typeBox.setBounds(160, 70, 180, 25);
        // ارجع اخذها من قاعدة البيانات بعدين 

        JLabel inLabel = new JLabel("Check-In Date:");
        inLabel.setBounds(40, 110, 120, 25);

        JTextField inField = new JTextField();
        inField.setBounds(160, 110, 180, 25);

        JLabel outLabel = new JLabel("Check-Out Date:");
        outLabel.setBounds(40, 150, 120, 25);

        JTextField outField = new JTextField();
        outField.setBounds(160, 150, 180, 25);

        JLabel stayLabel = new JLabel("Stay Duration:");
        stayLabel.setBounds(40, 190, 120, 25);

        JTextField stayField = new JTextField();
        stayField.setBounds(160, 190, 180, 25);
        stayField.setEditable(false);

        JButton checkBtn = new JButton("Check Availability");
        checkBtn.setBounds(120, 230, 180, 30);

        //     فقط رسالة عامة
        checkBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Checking availability (to be implemented)")
        );

        add(guestLabel);
        add(guestField);
        add(typeLabel);
        add(typeBox);
        add(inLabel);
        add(inField);
        add(outLabel);
        add(outField);
        add(stayLabel);
        add(stayField);
        add(checkBtn);

        setVisible(true);
    }
}
