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
        setSize(700, 500);
        setLocationRelativeTo(parent);

        setLayout(new BorderLayout());

        model = new DefaultTableModel(
                new String[]{"ID", "Guest", "Room Type", "Check-in", "Check-out", "Added At"}, 0
        );

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnAssign = new JButton("Assign Room");
        JButton btnRefresh = new JButton("Refresh");

        JPanel bottom = new JPanel();
        bottom.add(btnAssign);
        bottom.add(btnRefresh);

        add(bottom, BorderLayout.SOUTH);

        btnAssign.addActionListener(e -> assignRoom());
        btnRefresh.addActionListener(e -> loadData());

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);

        List<Waitlist> list = waitlistDAO.getAllWaitlist();

        for (Waitlist w : list) {
            RoomType rt = roomTypeDAO.getRoomTypeById(w.getRoomTypeId());
            model.addRow(new Object[]{
                    w.getId(),
                    w.getGuestId(),
                    rt != null ? rt.getName() : "Unknown",
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
        String roomTypeName = (String) model.getValueAt(row, 2);
        String in = (String) model.getValueAt(row, 3);
        String out = (String) model.getValueAt(row, 4);

        RoomType rt = roomTypeDAO.getRoomTypeByName(roomTypeName);

        // البحث عن أول غرفة Available
        Room assigned = null;
        for (Room r : roomDAO.getAllRooms()) {
            if (r.getRoomTypeId() == rt.getId() && r.getStatus().equals("Available")) {
                assigned = r;
                break;
            }
        }

        if (assigned == null) {
            JOptionPane.showMessageDialog(this, "No available room for this type.");
            return;
        }

        // إنشاء الحجز
        Reservation res = new Reservation();
        res.setGuestId(guestId);
        res.setRoomId(assigned.getId());
        res.setCheckInDate(in);
        res.setCheckOutDate(out);
        res.setTotalPrice(0); // احسبيه إذا تبين

        reservationDAO.createReservation(res);

        // حذف من waitlist
        waitlistDAO.removeFromWaitlist(waitId);

        JOptionPane.showMessageDialog(this, "Assigned Room: " + assigned.getRoomNumber());

        loadData();
    }
}
