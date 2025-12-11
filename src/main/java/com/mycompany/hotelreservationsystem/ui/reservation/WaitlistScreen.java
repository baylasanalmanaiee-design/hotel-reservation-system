/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.ui.reservation;

/**
 *
 * @author Bilsan
 */
import com.mycompany.hotelreservationsystem.dao.WaitlistDAO;
import com.mycompany.hotelreservationsystem.dao.ReservationDAO;
import com.mycompany.hotelreservationsystem.dao.RoomDAO;
import com.mycompany.hotelreservationsystem.dao.RoomTypeDAO;
import com.mycompany.hotelreservationsystem.model.Waitlist;
import com.mycompany.hotelreservationsystem.model.Room;
import com.mycompany.hotelreservationsystem.model.RoomType;
import com.mycompany.hotelreservationsystem.model.Reservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


  public class WaitlistScreen extends JDialog {

    private JTable table;
    private DefaultTableModel model;

    private WaitlistDAO waitlistDAO = new WaitlistDAO();
    private RoomDAO roomDAO = new RoomDAO();
    private ReservationDAO reservationDAO = new ReservationDAO();
    private RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

    public WaitlistScreen(JFrame parent) {
        super(parent, "Waitlist", true);
        setSize(850, 450);
        setLocationRelativeTo(parent);

        model = new DefaultTableModel(
                new String[]{"ID","Guest ID","Room Type","Check-In","Check-Out","Added At"}, 0
        );

        table = new JTable(model);
        loadWaitlist();

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnAssign = new JButton("Assign Room Now");
        btnAssign.setBackground(new Color(40,167,69));
        btnAssign.setForeground(Color.WHITE);
        btnAssign.addActionListener(e -> assignRoom());

        add(btnAssign, BorderLayout.SOUTH);
    }

    private void loadWaitlist() {
        model.setRowCount(0);
        for (Waitlist w : waitlistDAO.getAllWaitlist()) {
            model.addRow(new Object[]{
                    w.getId(),
                    w.getGuestId(),
                    w.getRoomTypeId(),
                    w.getCheckIn(),
                    w.getCheckOut(),
                    w.getAddedAt()
            });
        }
    }

    private void assignRoom() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a waitlist entry first!");
            return;
        }

        int waitId = (int) model.getValueAt(row, 0);
        int guestId = (int) model.getValueAt(row, 1);
        int roomTypeId = (int) model.getValueAt(row, 2);

        String checkIn = (String) model.getValueAt(row, 3);
        String checkOut = (String) model.getValueAt(row, 4);

        // 1) إيجاد غرفة متاحة
        Room availableRoom = null;
        for (Room r : roomDAO.getAllRooms()) {
            if (r.getRoomTypeId() == roomTypeId &&
                r.getStatus().equalsIgnoreCase("Available")) {
                availableRoom = r;
                break;
            }
        }

        if (availableRoom == null) {
            JOptionPane.showMessageDialog(this, "No available room right now!");
            return;
        }

        // 2) حساب السعر
        RoomType rt = roomTypeDAO.getRoomTypeById(roomTypeId);
        long nights = java.time.temporal.ChronoUnit.DAYS.between(
                java.time.LocalDate.parse(checkIn),
                java.time.LocalDate.parse(checkOut)
        );

        if (nights <= 0) {
            JOptionPane.showMessageDialog(this, "Invalid check-in/check-out dates!");
            return;
        }

        double totalPrice = nights * rt.getBasePrice();

        // 3) إنشاء الحجز
        Reservation r = new Reservation();
        r.setGuestId(guestId);
        r.setRoomId(availableRoom.getId());
        r.setCheckInDate(checkIn);
        r.setCheckOutDate(checkOut);
        r.setTotalPrice(totalPrice);

        boolean created = reservationDAO.createReservation(r);

        if (!created) {
            JOptionPane.showMessageDialog(this, "Failed to create reservation!");
            return;
        }

        // 4) حذف من قائمة الانتظار
        waitlistDAO.delete(waitId);

        JOptionPane.showMessageDialog(this,
                "Reservation created successfully!\nRoom: " + availableRoom.getRoomNumber() +
                        "\nTotal: " + totalPrice + " SR");

        loadWaitlist(); // تحديث الجدول
    }
}
