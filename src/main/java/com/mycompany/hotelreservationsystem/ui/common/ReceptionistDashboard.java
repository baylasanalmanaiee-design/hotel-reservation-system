/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.ui.common;

import javax.swing.*;
import java.awt.*;
import com.mycompany.hotelreservationsystem.ui.reservation.NewReservationScreen;
import com.mycompany.hotelreservationsystem.ui.reservation.ManageReservationsScreen;
import com.mycompany.hotelreservationsystem.ui.billing.CheckInScreen;
import com.mycompany.hotelreservationsystem.ui.billing.CheckOutScreen;
import com.mycompany.hotelreservationsystem.ui.reservation.WaitlistScreen;
import com.mycompany.hotelreservationsystem.ui.rooms.RoomsStatusScreen;

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

        // تنسيق الأزرار
        styleButton(newRes, new Color(70, 130, 180)); // أزرق
        styleButton(manageRes, new Color(40, 167, 69)); // أخضر
        styleButton(checkIn, new Color(255, 193, 7)); // أصفر
        styleButton(checkOut, new Color(220, 53, 69)); // أحمر
        styleButton(roomStatus, new Color(111, 66, 193)); // بنفسجي
        styleButton(logout, new Color(108, 117, 125)); // رمادي

        // Action Listeners
        newRes.addActionListener(e -> openNewReservation());
        manageRes.addActionListener(e -> openManageReservations());
        checkIn.addActionListener(e -> openCheckIn());
        checkOut.addActionListener(e -> openCheckOut());
        roomStatus.addActionListener(e -> openRoomsStatus());
        logout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", "Confirm Logout", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginForm().setVisible(true); //  open Login screen
            }
        });

        add(newRes);
        add(manageRes);
        add(checkIn);
        add(checkOut);
        add(roomStatus);
        add(logout);

        setVisible(true);
    }

    // دالة لتنسيق الأزرار
    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
    }

    // دوال فتح الشاشات
    private void openNewReservation() {
        NewReservationScreen newResScreen = new NewReservationScreen(this);
        newResScreen.setVisible(true);
    }

    private void openManageReservations() {
        ManageReservationsScreen manageScreen = new ManageReservationsScreen(this);
        manageScreen.setVisible(true);
    }

    private void openCheckIn() {
        CheckInScreen checkInScreen = new CheckInScreen(this);
        checkInScreen.setVisible(true);
    }

    private void openCheckOut() {
        CheckOutScreen checkOutScreen = new CheckOutScreen(this);
        checkOutScreen.setVisible(true);
    }

    private void openRoomsStatus() {
        RoomsStatusScreen roomsScreen = new RoomsStatusScreen(this);
        roomsScreen.setVisible(true);
    }

    // main method for test
    public static void main(String[] args) {
        new ReceptionistDashboard();
    }
}