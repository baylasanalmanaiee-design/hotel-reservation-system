package com.mycompany.hotelreservationsystem.ui.billing;

import com.mycompany.hotelreservationsystem.DatabaseConnection;
import com.mycompany.hotelreservationsystem.dao.DiscountDAO;
import com.mycompany.hotelreservationsystem.dao.ReservationDAO;
import com.mycompany.hotelreservationsystem.model.Reservation;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class CheckOutScreen extends JDialog {

    private JComboBox<String> cmbReservations;
    private JTextField txtNights, txtRoomPrice, txtDiscount, txtPenalty;
    private JTextArea txtExtraServices;
    private JTable chargesTable;
    private DefaultTableModel tableModel;

    private JButton btnGenerateInvoice, btnCompleteCheckout, btnCancel;

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final DiscountDAO discountDAO = new DiscountDAO();

    private static final double TAX_RATE = 0.10;
    private static final double SERVICE_FEE = 25.00;

    // cached values
    private double subtotal = 0;
    private double discountAmount = 0;
    private double taxAmount = 0;
    private double totalAmount = 0;

    public CheckOutScreen(JFrame parent) {
        super(parent, "Check-Out", true);
        setSize(850, 650);
        setLocationRelativeTo(parent);
        initUI();
        loadCheckedInReservations();
        addListeners();
    }

    // ================= UI =================

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(main);

        // --------- Top ---------
        JPanel top = new JPanel(new GridLayout(2, 1, 10, 10));
        top.setBorder(BorderFactory.createTitledBorder("Select Reservation"));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Reservation (CHECKED_IN):"));
        cmbReservations = new JComboBox<>();
        cmbReservations.setPreferredSize(new Dimension(420, 25));
        row1.add(cmbReservations);

        JPanel row2 = new JPanel(new GridLayout(1, 4, 10, 10));
        row2.add(new JLabel("Nights:"));
        txtNights = new JTextField(); txtNights.setEditable(false);
        row2.add(txtNights);

        row2.add(new JLabel("Room Price/Night:"));
        txtRoomPrice = new JTextField(); txtRoomPrice.setEditable(false);
        row2.add(txtRoomPrice);

        top.add(row1);
        top.add(row2);

        main.add(top, BorderLayout.NORTH);

        // --------- Center ---------
        String[] cols = {"Description", "Amount"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        chargesTable = new JTable(tableModel);
        chargesTable.setRowHeight(24);

        JScrollPane tableScroll = new JScrollPane(chargesTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Charges Breakdown"));

        txtExtraServices = new JTextArea(4, 40);
        txtExtraServices.setLineWrap(true);
        txtExtraServices.setWrapStyleWord(true);

        JScrollPane servicesScroll = new JScrollPane(txtExtraServices);
        servicesScroll.setBorder(BorderFactory.createTitledBorder("Extra Services (example: Laundry=30)"));

        JPanel discountPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        discountPanel.add(new JLabel("Discount Code:"));
        txtDiscount = new JTextField();
        discountPanel.add(txtDiscount);

        discountPanel.add(new JLabel("Late Penalty:"));
        txtPenalty = new JTextField("0.00");
        discountPanel.add(txtPenalty);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.add(tableScroll, BorderLayout.CENTER);

        JPanel southCenter = new JPanel(new BorderLayout());
        southCenter.add(discountPanel, BorderLayout.NORTH);
        southCenter.add(servicesScroll, BorderLayout.SOUTH);

        center.add(southCenter, BorderLayout.SOUTH);

        main.add(center, BorderLayout.CENTER);

        // --------- Bottom ---------
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGenerateInvoice = new JButton("Generate Invoice");
        btnCompleteCheckout = new JButton("Complete Check-Out");
        btnCancel = new JButton("Cancel");

        btnGenerateInvoice.addActionListener(e -> generateInvoice());
        btnCompleteCheckout.addActionListener(e -> completeCheckout());
        btnCancel.addActionListener(e -> dispose());

        bottom.add(btnGenerateInvoice);
        bottom.add(btnCompleteCheckout);
        bottom.add(btnCancel);

        main.add(bottom, BorderLayout.SOUTH);
    }

    // ================= Logic =================

    private void addListeners() {
        cmbReservations.addActionListener(e -> updateCharges());
        autoUpdate(txtDiscount);
        autoUpdate(txtPenalty);
        autoUpdate(txtExtraServices);
    }

    private void autoUpdate(JTextComponent c) {
        c.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateCharges(); }
            public void removeUpdate(DocumentEvent e) { updateCharges(); }
            public void changedUpdate(DocumentEvent e) { updateCharges(); }
        });
    }

    private void loadCheckedInReservations() {
        cmbReservations.removeAllItems();
        for (Reservation r : getCheckedInReservations()) {
            cmbReservations.addItem(r.getId() + " | Guest " + r.getGuestId() + " | Room " + r.getRoomId());
        }
        if (cmbReservations.getItemCount() > 0) {
            cmbReservations.setSelectedIndex(0);
            updateCharges();
        }
    }

    private List<Reservation> getCheckedInReservations() {
        List<Reservation> list = new ArrayList<>();
        String sql = """
            SELECT reservation_id, guest_id, room_id, check_in_date, check_out_date, total_price
            FROM reservations
            WHERE status='CHECKED_IN'
        """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Reservation(
                        rs.getInt("reservation_id"),
                        rs.getInt("guest_id"),
                        rs.getInt("room_id"),
                        rs.getString("check_in_date"),
                        rs.getString("check_out_date"),
                        rs.getDouble("total_price")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private int parseReservationId() {
        if (cmbReservations.getSelectedItem() == null) return 0;
        try {
            return Integer.parseInt(cmbReservations.getSelectedItem().toString().split("\\|")[0].trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private void updateCharges() {
        tableModel.setRowCount(0);

        int reservationId = parseReservationId();
        if (reservationId == 0) return;

        Reservation r = reservationDAO.getReservationById(reservationId);
        if (r == null) return;

        long nights = calcNights(r.getCheckInDate(), r.getCheckOutDate());
        double roomPrice = getRoomPrice(r.getRoomId());

        txtNights.setText(String.valueOf(nights));
        txtRoomPrice.setText(money(roomPrice));

        double roomTotal = nights * roomPrice;
        double servicesTotal = parseServices(txtExtraServices.getText());
        double penalty = parseDouble(txtPenalty.getText());

        taxAmount = (roomTotal + servicesTotal) * TAX_RATE;
        subtotal = roomTotal + servicesTotal + taxAmount + SERVICE_FEE + penalty;

        // ===== DISCOUNT =====
        double discountPct = 0;
        if (!txtDiscount.getText().isBlank()) {
            discountPct = discountDAO.getPercentageIfValid(txtDiscount.getText().trim());
        }
        discountAmount = (roomTotal + servicesTotal) * (discountPct / 100.0);

        totalAmount = subtotal - discountAmount;

        // ===== Table =====
        tableModel.addRow(new Object[]{"Room Charges", money(roomTotal)});
        tableModel.addRow(new Object[]{"Extra Services", money(servicesTotal)});
        tableModel.addRow(new Object[]{"Tax", money(taxAmount)});
        tableModel.addRow(new Object[]{"Service Fee", money(SERVICE_FEE)});
        if (penalty > 0)
            tableModel.addRow(new Object[]{"Late Penalty", money(penalty)});

        tableModel.addRow(new Object[]{"SUBTOTAL", money(subtotal)});

        if (discountAmount > 0) {
            tableModel.addRow(new Object[]{
                    "DISCOUNT (" + discountPct + "%)",
                    "-" + money(discountAmount)
            });
        }

        tableModel.addRow(new Object[]{"TOTAL AMOUNT", money(totalAmount)});
    }

    private void generateInvoice() {
        int reservationId = parseReservationId();
        if (reservationId == 0) return;

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     """
                     INSERT INTO invoices
                     (reservation_id, subtotal, discount_amount, tax_amount, total_amount, created_at)
                     VALUES (?, ?, ?, ?, ?, ?)
                     """)) {

            ps.setInt(1, reservationId);
            ps.setDouble(2, subtotal);
            ps.setDouble(3, discountAmount);
            ps.setDouble(4, taxAmount);
            ps.setDouble(5, totalAmount);
            ps.setString(6, LocalDateTime.now().toString());

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Invoice generated successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to generate invoice.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void completeCheckout() {
        int reservationId = parseReservationId();
        if (reservationId == 0) return;

        Reservation r = reservationDAO.getReservationById(reservationId);
        if (r == null) return;

        try (Connection c = DatabaseConnection.getConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement p1 = c.prepareStatement(
                    "UPDATE reservations SET status='CHECKED_OUT' WHERE reservation_id=?");
                 PreparedStatement p2 = c.prepareStatement(
                         "UPDATE rooms SET status='Cleaning Required' WHERE room_id=?")) {

                p1.setInt(1, reservationId);
                p2.setInt(1, r.getRoomId());

                p1.executeUpdate();
                p2.executeUpdate();

                c.commit();
                JOptionPane.showMessageDialog(this, "Check-Out completed.");
                dispose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= Helpers =================

    private long calcNights(String in, String out) {
        try {
            long n = ChronoUnit.DAYS.between(LocalDate.parse(in), LocalDate.parse(out));
            return Math.max(1, n);
        } catch (Exception e) {
            return 1;
        }
    }

    private double getRoomPrice(int roomId) {
        String sql = """
            SELECT rt.base_price
            FROM rooms r
            JOIN room_types rt ON r.type_id = rt.type_id
            WHERE r.room_id = ?
        """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("base_price");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private double parseServices(String text) {
        double sum = 0;
        if (text == null || text.isBlank()) return 0;
        for (String line : text.split("\\r?\\n")) {
            String[] p = line.split("[:=\\-]", 2);
            if (p.length == 2) sum += parseDouble(p[1]);
        }
        return sum;
    }

    private double parseDouble(String t) {
        try {
            return Double.parseDouble(t.replace("$", "").trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private String money(double v) {
        return String.format("$%.2f", v);
    }
}
