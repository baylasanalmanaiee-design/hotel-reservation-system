/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.ui.reservation;

/**
 *
 * @author Bilsan
 */

import com.mycompany.hotelreservationsystem.dao.GuestDAO;
import com.mycompany.hotelreservationsystem.dao.ReservationDAO;
import com.mycompany.hotelreservationsystem.dao.RoomDAO;
import com.mycompany.hotelreservationsystem.dao.WaitlistDAO;
import com.mycompany.hotelreservationsystem.model.Guest;
import com.mycompany.hotelreservationsystem.model.Reservation;
import com.mycompany.hotelreservationsystem.model.Room;
import com.mycompany.hotelreservationsystem.model.Waitlist;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class NewReservationScreen extends JDialog {

    // DAO objects
    private GuestDAO guestDAO = new GuestDAO();
    private RoomDAO roomDAO = new RoomDAO();
    private ReservationDAO reservationDAO = new ReservationDAO();
    private WaitlistDAO waitlistDAO = new WaitlistDAO();

    private JTextField txtGuestName, txtPhone, txtId;
    private JComboBox<String> cmbRoomType;
    private JTextField txtCheckInDate, txtCheckOutDate;
    private JButton btnSearchRooms, btnConfirm, btnCancel;
    private JTable roomsTable;
    private DefaultTableModel tableModel;

    public NewReservationScreen(JFrame parent) {
        super(parent, "New Reservation", true);
        setSize(900, 600);
        setLocationRelativeTo(parent);

       /* JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
*/
        JPanel guestPanel = createGuestPanel();
        JPanel roomPanel = createRoomPanel();
        JPanel tablePanel = createTablePanel();
        JPanel buttonPanel = createButtonPanel();

        /*mainPanel.add(guestPanel, BorderLayout.NORTH);
        mainPanel.add(roomPanel, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.SOUTH);

        JPanel container = new JPanel(new BorderLayout());
        container.add(mainPanel, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);

        add(container);*/
       JPanel container = new JPanel();
      container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

      container.add(guestPanel);
      container.add(roomPanel);
      container.add(tablePanel);
      container.add(buttonPanel);

      add(container);

    }

    private JPanel createGuestPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Guest Information"));

        panel.add(new JLabel("Full Name:"));
        txtGuestName = new JTextField();
        panel.add(txtGuestName);

        panel.add(new JLabel("Phone:"));
        txtPhone = new JTextField();
        panel.add(txtPhone);

        panel.add(new JLabel("ID/Passport:"));
        txtId = new JTextField();
        panel.add(txtId);

        return panel;
    }

    private JPanel createRoomPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Room Selection"));

        panel.add(new JLabel("Room Type:"));
        cmbRoomType = new JComboBox<>(new String[]{"Single", "Double", "Suite", "Deluxe"});
        panel.add(cmbRoomType);

        panel.add(new JLabel("Check-in Date (YYYY-MM-DD):"));
        txtCheckInDate = new JTextField();
        panel.add(txtCheckInDate);

        panel.add(new JLabel("Check-out Date (YYYY-MM-DD):"));
        txtCheckOutDate = new JTextField();
        panel.add(txtCheckOutDate);

        btnSearchRooms = new JButton("Search Available Rooms");
        btnSearchRooms.setBackground(new Color(70, 130, 180));
        btnSearchRooms.setForeground(Color.WHITE);
        btnSearchRooms.addActionListener(e -> searchAvailableRooms());
        panel.add(btnSearchRooms);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Available Rooms"));

        String[] columns = {"Room ID", "Room No", "Type", "Price/Night"};
        tableModel = new DefaultTableModel(columns, 0);
        roomsTable = new JTable(tableModel);
        roomsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(roomsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnConfirm = new JButton("Confirm Reservation");
        btnConfirm.setBackground(new Color(34, 139, 34));
        btnConfirm.setForeground(Color.WHITE);

        btnCancel = new JButton("Cancel");
        btnCancel.setBackground(new Color(220, 53, 69));
        btnCancel.setForeground(Color.WHITE);

        btnConfirm.addActionListener(e -> confirmReservation());
        btnCancel.addActionListener(e -> dispose());

        panel.add(btnCancel);
        panel.add(btnConfirm);

        return panel;
    }

    // ================= LOGIC ================= //

   private void searchAvailableRooms() {
    tableModel.setRowCount(0);

    String checkIn = txtCheckInDate.getText().trim();
    String checkOut = txtCheckOutDate.getText().trim();

    // ✨ تحقق إدخال التواريخ ✨
    if (checkIn.isEmpty() || checkOut.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Please enter both Check-In and Check-Out dates!",
                "Required Fields",
                JOptionPane.ERROR_MESSAGE);
        return;
    }

    int roomType = cmbRoomType.getSelectedIndex() + 1;

    if (!reservationDAO.checkAvailability(roomType, checkIn, checkOut)) {
        JOptionPane.showMessageDialog(this,
                "No rooms available! Guest will be added to waitlist",
                "Waitlist",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    for (Room room : roomDAO.getAllRooms()) {
        if (room.getRoomTypeId() == roomType &&
                room.getStatus().equalsIgnoreCase("Available")) {

            tableModel.addRow(new Object[]{
                    room.getId(),
                    room.getRoomNumber(),
                    roomType,
                    "100.00"
            });
        }
    }

    if (tableModel.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this,
                "No available rooms. Will be added to waitlist.",
                "Waitlist",
                JOptionPane.WARNING_MESSAGE);
    }
}


    private void confirmReservation() {
        String fullName = txtGuestName.getText().trim();
        String phone = txtPhone.getText().trim();
        String idNum = txtId.getText().trim();

        if (fullName.isEmpty() || phone.isEmpty() || idNum.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all guest information!");
            return;
        }

        Guest guest = new Guest(0, fullName, phone, idNum);
        int guestId = guestDAO.addGuest(guest);

        if (guestId == -1) {
            JOptionPane.showMessageDialog(this, "Error saving guest data!");
            return;
        }

        if (roomsTable.getRowCount() == 0) {
            Waitlist w = new Waitlist(
                    guestId,
                    cmbRoomType.getSelectedIndex() + 1,
                    txtCheckInDate.getText(),
                    txtCheckOutDate.getText(),
                    java.time.LocalDateTime.now().toString()
            );

            waitlistDAO.addToWaitlist(w);
            JOptionPane.showMessageDialog(this, "Added to Waitlist successfully!");
            dispose();
            return;
        }

        int selected = roomsTable.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Select a room first!");
            return;
        }

        int roomId = Integer.parseInt(tableModel.getValueAt(selected, 0).toString());

        Reservation r = new Reservation();
        r.setGuestId(guestId);
        r.setRoomId(roomId);
        r.setCheckInDate(txtCheckInDate.getText());
        r.setCheckOutDate(txtCheckOutDate.getText());

        if (reservationDAO.createReservation(r)) {
            roomDAO.updateRoomStatus(Integer.parseInt(tableModel.getValueAt(selected, 1).toString()), "Booked");
            JOptionPane.showMessageDialog(this, "Reservation Created Successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Reservation Failed!");
        }
    }
}



