package com.mycompany.hotelreservationsystem.ui.billing;

import com.mycompany.hotelreservationsystem.DatabaseConnection;
import com.mycompany.hotelreservationsystem.dao.DiscountDAO;
import com.mycompany.hotelreservationsystem.dao.InvoiceDAO;
import com.mycompany.hotelreservationsystem.dao.ReservationDAO;
import com.mycompany.hotelreservationsystem.dao.RoomDAO;
import com.mycompany.hotelreservationsystem.model.Invoice;
import com.mycompany.hotelreservationsystem.model.Reservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class CheckOutScreen extends JDialog {

    private JComboBox<String> cmbReservations;
    private JTextField txtNights, txtRoomPrice, txtDiscount, txtPenalty;
    private JTextArea txtExtraServices;
    private JButton btnGenerateInvoice, btnCompleteCheckout, btnCancel;
    private JTable chargesTable;
    private DefaultTableModel tableModel;

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final DiscountDAO discountDAO = new DiscountDAO();

    private static final double TAX_RATE = 0.10;
    private static final double SERVICE_FEE = 25.00;

    public CheckOutScreen(JFrame parent) {
        super(parent, "Check-Out", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        initializeComponents();
    }

    private void initializeComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        mainPanel.add(createSelectionPanel(), BorderLayout.NORTH);
        mainPanel.add(createChargesPanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);

        loadCheckedInReservations();
        addListeners();
    }

    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Select Reservation for Check-Out"));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Reservation (CHECKED_IN):"));
        cmbReservations = new JComboBox<>();
        cmbReservations.setPreferredSize(new Dimension(360, 25));
        top.add(cmbReservations);

        JPanel bottom = new JPanel(new GridLayout(1, 4, 10, 10));
        bottom.add(new JLabel("Nights:"));
        txtNights = new JTextField();
        txtNights.setEditable(false);
        bottom.add(txtNights);

        bottom.add(new JLabel("Room Price/Night:"));
        txtRoomPrice = new JTextField();
        txtRoomPrice.setEditable(false);
        bottom.add(txtRoomPrice);

        panel.add(top);
        panel.add(bottom);

        return panel;
    }

    private JPanel createChargesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        String[] columns = {"Description", "Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        chargesTable = new JTable(tableModel);
        chargesTable.setRowHeight(24);

        JScrollPane tableScroll = new JScrollPane(chargesTable);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Charges Breakdown"));
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        txtExtraServices = new JTextArea(3, 40);
        JScrollPane servicesScroll = new JScrollPane(txtExtraServices);
        JPanel servicesPanel = new JPanel(new BorderLayout());
        servicesPanel.setBorder(BorderFactory.createTitledBorder("Extra Services"));
        servicesPanel.add(servicesScroll, BorderLayout.CENTER);

        JPanel discountPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        discountPanel.add(new JLabel("Discount Code:"));
        txtDiscount = new JTextField();
        discountPanel.add(txtDiscount);
        discountPanel.add(new JLabel("Late Penalty:"));
        txtPenalty = new JTextField("0.00");
        discountPanel.add(txtPenalty);

        panel.add(tablePanel, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.add(discountPanel, BorderLayout.NORTH);
        south.add(servicesPanel, BorderLayout.SOUTH);

        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnGenerateInvoice = new JButton("Generate Invoice");
        btnCompleteCheckout = new JButton("Complete Check-Out");
        btnCancel = new JButton("Cancel");

        btnGenerateInvoice.addActionListener(e -> generateInvoice());
        btnCompleteCheckout.addActionListener(e -> completeCheckout());
        btnCancel.addActionListener(e -> dispose());

        panel.add(btnGenerateInvoice);
        panel.add(btnCompleteCheckout);
        panel.add(btnCancel);

        return panel;
    }

    private void addListeners() {
        cmbReservations.addActionListener(e -> updateCharges());
        txtDiscount.addActionListener(e -> updateCharges());
        txtPenalty.addActionListener(e -> updateCharges());
    }

    private void loadCheckedInReservations() {
        cmbReservations.removeAllItems();
        for (Reservation r : getCheckedInReservationsFromDB()) {
            cmbReservations.addItem(r.getId() + " | Guest " + r.getGuestId() + " | Room " + r.getRoomId());
        }
        if (cmbReservations.getItemCount() > 0) {
            cmbReservations.setSelectedIndex(0);
            updateCharges();
        }
    }

    private List<Reservation> getCheckedInReservationsFromDB() {
        List<Reservation> list = new ArrayList<>();
        String sql = """
            SELECT reservation_id, guest_id, room_id, check_in_date, check_out_date, total_price
            FROM reservations
            WHERE status = 'CHECKED_IN'
            ORDER BY reservation_id DESC
        """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql);
             ResultSet r = s.executeQuery()) {
            while (r.next()) {
                list.add(new Reservation(
                        r.getInt("reservation_id"),
                        r.getInt("guest_id"),
                        r.getInt("room_id"),
                        r.getString("check_in_date"),
                        r.getString("check_out_date"),
                        r.getDouble("total_price")
                ));
            }
        } catch (Exception ignored) {}
        return list;
    }

    private int parseReservationId(String text) {
        try {
            return Integer.parseInt(text.split("\\|")[0].trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private void updateCharges() {
        tableModel.setRowCount(0);
        if (cmbReservations.getSelectedItem() == null) return;

        int id = parseReservationId(cmbReservations.getSelectedItem().toString());
        Reservation r = reservationDAO.getReservationById(id);
        if (r == null) return;

        long nights = calculateNights(r.getCheckInDate(), r.getCheckOutDate());
        double price = getRoomPricePerNight(r.getRoomId());

        txtNights.setText(String.valueOf(nights));
        txtRoomPrice.setText(String.format("$%.2f", price));

        double roomTotal = nights * price;
        double tax = roomTotal * TAX_RATE;
        double penalty = parseDouble(txtPenalty.getText());

        tableModel.addRow(new Object[]{"Room Charges", money(roomTotal)});
        tableModel.addRow(new Object[]{"Tax", money(tax)});
        tableModel.addRow(new Object[]{"Service Fee", money(SERVICE_FEE)});

        double subtotal = roomTotal + tax + SERVICE_FEE + penalty;

        double discountPct = txtDiscount.getText().isBlank() ? 0.0 :
                discountDAO.getPercentageIfValid(txtDiscount.getText().trim());

        double discount = subtotal * (discountPct / 100.0);
        double total = subtotal - discount;

        tableModel.addRow(new Object[]{"SUBTOTAL", money(subtotal)});
        tableModel.addRow(new Object[]{"DISCOUNT", "-" + money(discount)});
        tableModel.addRow(new Object[]{"TOTAL AMOUNT", money(total)});
    }

    private long calculateNights(String in, String out) {
        try {
            return Math.max(0, ChronoUnit.DAYS.between(LocalDate.parse(in), LocalDate.parse(out)));
        } catch (Exception e) {
            return 0;
        }
    }

    private double getRoomPricePerNight(int roomId) {
        String sql = """
            SELECT rt.base_price
            FROM rooms r
            JOIN room_types rt ON r.type_id = rt.type_id
            WHERE r.room_id = ?
        """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, roomId);
            ResultSet r = s.executeQuery();
            if (r.next()) return r.getDouble("base_price");
        } catch (Exception ignored) {}
        return 0.0;
    }

    private double parseDouble(String t) {
        try {
            return Double.parseDouble(t.replace("$", "").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private String money(double v) {
        return String.format("$%.2f", v);
    }

    private double getAmountFromRow(String label) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (label.equals(tableModel.getValueAt(i, 0))) {
                return parseDouble(tableModel.getValueAt(i, 1).toString());
            }
        }
        return 0.0;
    }

    private void generateInvoice() {
        if (cmbReservations.getSelectedIndex() == -1) return;

        int id = parseReservationId(cmbReservations.getSelectedItem().toString());
        double total = getAmountFromRow("TOTAL AMOUNT");

        Invoice inv = new Invoice();
        inv.setReservationId(id);
        inv.setAmount(total);
        inv.setDate(java.time.LocalDateTime.now().toString());

        int invoiceId = InvoiceDAO.insert(inv);
        if (invoiceId > 0)
            JOptionPane.showMessageDialog(this, "Invoice saved. ID: " + invoiceId);
    }

    private void completeCheckout() {
        if (cmbReservations.getSelectedIndex() == -1) return;

        int id = parseReservationId(cmbReservations.getSelectedItem().toString());
        Reservation r = reservationDAO.getReservationById(id);
        if (r == null) return;

        boolean ok1 = reservationDAO.updateStatus(id, "CHECKED_OUT");
        boolean ok2 = roomDAO.updateRoomStatus(r.getRoomId(), "Cleaning Required");

        if (ok1 && ok2) {
            JOptionPane.showMessageDialog(this, "Check-Out completed successfully.");
            loadCheckedInReservations();
            dispose();
        }
    }
}
