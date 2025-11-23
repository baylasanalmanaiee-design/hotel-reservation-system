/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.ui.common;
import javax.swing.*;
/**
 *
 * @author kady
 */
public class ReceptionistDashboard extends JFrame{
    public ReceptionistDashboard() {
        setTitle("Receptionist Dashboard");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JButton newRes = new JButton("New Reservation");
        newRes.setBounds(100, 30, 180, 30);

        JButton manageRes = new JButton("Manage Reservations");
        manageRes.setBounds(100, 70, 180, 30);

        JButton checkIn = new JButton("Check-In");
        checkIn.setBounds(100, 110, 180, 30);

        JButton checkOut = new JButton("Check-Out");
        checkOut.setBounds(100, 150, 180, 30);

        JButton roomStatus = new JButton("Room Status");
        roomStatus.setBounds(100, 190, 180, 30);

        JButton logout = new JButton("Logout");
        logout.setBounds(100, 230, 180, 30);

       
        newRes.addActionListener(e -> JOptionPane.showMessageDialog(this, "Coming Soon"));
        manageRes.addActionListener(e -> JOptionPane.showMessageDialog(this, "Coming Soon"));
        checkIn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Coming Soon"));
        checkOut.addActionListener(e -> JOptionPane.showMessageDialog(this, "Coming Soon"));
        roomStatus.addActionListener(e -> JOptionPane.showMessageDialog(this, "Coming Soon"));
        logout.addActionListener(e -> System.exit(0));

        add(newRes);
        add(manageRes);
        add(checkIn);
        add(checkOut);
        add(roomStatus);
        add(logout);

        setVisible(true);
    }
}
