/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.ui.common;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author kady
 */
public class ManagerDashboard extends JFrame {

    public ManagerDashboard() {
        setTitle("Manager Dashboard");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // العنوان
        JLabel title = new JLabel("Manager Dashboard", SwingConstants.CENTER);
        title.setBounds(0, 10, 450, 30);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title);

        // الأزرار
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

        JButton logout = new JButton("Logout");
        logout.setBounds(135, 270, 180, 30);

        // نفس فكرة الرسبشن: تنسيق الأزرار بألوان مختلفة
        styleButton(manageRooms,       new Color(70, 130, 180));  // أزرق
        styleButton(manageRoomTypes,   new Color(40, 167, 69));   // أخضر
        styleButton(roomStatus,        new Color(111, 66, 193));  // بنفسجي
        styleButton(reservationsReport,new Color(255, 193, 7));   // أصفر
        styleButton(incomeReport,      new Color(23, 162, 184));  // سماوي
        styleButton(logout,            new Color(108, 117, 125)); // رمادي

        // Action Listeners (الحين نسويها بدوال خاصة، زي الرسبشن)
        manageRooms.addActionListener(e -> openManageRooms());
        manageRoomTypes.addActionListener(e -> openManageRoomTypes());
        roomStatus.addActionListener(e -> openRoomStatus());
        reservationsReport.addActionListener(e -> openReservationsReport());
        incomeReport.addActionListener(e -> openIncomeReport());

        logout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginForm().setVisible(true); // رجوع لواجهة اللوق إن
            }
        });

        add(manageRooms);
        add(manageRoomTypes);
        add(roomStatus);
        add(reservationsReport);
        add(incomeReport);
        add(logout);

        setVisible(true);
    }

    // دالة لتنسيق الأزرار (نفس اللي في الرسبشن)
    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
    }

    // الدوال حالياً تعرض رسائل فقط، لين البنات يربطون شاشاتهم الحقيقية
    private void openManageRooms() {
        // TODO: استبدلي الرسالة بفتح شاشة إدارة الغرف لما تجهز
        JOptionPane.showMessageDialog(this, "Manage Rooms coming soon...");
    }

    private void openManageRoomTypes() {
        // TODO: استبدليها بفتح شاشة أنواع الغرف
        JOptionPane.showMessageDialog(this, "Room Types coming soon...");
    }

    private void openRoomStatus() {
        // TODO: فتح شاشة حالة الغرف
        JOptionPane.showMessageDialog(this, "Room Status coming soon...");
    }

    private void openReservationsReport() {
        // TODO: فتح شاشة تقارير الحجوزات
        JOptionPane.showMessageDialog(this, "Reservations Report coming soon...");
    }

    private void openIncomeReport() {
        // TODO: فتح شاشة تقارير الدخل
        JOptionPane.showMessageDialog(this, "Income Report coming soon...");
    }

    // main للتجربة لو حبيتي تشغلينها لحالها
    public static void main(String[] args) {
        new ManagerDashboard();
    }
}
