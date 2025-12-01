/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.ui.reservation;

/**
 *
 * @author Bilsan
 */

import com.mycompany.hotelreservationsystem.dao.ReservationDAO;
import com.mycompany.hotelreservationsystem.model.Reservation;
import javax.swing.*;
import java.sql.*;

/*public class SearchReservationForm extends JFrame {

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

*/

public class SearchReservationForm extends JFrame {

    public SearchReservationForm() {

        setTitle("Search Reservation");
        setSize(380, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        ReservationDAO reservationDAO = new ReservationDAO();

        JLabel idLabel = new JLabel("Reservation ID:");
        idLabel.setBounds(40, 40, 120, 25);

        JTextField idField = new JTextField();
        idField.setBounds(160, 40, 150, 25);

        JButton searchBtn = new JButton("Search");
        searchBtn.setBounds(120, 90, 120, 30);

        searchBtn.addActionListener(e -> {

            int id = Integer.parseInt(idField.getText());

            Reservation r = reservationDAO.getReservationById(id);

            if (r == null)
                JOptionPane.showMessageDialog(this, "Not found");
            else
                new ReservationDetailsForm(r);

        });

        add(idLabel);  
        add(idField);  
        add(searchBtn);

        setVisible(true);
    }
}

