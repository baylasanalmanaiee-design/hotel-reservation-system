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
    private final DiscountDAO discountDAO = new DiscountDAO();

    private static final double TAX_RATE = 0.10;
    private static final double SERVICE_FEE = 25.00;

    public CheckOutScreen(JFrame parent) {
        super(parent, "Check-Out", true);
        setSize(850, 650);
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
        cmbReservations.setPreferredSize(new Dimension(420, 25));
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

        txtExtraServices = new JTextArea(5, 40);
        txtExtraServices.setLineWrap(true);
        txtExtraServices.setWrapStyleWord(true);

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
        addAutoUpdate(txtDiscount);
        addAutoUpdate(txtPenalty);
        addAutoUpdate(txtExtraServices);
    }

    private void addAutoUpdate(JTextComponent comp) {
        comp.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateCharges(); }
            @Override public void removeUpdate(DocumentEvent e) { updateCharges(); }
            @Override public void changedUpdate(DocumentEvent e) { updateCharges(); }
        });
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    private int parseReservationId(String text) {
        try { return Integer.parseInt(text.split("\\|")[0].trim()); }
        catch (Exception e) { return 0; }
    }

    private void updateCharges() {
        tableModel.setRowCount(0);
        if (cmbReservations.getSelectedItem() == null) return;

        int id = parseReservationId(cmbReservations.getSelectedItem().toString());
        Reservation r = reservationDAO.getReservationById(id);
        if (r == null) return;

        long nights = calculateNights(r.getCheckInDate(), r.getCheckOutDate());
        double roomPrice = getRoomPricePerNight(r.getRoomId());

        txtNights.setText(String.valueOf(nights));
        txtRoomPrice.setText(money(roomPrice));

        double roomTotal = nights * roomPrice;
        List<ServiceLine> services = parseServices(txtExtraServices.getText());
        double servicesTotal = services.stream().mapToDouble(x -> x.amount).sum();

        double tax = (roomTotal + servicesTotal) * TAX_RATE;
        double penalty = parseDouble(txtPenalty.getText());

        tableModel.addRow(new Object[]{"Room Charges", money(roomTotal)});

        for (ServiceLine s : services) {
            tableModel.addRow(new Object[]{"Extra: " + s.name, money(s.amount)});
        }

        tableModel.addRow(new Object[]{"Tax", money(tax)});
        tableModel.addRow(new Object[]{"Service Fee", money(SERVICE_FEE)});

        if (penalty > 0) {
            tableModel.addRow(new Object[]{"Late Penalty", money(penalty)});
        }

        double subtotal = roomTotal + servicesTotal + tax + SERVICE_FEE + penalty;
        double discountPct = txtDiscount.getText().isBlank() ? 0.0 : discountDAO.getPercentageIfValid(txtDiscount.getText().trim());
        double discount = (roomTotal + servicesTotal) * (discountPct / 100.0);
        double total = subtotal - discount;

        tableModel.addRow(new Object[]{"SUBTOTAL", money(subtotal)});
        tableModel.addRow(new Object[]{"DISCOUNT", "-" + money(discount)});
        tableModel.addRow(new Object[]{"TOTAL AMOUNT", money(total)});
    }

    private long calculateNights(String in, String out) {
        try {
            long n = ChronoUnit.DAYS.between(LocalDate.parse(in), LocalDate.parse(out));
            return Math.max(1, n);
        } catch (Exception e) {
            return 1;
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
            try (ResultSet r = s.executeQuery()) {
                if (r.next()) return r.getDouble("base_price");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    private void generateInvoice() {
        if (cmbReservations.getSelectedIndex() == -1) return;

        int reservationId = parseReservationId(cmbReservations.getSelectedItem().toString());
        double total = parseDouble(getAmountFromRow("TOTAL AMOUNT"));

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(
                     "INSERT INTO invoices (reservation_id, amount, date) VALUES (?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            s.setInt(1, reservationId);
            s.setDouble(2, total);
            s.setString(3, java.time.LocalDateTime.now().toString());
            s.executeUpdate();

            try (ResultSet r = s.getGeneratedKeys()) {
                if (r.next()) {
                    JOptionPane.showMessageDialog(this, "Invoice saved. ID: " + r.getInt(1));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getAmountFromRow(String label) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (label.equals(tableModel.getValueAt(i, 0))) {
                return tableModel.getValueAt(i, 1).toString();
            }
        }
        return "0";
    }

    private void completeCheckout() {
        if (cmbReservations.getSelectedIndex() == -1) return;

        int reservationId = parseReservationId(cmbReservations.getSelectedItem().toString());
        Reservation r = reservationDAO.getReservationById(reservationId);
        if (r == null) return;

        try (Connection c = DatabaseConnection.getConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement s1 = c.prepareStatement(
                    "UPDATE reservations SET status='CHECKED_OUT' WHERE reservation_id=?");
                 PreparedStatement s2 = c.prepareStatement(
                         "UPDATE rooms SET status='Cleaning Required' WHERE room_id=?")) {

                s1.setInt(1, reservationId);
                s2.setInt(1, r.getRoomId());

                s1.executeUpdate();
                s2.executeUpdate();

                c.commit();
                JOptionPane.showMessageDialog(this, "Check-Out completed successfully.");
                loadCheckedInReservations();
                dispose();
            } catch (Exception ex) {
                c.rollback();
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static class ServiceLine {
        String name;
        double amount;
        ServiceLine(String n, double a) {
            name = n;
            amount = a;
        }
    }

    private List<ServiceLine> parseServices(String text) {
        List<ServiceLine> list = new ArrayList<>();
        if (text == null || text.isBlank()) return list;

        for (String line : text.split("\\r?\\n")) {
            String[] p = line.split("[:=\\-]", 2);
            if (p.length == 2) {
                double a = parseDouble(p[1]);
                if (a > 0) list.add(new ServiceLine(p[0].trim(), a));
            }
        }
        return list;
    }
}
