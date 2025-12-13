package com.mycompany.hotelreservationsystem.ui.common;
import com.mycompany.hotelreservationsystem.ui.common.ReservationsReportScreen;

import javax.swing.*;
import java.awt.*;

import com.mycompany.hotelreservationsystem.ui.rooms.RoomsStatusScreen;
import com.mycompany.hotelreservationsystem.ui.reservation.ManageReservationsScreen;
import com.mycompany.hotelreservationsystem.ui.rooms.ManageRoomsScreen;
import com.mycompany.hotelreservationsystem.ui.rooms.ManageRoomTypesScreen;
import com.mycompany.hotelreservationsystem.ui.billing.IncomeReportScreen;
import com.mycompany.hotelreservationsystem.ui.billing.ManageDiscountsScreen; // ✅

public class ManagerDashboard extends JFrame {

    public ManagerDashboard() {
        setTitle("Manager Dashboard");
        setSize(450, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel title = new JLabel("Manager Dashboard", SwingConstants.CENTER);
        title.setBounds(0, 10, 450, 30);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title);

        JButton manageRooms = new JButton("Manage Rooms");
        manageRooms.setBounds(135, 60, 180, 30);

        JButton manageRoomTypes = new JButton("Manage Room Types");
        manageRoomTypes.setBounds(135, 100, 180, 30);

        JButton roomStatus = new JButton("View Room Status");
        roomStatus.setBounds(135, 140, 180, 30);

        JButton reservationsReport = new JButton("Reservations Report");
        reservationsReport.setBounds(135, 180, 180, 30);

        JButton incomeReport = new JButton("Income Report");
        incomeReport.setBounds(135, 220, 180, 30);

        // ✅ زر الخصومات
        JButton btnDiscounts = new JButton("Manage Discounts");
        btnDiscounts.setBounds(135, 260, 180, 30);

        JButton logout = new JButton("Logout");
        logout.setBounds(135, 310, 180, 30);

        styleButton(manageRooms, new Color(70, 130, 180));
        styleButton(manageRoomTypes, new Color(40, 167, 69));
        styleButton(roomStatus, new Color(111, 66, 193));
        styleButton(reservationsReport, new Color(255, 193, 7));
        styleButton(incomeReport, new Color(23, 162, 184));
        styleButton(btnDiscounts, new Color(220, 53, 69)); // ✅
        styleButton(logout, new Color(108, 117, 125));

        manageRooms.addActionListener(e -> openManageRooms());
        manageRoomTypes.addActionListener(e -> openManageRoomTypes());
        roomStatus.addActionListener(e -> openRoomStatus());
        reservationsReport.addActionListener(e -> openReservationsReport());
        incomeReport.addActionListener(e -> openIncomeReport());

        // ✅ فتح شاشة الخصومات (تأكد أنها في ui.billing)
        btnDiscounts.addActionListener(e -> new ManageDiscountsScreen(this).setVisible(true));

        logout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginForm().setVisible(true);
            }
        });

        add(manageRooms);
        add(manageRoomTypes);
        add(roomStatus);
        add(reservationsReport);
        add(incomeReport);
        add(btnDiscounts); // ✅ لازم
        add(logout);

        setVisible(true);
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
    }

    private void openManageRooms() {
        ManageRoomsScreen screen = new ManageRoomsScreen(this);
        screen.setVisible(true);
    }

    private void openManageRoomTypes() {
        ManageRoomTypesScreen screen = new ManageRoomTypesScreen(this);
        screen.setVisible(true);
    }

    private void openRoomStatus() {
        RoomsStatusScreen screen = new RoomsStatusScreen(this);
        screen.setVisible(true);
    }

    private void openReservationsReport() {
        new ReservationsReportScreen(this).setVisible(true);
    }


    private void openIncomeReport() {
        IncomeReportScreen screen = new IncomeReportScreen(this);
        screen.setVisible(true);
    }

    public static void main(String[] args) {
        new ManagerDashboard();
    }
}
