package com.mycompany.hotelreservationsystem.ui.common;

import javax.swing.*;
import java.awt.*;
/**
 *
 * @author kady
 */

import com.mycompany.hotelreservationsystem.ui.rooms.RoomsStatusScreen;
import com.mycompany.hotelreservationsystem.ui.reservation.ManageReservationsScreen;
import com.mycompany.hotelreservationsystem.ui.billing.InvoiceViewScreen;
import com.mycompany.hotelreservationsystem.ui.rooms.RoomsStatusScreen;
import com.mycompany.hotelreservationsystem.ui.rooms.ManageRoomsScreen;
import com.mycompany.hotelreservationsystem.ui.rooms.ManageRoomTypesScreen;


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

        // نفس ستايل الرسبشن
        styleButton(manageRooms,        new Color(70, 130, 180));  // أزرق
        styleButton(manageRoomTypes,    new Color(40, 167, 69));   // أخضر
        styleButton(roomStatus,         new Color(111, 66, 193));  // بنفسجي
        styleButton(reservationsReport, new Color(255, 193, 7));   // أصفر
        styleButton(incomeReport,       new Color(23, 162, 184));  // سماوي
        styleButton(logout,             new Color(108, 117, 125)); // رمادي

        // ربط الأزرار بالشاشات
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

    // تنسيق الأزرار
    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
    }

    // الدوال اللي تفتح الشاشات

    // هذي لسه TODO لأن ما عندكم شاشة ManageRooms جاهزة
    private void openManageRooms() {
       // تفتح شاشة إدارة الغرف
       ManageRoomsScreen screen = new ManageRoomsScreen(this);
       screen.setVisible(true);
   }

   private void openManageRoomTypes() {
       // تفتح شاشة أنواع الغرف
       ManageRoomTypesScreen screen = new ManageRoomTypesScreen(this);
       screen.setVisible(true);
   }


    private void openRoomStatus() {
        // هذه مرتبطة فعلياً بشاشة حالة الغرف
        RoomsStatusScreen screen = new RoomsStatusScreen(this);
        screen.setVisible(true);
    }

    private void openReservationsReport() {
        // حالياً نخليها تفتح شاشة إدارة الحجوزات
        ManageReservationsScreen screen = new ManageReservationsScreen(this);
        screen.setVisible(true);
    }

    private void openIncomeReport() {
        // نفتح شاشة عرض الفواتير/الدخل
        InvoiceViewScreen screen = new InvoiceViewScreen(this);
        screen.setVisible(true);
    }

    // main للتجربة
    public static void main(String[] args) {
        new ManagerDashboard();
    }
}
