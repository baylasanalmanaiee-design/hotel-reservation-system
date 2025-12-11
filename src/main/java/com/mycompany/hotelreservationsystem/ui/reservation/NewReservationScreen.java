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
import com.mycompany.hotelreservationsystem.dao.RoomTypeDAO;
import com.mycompany.hotelreservationsystem.dao.WaitlistDAO;
import com.mycompany.hotelreservationsystem.model.Guest;
import com.mycompany.hotelreservationsystem.model.Reservation;
import com.mycompany.hotelreservationsystem.model.Room;
import com.mycompany.hotelreservationsystem.model.RoomType;
import com.mycompany.hotelreservationsystem.model.Waitlist;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class NewReservationScreen extends JDialog {

    // DAO objects
    private GuestDAO guestDAO = new GuestDAO();
    private RoomDAO roomDAO = new RoomDAO();
    private ReservationDAO reservationDAO = new ReservationDAO();
    private WaitlistDAO waitlistDAO = new WaitlistDAO();
    private RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

    private JTextField txtGuestName, txtPhone, txtId, txtEmail;
    private JComboBox<String> cmbRoomType;
    private JTextField txtCheckInDate, txtCheckOutDate;
    private JButton btnSearchRooms, btnConfirm, btnCancel;
    private JTable roomsTable;
    private DefaultTableModel tableModel;

    private java.util.List<RoomType> roomTypes;

    public NewReservationScreen(JFrame parent) {
        super(parent, "New Reservation", true);
        setSize(900, 650);
        setLocationRelativeTo(parent);

        JPanel guestPanel = createGuestPanel();
        JPanel roomPanel = createRoomPanel();
        JPanel tablePanel = createTablePanel();
        JPanel buttonPanel = createButtonPanel();

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        container.add(guestPanel);
        container.add(roomPanel);
        container.add(tablePanel);
        container.add(buttonPanel);

        add(container);

        loadRoomTypesIntoCombo();
    }

    private JPanel createGuestPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
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

        panel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        panel.add(txtEmail);

        return panel;
    }

    private JPanel createRoomPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Room Selection"));

        panel.add(new JLabel("Room Type:"));
        cmbRoomType = new JComboBox<>();
        panel.add(cmbRoomType);

        panel.add(new JLabel("Check-in Date (YYYY-MM-DD):"));
        txtCheckInDate = new JTextField();
        panel.add(txtCheckInDate);

        panel.add(new JLabel("Check-out Date (YYYY-MM-DD):"));
        txtCheckOutDate = new JTextField();
        panel.add(txtCheckOutDate);

        // زر البحث فقط
        btnSearchRooms = new JButton("Search Available Rooms");
        btnSearchRooms.setBackground(new Color(70, 130, 180));
        btnSearchRooms.setForeground(Color.WHITE);
        btnSearchRooms.addActionListener(e -> searchAvailableRooms());

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrapper.add(btnSearchRooms);

        JPanel outer = new JPanel(new BorderLayout());
        outer.add(panel, BorderLayout.CENTER);
        outer.add(wrapper, BorderLayout.SOUTH);

        return outer;
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

    private void loadRoomTypesIntoCombo() {
        cmbRoomType.removeAllItems();
        roomTypes = roomTypeDAO.getAllRoomTypes();

        if (roomTypes == null || roomTypes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No room types found in database!");
            return;
        }

        for (RoomType rt : roomTypes) {
            cmbRoomType.addItem(rt.getName());
        }
    }

    private void searchAvailableRooms() {
        tableModel.setRowCount(0);

        String checkIn = txtCheckInDate.getText().trim();
        String checkOut = txtCheckOutDate.getText().trim();

        if (checkIn.isEmpty() || checkOut.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Check-In and Check-Out dates!");
            return;
        }

        if (cmbRoomType.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room type!");
            return;
        }

        RoomType selectedType = roomTypes.get(cmbRoomType.getSelectedIndex());
        int roomTypeId = selectedType.getId();

        boolean available = reservationDAO.checkAvailability(roomTypeId, checkIn, checkOut);
        if (!available) {
            JOptionPane.showMessageDialog(this,
                    "No rooms available. Guest will be added to waitlist.",
                    "Waitlist",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Room> allRooms = roomDAO.getAllRooms();
        for (Room room : allRooms) {
            if (room.getRoomTypeId() == roomTypeId &&
                room.getStatus().equalsIgnoreCase("Available")) {

                tableModel.addRow(new Object[]{
                        room.getId(),
                        room.getRoomNumber(),
                        selectedType.getName(),
                        selectedType.getBasePrice()
                });
            }
        }
    }

    private void confirmReservation() {
        String fullName = txtGuestName.getText().trim();
        String phone = txtPhone.getText().trim();
        String idNum = txtId.getText().trim();
        String email = txtEmail.getText().trim();
        String checkIn = txtCheckInDate.getText().trim();
        String checkOut = txtCheckOutDate.getText().trim();

        if (fullName.isEmpty() || phone.isEmpty() || idNum.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all guest information!");
            return;
        }

        if (checkIn.isEmpty() || checkOut.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both dates!");
            return;
        }

        if (cmbRoomType.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Please select room type!");
            return;
        }

        LocalDate inDate;
        LocalDate outDate;

        try {
            inDate = LocalDate.parse(checkIn);
            outDate = LocalDate.parse(checkOut);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format!");
            return;
        }

        long nights = ChronoUnit.DAYS.between(inDate, outDate);
        if (nights <= 0) {
            JOptionPane.showMessageDialog(this, "Check-Out must be after Check-In!");
            return;
        }

        RoomType selectedType = roomTypes.get(cmbRoomType.getSelectedIndex());
        double totalPrice = selectedType.getBasePrice() * nights;

        // حفظ الضيف
        Guest guest = new Guest(0, fullName, phone, idNum, email);
        int guestId = guestDAO.addGuest(guest);

        if (guestId == -1) {
            JOptionPane.showMessageDialog(this, "Error saving guest!");
            return;
        }

        // إذا لا توجد غرف → أضف للانتظار
        if (roomsTable.getRowCount() == 0) {
            Waitlist w = new Waitlist(
                    guestId,
                    selectedType.getId(),
                    checkIn,
                    checkOut,
                    java.time.LocalDateTime.now().toString()
            );

            waitlistDAO.addToWaitlist(w);
            JOptionPane.showMessageDialog(this, "Guest added to waitlist!");
            dispose();
            return;
        }

        // إذا هناك غرف → نأخذ المختارة
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a room first!");
            return;
        }

        int roomId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());

        Reservation r = new Reservation();
        r.setGuestId(guestId);
        r.setRoomId(roomId);
        r.setCheckInDate(checkIn);
        r.setCheckOutDate(checkOut);
        r.setTotalPrice(totalPrice);

        if (reservationDAO.createReservation(r)) {
            JOptionPane.showMessageDialog(this,
                    "Reservation Created Successfully!\nTotal: " + totalPrice + " SR");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create reservation!");
        }
    }
}
