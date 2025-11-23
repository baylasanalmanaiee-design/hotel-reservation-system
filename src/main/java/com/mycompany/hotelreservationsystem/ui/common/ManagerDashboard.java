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
public class ManagerDashboard extends JFrame{
    public ManagerDashboard() {
         setTitle("Manager Dashboard");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel title = new JLabel("Manager Dashboard", SwingConstants.CENTER);
        title.setBounds(0, 10, 450, 30);
        add(title);

        JButton manageRooms = new JButton("Manage Rooms");
        manageRooms.setBounds(130, 60, 180, 30);

        JButton manageRoomTypes = new JButton("Manage Room Types");
        manageRoomTypes.setBounds(130, 100, 180, 30);

        JButton roomStatus = new JButton("View Room Status");
        roomStatus.setBounds(130, 140, 180, 30);

        JButton reservationsReport = new JButton("Reservations Report");
        reservationsReport.setBounds(130, 180, 180, 30);

        JButton incomeReport = new JButton("Income Report");
        incomeReport.setBounds(130, 220, 180, 30);

        JButton logout = new JButton("Logout");
        logout.setBounds(130, 270, 180, 30);

        // Temporary Actions (مؤقتة لين يخلصون البنات شغلهم)
        manageRooms.addActionListener(e -> JOptionPane.showMessageDialog(this, "Manage Rooms coming soon..."));
        manageRoomTypes.addActionListener(e -> JOptionPane.showMessageDialog(this, "Room Types coming soon..."));
        roomStatus.addActionListener(e -> JOptionPane.showMessageDialog(this, "Room Status coming soon..."));
        reservationsReport.addActionListener(e -> JOptionPane.showMessageDialog(this, "Reservations Report coming soon..."));
        incomeReport.addActionListener(e -> JOptionPane.showMessageDialog(this, "Income Report coming soon..."));
        logout.addActionListener(e -> {
            new LoginForm();
            dispose();
        });

        add(manageRooms);
        add(manageRoomTypes);
        add(roomStatus);
        add(reservationsReport);
        add(incomeReport);
        add(logout);

        setVisible(true);
    }
}
