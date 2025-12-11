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
import java.time.format.DateTimeParseException;

public class NewReservationScreen extends JDialog {

    // DAO objects
    private GuestDAO guestDAO = new GuestDAO();
    private RoomDAO roomDAO = new RoomDAO();
    private RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
    private ReservationDAO reservationDAO = new ReservationDAO();
    private WaitlistDAO waitlistDAO = new WaitlistDAO();

    private JTextField txtGuestName, txtPhone, txtId, txtEmail;
    private JComboBox<RoomType> cmbRoomType;
    private JTextField txtCheckInDate, txtCheckOutDate;
    private JButton btnSearchRooms, btnConfirm, btnCancel;
    private JTable roomsTable;
    private DefaultTableModel tableModel;

    public NewReservationScreen(JFrame parent) {
        super(parent, "New Reservation", true);
        setSize(900, 600);
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

        loadRoomTypesFromDB();
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

        panel.add(new JLabel("Email:"));      // NEW
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

        btnSearchRooms = new JButton("Search Available Rooms");
        btnSearchRooms.setBackground(new Color(70, 130, 180));
        btnSearchRooms.setForeground(Color.WHITE);
        btnSearchRooms.addActionListener(e -> searchAvailableRooms());

        // نخليه بعرض الصف كامل تحت
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(btnSearchRooms);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(panel, BorderLayout.CENTER);
        wrapper.add(btnPanel, BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Available Rooms"));

        String[] columns = {"Room ID", "Room No", "Type", "Price/Night"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
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

    // ====== helpers ======

    private void loadRoomTypesFromDB() {
        cmbRoomType.removeAllItems();
        for (RoomType rt : roomTypeDAO.getAllRoomTypes()) {
            cmbRoomType.addItem(rt);  // toString() يرجع الاسم
        }
    }

    private long calculateNights(String in, String out) {
        try {
            LocalDate dIn = LocalDate.parse(in.trim());
            LocalDate dOut = LocalDate.parse(out.trim());
            long days = java.time.temporal.ChronoUnit.DAYS.between(dIn, dOut);
            return days;
        } catch (DateTimeParseException ex) {
            return -1;
        }
    }

    // ================= LOGIC ================= //

    private void searchAvailableRooms() {
        tableModel.setRowCount(0);

        String checkIn = txtCheckInDate.getText().trim();
        String checkOut = txtCheckOutDate.getText().trim();

        if (checkIn.isEmpty() || checkOut.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both Check-In and Check-Out dates!",
                    "Required Fields",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cmbRoomType.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select room type!",
                    "Required Field",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        RoomType selectedType = (RoomType) cmbRoomType.getSelectedItem();
        int roomTypeId = selectedType.getId();
        double basePrice = selectedType.getBasePrice();

        // تأكد من توفر غرف لنوع هذه الغرفة في هذا النطاق
        boolean available = reservationDAO.checkAvailability(roomTypeId, checkIn, checkOut);
        if (!available) {
            JOptionPane.showMessageDialog(this,
                    "No rooms available for this type in selected dates!\nGuest will be added to Waitlist when confirming.",
                    "No Availability",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // جلب الغرف من DB حسب النوع والحالة
        for (Room room : roomDAO.getAllRooms()) {
            if (room.getRoomTypeId() == roomTypeId &&
                    room.getStatus().equalsIgnoreCase("Available")) {

                tableModel.addRow(new Object[]{
                        room.getId(),
                        room.getRoomNumber(),
                        selectedType.getName(),
                        basePrice
                });
            }
        }

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No available rooms now. Guest will be added to Waitlist on confirm.",
                    "No Rooms",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void confirmReservation() {
        String fullName = txtGuestName.getText().trim();
        String phone = txtPhone.getText().trim();
        String idNum = txtId.getText().trim();
        String email = txtEmail.getText().trim();
        String checkIn = txtCheckInDate.getText().trim();
        String checkOut = txtCheckOutDate.getText().trim();

        if (fullName.isEmpty() || phone.isEmpty() || idNum.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all guest information!");
            return;
        }

        if (checkIn.isEmpty() || checkOut.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Check-In and Check-Out dates!");
            return;
        }

        long nights = calculateNights(checkIn, checkOut);
        if (nights <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Check-Out date must be after Check-In date!",
                    "Invalid Dates",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cmbRoomType.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select room type!");
            return;
        }

        RoomType selectedType = (RoomType) cmbRoomType.getSelectedItem();

        // 1) حفظ الضيف أولاً
        Guest guest = new Guest(0, fullName, phone, idNum, email);
        int guestId = guestDAO.addGuest(guest);

        if (guestId == -1) {
            JOptionPane.showMessageDialog(this, "Error saving guest data!");
            return;
        }

        // 2) لو ما فيه غرف في الجدول → إضافة إلى لائحة الانتظار
        if (roomsTable.getRowCount() == 0) {
            Waitlist w = new Waitlist(
                    guestId,
                    selectedType.getId(),
                    checkIn,
                    checkOut,
                    java.time.LocalDateTime.now().toString()
            );

            waitlistDAO.addToWaitlist(w);
            JOptionPane.showMessageDialog(this,
                    "No rooms available. Guest added to Waitlist successfully!");
            dispose();
            return;
        }

        // 3) لازم يختار غرفة
        int selected = roomsTable.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room from the table!");
            return;
        }

        int roomId = Integer.parseInt(tableModel.getValueAt(selected, 0).toString());
        double pricePerNight = Double.parseDouble(tableModel.getValueAt(selected, 3).toString());
        double totalPrice = nights * pricePerNight;

        Reservation r = new Reservation();
        r.setGuestId(guestId);
        r.setRoomId(roomId);
        r.setCheckInDate(checkIn);
        r.setCheckOutDate(checkOut);
        r.setTotalPrice(totalPrice);

        if (reservationDAO.createReservation(r)) {
            // تحديث حالة الغرفة إلى Booked
            int roomNumber = Integer.parseInt(tableModel.getValueAt(selected, 1).toString());
            roomDAO.updateRoomStatus(roomNumber, "Booked");

            JOptionPane.showMessageDialog(this,
                    "Reservation Created Successfully!\nTotal price: " + totalPrice + " SR");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Reservation Failed!");
        }
    }
}




