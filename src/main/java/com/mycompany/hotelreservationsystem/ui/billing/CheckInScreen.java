package com.mycompany.hotelreservationsystem.ui.billing;

import com.mycompany.hotelreservationsystem.DatabaseConnection;
import com.mycompany.hotelreservationsystem.dao.GuestDAO;
import com.mycompany.hotelreservationsystem.dao.ReservationDAO;
import com.mycompany.hotelreservationsystem.dao.RoomDAO;
import com.mycompany.hotelreservationsystem.model.Guest;
import com.mycompany.hotelreservationsystem.model.Reservation;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckInScreen extends JDialog {

    private JComboBox<String> cmbReservations;
    private JTextArea txtReservationDetails;
    private JButton btnConfirm, btnCancel;

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final GuestDAO guestDAO = new GuestDAO();

    private final Map<Integer, String> reservationStatusMap = new HashMap<>();

    public CheckInScreen(JFrame parent) {
        super(parent, "Check-In Guest", true);
        setSize(650, 480);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        mainPanel.add(createSelectionPanel(), BorderLayout.NORTH);
        mainPanel.add(createDetailsPanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);

        loadAllReservationsFromDB();
        cmbReservations.addActionListener(e -> updateReservationDetails());
    }

    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Select Reservation"));

        panel.add(new JLabel("Reservation (All):"));
        cmbReservations = new JComboBox<>();
        panel.add(cmbReservations);

        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Reservation Details"));

        txtReservationDetails = new JTextArea(12, 45);
        txtReservationDetails.setEditable(false);
        txtReservationDetails.setFont(new Font("Monospaced", Font.PLAIN, 12));

        panel.add(new JScrollPane(txtReservationDetails), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnConfirm = new JButton("Confirm Check-In");
        btnCancel = new JButton("Cancel");

        btnConfirm.setBackground(new Color(40, 167, 69));
        btnConfirm.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(220, 53, 69));
        btnCancel.setForeground(Color.WHITE);

        btnConfirm.addActionListener(e -> confirmCheckIn());
        btnCancel.addActionListener(e -> dispose());

        panel.add(btnCancel);
        panel.add(btnConfirm);

        return panel;
    }

    private void loadAllReservationsFromDB() {
        cmbReservations.removeAllItems();
        reservationStatusMap.clear();

        List<ReservationRow> list = getAllReservationsFromDB();

        for (ReservationRow rr : list) {
            reservationStatusMap.put(rr.reservationId, rr.status);

            String item = "RES" + String.format("%03d", rr.reservationId)
                    + " | " + rr.status
                    + " | Guest " + rr.guestId
                    + " | Room " + rr.roomId;

            cmbReservations.addItem(item);
        }

        if (cmbReservations.getItemCount() > 0) {
            cmbReservations.setSelectedIndex(0);
            updateReservationDetails();
        } else {
            txtReservationDetails.setText("No reservations found in database.");
        }
    }

    private List<ReservationRow> getAllReservationsFromDB() {
        List<ReservationRow> list = new ArrayList<>();

        String sql = """
            SELECT reservation_id, guest_id, room_id, check_in_date, check_out_date, total_price, status
            FROM reservations
            ORDER BY reservation_id DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ReservationRow rr = new ReservationRow();
                rr.reservationId = rs.getInt("reservation_id");
                rr.guestId = rs.getInt("guest_id");
                rr.roomId = rs.getInt("room_id");
                rr.checkIn = rs.getString("check_in_date");
                rr.checkOut = rs.getString("check_out_date");
                rr.total = rs.getDouble("total_price");
                rr.status = rs.getString("status");
                if (rr.status == null) rr.status = "";
                list.add(rr);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading reservations: " + e.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return list;
    }

    private int parseReservationId(String comboText) {
        try {
            String code = comboText.split("\\|")[0].trim(); // "RES001"
            return Integer.parseInt(code.substring(3));
        } catch (Exception e) {
            return 0;
        }
    }

    private void updateReservationDetails() {
        if (cmbReservations.getSelectedItem() == null) return;

        int reservationId = parseReservationId(cmbReservations.getSelectedItem().toString());
        if (reservationId <= 0) return;

        Reservation r = reservationDAO.getReservationById(reservationId);
        if (r == null) {
            txtReservationDetails.setText("");
            return;
        }

        String status = reservationStatusMap.getOrDefault(reservationId, "");

        Guest g = guestDAO.getGuestById(r.getGuestId());
        String guestName = (g != null && g.getFullName() != null && !g.getFullName().isBlank())
                ? g.getFullName()
                : ("Guest " + r.getGuestId());

        long nights;
        try {
            nights = Math.max(0, ChronoUnit.DAYS.between(
                    LocalDate.parse(r.getCheckInDate()),
                    LocalDate.parse(r.getCheckOutDate())
            ));
        } catch (Exception ex) {
            nights = 0;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Reservation ID : RES").append(String.format("%03d", r.getId())).append("\n");
        sb.append("Status         : ").append(status).append("\n");
        sb.append("Guest          : ").append(guestName).append(" (ID ").append(r.getGuestId()).append(")\n");
        sb.append("Room ID        : ").append(r.getRoomId()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Check-In Date  : ").append(r.getCheckInDate()).append("\n");
        sb.append("Check-Out Date : ").append(r.getCheckOutDate()).append("\n");
        sb.append("Nights         : ").append(nights).append("\n");
        sb.append("Total Price    : ").append(r.getTotalPrice()).append("\n");

        if (isStatusAllowedForCheckIn(status)) {
            sb.append("\n✅ This reservation is eligible for Check-In.");
        } else {
            sb.append("\n❌ This reservation is NOT eligible for Check-In.");
        }

        txtReservationDetails.setText(sb.toString());
    }

    private boolean isStatusAllowedForCheckIn(String status) {
        String s = status == null ? "" : status.trim().toUpperCase();
        return !s.equals("CHECKED_IN") && !s.equals("CHECKED_OUT") && !s.equals("CANCELLED");
    }

    private void confirmCheckIn() {
        if (cmbReservations.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation.");
            return;
        }

        int reservationId = parseReservationId(cmbReservations.getSelectedItem().toString());
        String status = reservationStatusMap.getOrDefault(reservationId, "");

        if (!isStatusAllowedForCheckIn(status)) {
            JOptionPane.showMessageDialog(this,
                    "You cannot Check-In this reservation.\nCurrent Status: " + status,
                    "Not Allowed",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Confirm check-in for this reservation?",
                "Confirm Check-In",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        Reservation r = reservationDAO.getReservationById(reservationId);
        if (r == null) {
            JOptionPane.showMessageDialog(this, "Reservation not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean ok1 = reservationDAO.updateStatus(reservationId, "CHECKED_IN");
        boolean ok2 = roomDAO.updateRoomStatusById(r.getRoomId(), "Occupied");

        if (ok1 && ok2) {
            JOptionPane.showMessageDialog(this, "Check-In completed successfully.");
            loadAllReservationsFromDB();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Check-In failed.\nReservation updated: " + ok1 + "\nRoom updated: " + ok2,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class ReservationRow {
        int reservationId;
        int guestId;
        int roomId;
        String checkIn;
        String checkOut;
        double total;
        String status;
    }
}
