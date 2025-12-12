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
import java.util.List;

public class CheckInScreen extends JDialog {

    private JComboBox<String> cmbReservations;
    private JTextField txtGuestId;
    private JTextField txtDeposit;
    private JTextArea txtReservationDetails;
    private JButton btnConfirm;
    private JButton btnCancel;

    private final ReservationDAO reservationDAO = new ReservationDAO(); // نستخدمه فقط لـ getReservationById + updateStatus
    private final RoomDAO roomDAO = new RoomDAO();
    private final GuestDAO guestDAO = new GuestDAO();

    public CheckInScreen(JFrame parent) {
        super(parent, "Check-In Guest", true);
        setSize(600, 500);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel selectionPanel = createSelectionPanel();
        JPanel detailsPanel = createDetailsPanel();
        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(selectionPanel, BorderLayout.NORTH);
        mainPanel.add(detailsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadReservationsFromDB();
        addReservationChangeListener();
    }

    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Select Reservation"));

        panel.add(new JLabel("Reservation (CONFIRMED):"));
        cmbReservations = new JComboBox<>();
        panel.add(cmbReservations);

        panel.add(new JLabel("Guest ID / Passport:"));
        txtGuestId = new JTextField();
        panel.add(txtGuestId);

        panel.add(new JLabel("Deposit Amount:"));
        txtDeposit = new JTextField();
        panel.add(txtDeposit);

        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Reservation Details"));

        txtReservationDetails = new JTextArea(10, 40);
        txtReservationDetails.setEditable(false);
        txtReservationDetails.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtReservationDetails.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(txtReservationDetails);
        panel.add(scroll, BorderLayout.CENTER);

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

    // ==========================
    // ✅ Load CONFIRMED reservations directly from DB
    // ==========================
    private void loadReservationsFromDB() {
        cmbReservations.removeAllItems();

        List<Reservation> list = getConfirmedReservationsFromDB();

        for (Reservation r : list) {
            // نخزن الـ ID الحقيقي في بداية النص عشان نطلّعه بسهولة
            String item = r.getId() + " | Guest " + r.getGuestId() + " | Room " + r.getRoomId();
            cmbReservations.addItem(item);
        }

        if (cmbReservations.getItemCount() > 0) {
            cmbReservations.setSelectedIndex(0);
            updateReservationDetails();
        } else {
            txtReservationDetails.setText("No CONFIRMED reservations found.");
        }
    }

    private void addReservationChangeListener() {
        cmbReservations.addActionListener(e -> updateReservationDetails());
    }

    // نجيب قائمة الحجوزات المؤكدة من DB بدون ما نعدل ReservationDAO
    private List<Reservation> getConfirmedReservationsFromDB() {
        List<Reservation> list = new ArrayList<>();

        String sql = """
            SELECT reservation_id, guest_id, room_id, check_in_date, check_out_date, total_price
            FROM reservations
            WHERE status = 'CONFIRMED'
            ORDER BY reservation_id DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Reservation r = new Reservation(
                        rs.getInt("reservation_id"),
                        rs.getInt("guest_id"),
                        rs.getInt("room_id"),
                        rs.getString("check_in_date"),
                        rs.getString("check_out_date"),
                        rs.getDouble("total_price")
                );
                list.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading reservations: " + e.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return list;
    }

    // يقرأ الرقم قبل | (مثال: "12 | Guest 3 | Room 5")
    private int parseReservationId(String comboText) {
        if (comboText == null) return 0;
        String[] parts = comboText.split("\\|");
        try {
            return Integer.parseInt(parts[0].trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private void updateReservationDetails() {
        String selected = (String) cmbReservations.getSelectedItem();
        if (selected == null) {
            txtReservationDetails.setText("");
            return;
        }

        int reservationId = parseReservationId(selected);
        if (reservationId <= 0) {
            txtReservationDetails.setText("");
            return;
        }

        Reservation r = reservationDAO.getReservationById(reservationId);
        if (r == null) {
            txtReservationDetails.setText("");
            return;
        }

        Guest g = guestDAO.getGuestById(r.getGuestId());
        String guestName = (g != null && g.getFullName() != null && !g.getFullName().isBlank())
                ? g.getFullName()
                : ("Guest " + r.getGuestId());

        long nights;
        try {
            LocalDate in = LocalDate.parse(r.getCheckInDate());
            LocalDate out = LocalDate.parse(r.getCheckOutDate());
            nights = ChronoUnit.DAYS.between(in, out);
            if (nights < 0) nights = 0;
        } catch (Exception ignored) {
            nights = 0;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Reservation ID : ").append(r.getId()).append("\n");
        sb.append("Guest          : ").append(guestName).append(" (Guest ID ").append(r.getGuestId()).append(")\n");
        sb.append("Room ID        : ").append(r.getRoomId()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Check-In Date  : ").append(r.getCheckInDate()).append("\n");
        sb.append("Check-Out Date : ").append(r.getCheckOutDate()).append("\n");
        sb.append("Nights         : ").append(nights).append("\n");
        sb.append("Status         : CONFIRMED").append("\n");
        sb.append("Total Price    : ").append(r.getTotalPrice()).append("\n");

        txtReservationDetails.setText(sb.toString());
    }

    private void confirmCheckIn() {
        if (!validateInput()) return;

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Confirm check-in for this reservation?",
                "Confirm Check-In",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (answer != JOptionPane.YES_OPTION) return;

        String selected = (String) cmbReservations.getSelectedItem();
        int reservationId = parseReservationId(selected);

        Reservation r = reservationDAO.getReservationById(reservationId);
        if (r == null) {
            JOptionPane.showMessageDialog(this, "Reservation not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ✅ تحديث حالة الحجز باستخدام DAO الأصلي
        boolean ok1 = reservationDAO.updateStatus(reservationId, "CHECKED_IN");

        // ✅ تحديث حالة الغرفة (لازم توافق قيم rooms.status عندكم)
        // عندكم غالبًا: Available / Occupied / Cleaning Required / Maintenance
        boolean ok2 = roomDAO.updateRoomStatus(r.getRoomId(), "Occupied");

        if (ok1 && ok2) {
            JOptionPane.showMessageDialog(
                    this,
                    "Check-In completed successfully.\nReservation status -> CHECKED_IN\nRoom status -> Occupied",
                    "Check-In Done",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // ملاحظة: الآن deposit فقط validated ومكتوب في UI
            // حفظه في payments/invoices بيجي في الخطوة الجاية

            loadReservationsFromDB();
            dispose();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Check-In failed (could not update reservation/room).",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private boolean validateInput() {
        if (cmbReservations.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation.");
            return false;
        }

        String id = txtGuestId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter guest ID / passport.");
            return false;
        }

        String depositText = txtDeposit.getText().trim();
        if (depositText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter deposit amount (0 if none).");
            return false;
        }

        try {
            double deposit = Double.parseDouble(depositText);
            if (deposit < 0) {
                JOptionPane.showMessageDialog(this, "Deposit cannot be negative.");
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Deposit must be a numeric value.");
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        CheckInScreen screen = new CheckInScreen(frame);
        screen.setVisible(true);
    }
}
