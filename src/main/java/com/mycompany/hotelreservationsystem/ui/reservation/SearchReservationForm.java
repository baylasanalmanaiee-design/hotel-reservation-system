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

public class SearchReservationForm extends JFrame {

    public SearchReservationForm() {

        setTitle("Search Reservation");
        setSize(380, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel idLabel = new JLabel("Reservation ID:");
        idLabel.setBounds(40, 40, 120, 25);

        JTextField idField = new JTextField();
        idField.setBounds(160, 40, 150, 25);

        JButton searchBtn = new JButton("Search");
        searchBtn.setBounds(120, 90, 120, 30);

        searchBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this,"Search action (to be implemented)")  ); /// اعدل عليه بعدين 

        add(idLabel);
        add(idField);
        add(searchBtn);

        setVisible(true);
    }
}
