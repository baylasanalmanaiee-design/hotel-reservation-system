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

    // Bottom buttons (main screen)
    private JButton btnGenerateInvoice, btnPay, btnRefund, btnCompleteCheckout, btnCancel;

    // Payment summary (main screen)
    private JLabel lblPaid, lblRefunds, lblBalance, lblTotal;

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final DiscountDAO discountDAO = new DiscountDAO();

    private static final double TAX_RATE = 0.10;
    private static final double SERVICE_FEE = 25.00;

    // cached values
    private double subtotal = 0;
    private double discountAmount = 0;
    private double taxAmount = 0;
    private double totalAmount = 0;

    // invoice id after generating
    private Integer currentInvoiceId = null;

    public CheckOutScreen(JFrame parent) {
        super(parent, "Check-Out", true);
        setSize(920, 700);
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
        cmbReservations.setPreferredSize(new Dimension(520, 25));
        row1.add(cmbReservations);

        JPanel row2 = new JPanel(new GridLayout(1, 4, 10, 10));
        row2.add(new JLabel("Nights:"));
        txtNights = new JTextField();
        txtNights.setEditable(false);
        row2.add(txtNights);

        row2.add(new JLabel("Room Price/Night:"));
        txtRoomPrice = new JTextField();
        txtRoomPrice.setEditable(false);
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
        tableScroll.setBorder(BorderFactory.createTitledBorder("Charges Breakdown (Itemized)"));

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

        // --------- Bottom (Summary + Buttons) ---------
        JPanel bottom = new JPanel(new BorderLayout(10, 10));

        JPanel summary = new JPanel(new GridLayout(2, 2, 10, 6));
        summary.setBorder(BorderFactory.createTitledBorder("Payment Summary"));

        lblTotal = new JLabel("Total: $0.00");
        lblPaid = new JLabel("Paid: $0.00");
        lblRefunds = new JLabel("Refunds: $0.00");
        lblBalance = new JLabel("Balance: $0.00");

        summary.add(lblTotal);
        summary.add(lblPaid);
        summary.add(lblRefunds);
        summary.add(lblBalance);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnGenerateInvoice = new JButton("Generate Invoice");
        btnPay = new JButton("Pay");
        btnRefund = new JButton("Refund");
        btnCompleteCheckout = new JButton("Complete Check-Out");
        btnCancel = new JButton("Cancel");

        btnGenerateInvoice.addActionListener(e -> generateInvoice());
        btnPay.addActionListener(e -> openPayDialog());
        btnRefund.addActionListener(e -> refundQuick());
        btnCompleteCheckout.addActionListener(e -> completeCheckout());
        btnCancel.addActionListener(e -> dispose());

        setPaymentActionsEnabled(false);

        buttons.add(btnGenerateInvoice);
        buttons.add(btnPay);
        buttons.add(btnRefund);
        buttons.add(btnCompleteCheckout);
        buttons.add(btnCancel);

        bottom.add(summary, BorderLayout.NORTH);
        bottom.add(buttons, BorderLayout.SOUTH);

        main.add(bottom, BorderLayout.SOUTH);
    }

    // ================= Logic =================

    private void addListeners() {
        cmbReservations.addActionListener(e -> {
            currentInvoiceId = null;
            setPaymentActionsEnabled(false);
            updateCharges();
            updatePaymentSummary();
        });

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
        currentInvoiceId = null;
        setPaymentActionsEnabled(false);

        for (Reservation r : getCheckedInReservations()) {
            cmbReservations.addItem(r.getId() + " | Guest " + r.getGuestId() + " | Room " + r.getRoomId());
        }

        if (cmbReservations.getItemCount() > 0) {
            cmbReservations.setSelectedIndex(0);
            updateCharges();
        } else {
            tableModel.setRowCount(0);
            txtNights.setText("");
            txtRoomPrice.setText("");
            subtotal = discountAmount = taxAmount = totalAmount = 0;
            updatePaymentSummary();
        }
    }

    private List<Reservation> getCheckedInReservations() {
        List<Reservation> list = new ArrayList<>();
        String sql =
                "SELECT reservation_id, guest_id, room_id, check_in_date, check_out_date, total_price " +
                        "FROM reservations WHERE status='CHECKED_IN'";

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
        if (reservationId == 0) {
            subtotal = discountAmount = taxAmount = totalAmount = 0;
            updatePaymentSummary();
            return;
        }

        Reservation r = reservationDAO.getReservationById(reservationId);
        if (r == null) {
            subtotal = discountAmount = taxAmount = totalAmount = 0;
            updatePaymentSummary();
            return;
        }

        long nights = calcNights(r.getCheckInDate(), r.getCheckOutDate());
        double roomPrice = getRoomPrice(r.getRoomId());

        txtNights.setText(String.valueOf(nights));
        txtRoomPrice.setText(money(roomPrice));

        double roomTotal = nights * roomPrice;
        double servicesTotal = parseServices(txtExtraServices.getText());
        double penalty = parseDouble(txtPenalty.getText());

        taxAmount = (roomTotal + servicesTotal) * TAX_RATE;
        subtotal = roomTotal + servicesTotal + taxAmount + SERVICE_FEE + penalty;

        double discountPct = 0;
        if (!txtDiscount.getText().isBlank()) {
            discountPct = discountDAO.getPercentageIfValid(txtDiscount.getText().trim());
        }
        discountAmount = (roomTotal + servicesTotal) * (discountPct / 100.0);

        totalAmount = subtotal - discountAmount;

        tableModel.addRow(new Object[]{"Room Charges", money(roomTotal)});
        tableModel.addRow(new Object[]{"Extra Services", money(servicesTotal)});
        tableModel.addRow(new Object[]{"Tax (10%)", money(taxAmount)});
        tableModel.addRow(new Object[]{"Service Fee", money(SERVICE_FEE)});
        if (penalty > 0) tableModel.addRow(new Object[]{"Late Penalty", money(penalty)});

        tableModel.addRow(new Object[]{"SUBTOTAL", money(subtotal)});
        if (discountAmount > 0) {
            tableModel.addRow(new Object[]{"DISCOUNT (" + discountPct + "%)", "-" + money(discountAmount)});
        }
        tableModel.addRow(new Object[]{"TOTAL AMOUNT", money(totalAmount)});

        updatePaymentSummary();
    }

    // ================= Invoice / Payments =================

    private void generateInvoice() {
        int reservationId = parseReservationId();
        if (reservationId == 0) return;

        String sql =
                "INSERT INTO invoices (reservation_id, subtotal, discount_amount, tax_amount, total_amount, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, reservationId);
            ps.setDouble(2, subtotal);
            ps.setDouble(3, discountAmount);
            ps.setDouble(4, taxAmount);
            ps.setDouble(5, totalAmount);
            ps.setString(6, LocalDateTime.now().toString());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) currentInvoiceId = keys.getInt(1);
            }

            JOptionPane.showMessageDialog(this,
                    "Invoice generated successfully." + (currentInvoiceId != null ? (" Invoice ID: " + currentInvoiceId) : ""));

            setPaymentActionsEnabled(currentInvoiceId != null);
            updatePaymentSummary();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to generate invoice.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openPayDialog() {
        if (currentInvoiceId == null) {
            JOptionPane.showMessageDialog(this, "Please generate the invoice first.");
            return;
        }

        final int invoiceId = currentInvoiceId;
        final int reservationId = parseReservationId();

        JDialog dlg = new JDialog(this, "Invoice Preview & Payment", true);
        dlg.setSize(780, 600);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(10, 10));

        // ===== Preview Area =====
        JTextArea area = new JTextArea(buildInvoiceText(invoiceId, reservationId));
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        dlg.add(new JScrollPane(area), BorderLayout.CENTER);

        // ===== Summary Top =====
        JPanel info = new JPanel(new GridLayout(2, 2, 10, 6));
        info.setBorder(BorderFactory.createTitledBorder("Current Balance"));
        JLabel lTotal = new JLabel();
        JLabel lPaidRefund = new JLabel();
        JLabel lBalance = new JLabel();
        info.add(lTotal);
        info.add(lPaidRefund);
        info.add(lBalance);
        dlg.add(info, BorderLayout.NORTH);

        Runnable refresh = () -> {
            double paidNow = getPaidTotal(invoiceId);
            double refundsNow = getRefundTotalAbs(invoiceId);
            double balanceNow = getCurrentBalance(invoiceId);

            lTotal.setText("Total: " + money(totalAmount));
            lPaidRefund.setText("Paid: " + money(paidNow) + " | Refunds: " + money(refundsNow));
            lBalance.setText("Balance: " + money(balanceNow));

            area.setText(buildInvoiceText(invoiceId, reservationId));
        };
        refresh.run();

        // ===== South: Payment Options + Buttons =====
        JPanel south = new JPanel(new BorderLayout(10, 10));

        // Payment options
        JPanel payOptions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        payOptions.setBorder(BorderFactory.createTitledBorder("Payment"));

        JRadioButton rbFull = new JRadioButton("Full payment");
        JRadioButton rbPartial = new JRadioButton("Partial payment");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbFull);
        bg.add(rbPartial);
        rbFull.setSelected(true);

        JTextField txtAmount = new JTextField(10);
        txtAmount.setEnabled(false);
        txtAmount.setText(String.format("%.2f", getCurrentBalance(invoiceId)));

        rbFull.addActionListener(e -> {
            txtAmount.setEnabled(false);
            txtAmount.setText(String.format("%.2f", getCurrentBalance(invoiceId)));
        });
        rbPartial.addActionListener(e -> {
            txtAmount.setEnabled(true);
            txtAmount.setText("0.00");
        });

        String[] methods = {"Cash", "Card", "Transfer"};
        JComboBox<String> cmbMethod = new JComboBox<>(methods);

        payOptions.add(rbFull);
        payOptions.add(rbPartial);
        payOptions.add(new JLabel("Amount:"));
        payOptions.add(txtAmount);
        payOptions.add(new JLabel("Method:"));
        payOptions.add(cmbMethod);

        south.add(payOptions, BorderLayout.CENTER);

        // ===== Bottom buttons (مرتبة) =====
        JPanel bottom = new JPanel(new BorderLayout(10, 10));

        // Left: Print + Export
        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnPrint = new JButton("Print / PDF");
        JButton btnExport = new JButton("Export TXT");
        exportPanel.add(btnPrint);
        exportPanel.add(btnExport);

        // Center: Pay
        JPanel payPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnPayNow = new JButton("Pay");
        btnPayNow.setPreferredSize(new Dimension(160, 40));
        payPanel.add(btnPayNow);

        // Right: Close
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("Close");
        closePanel.add(btnClose);

        bottom.add(exportPanel, BorderLayout.WEST);
        bottom.add(payPanel, BorderLayout.CENTER);
        bottom.add(closePanel, BorderLayout.EAST);

        south.add(bottom, BorderLayout.SOUTH);

        // Actions
        btnPrint.addActionListener(e -> printText(area.getText()));
        btnExport.addActionListener(e -> exportTextFromDialog(area.getText()));
        btnClose.addActionListener(e -> dlg.dispose());

        btnPayNow.addActionListener(e -> {
            double balanceNow = getCurrentBalance(invoiceId);
            if (balanceNow <= 0.001) {
                JOptionPane.showMessageDialog(dlg, "This invoice is already fully paid.");
                refresh.run();
                return;
            }

            double amountToPay = rbFull.isSelected() ? balanceNow : parseDouble(txtAmount.getText());

            if (amountToPay <= 0) {
                JOptionPane.showMessageDialog(dlg, "Enter a valid amount (> 0).");
                return;
            }
            if (amountToPay > balanceNow + 0.001) {
                JOptionPane.showMessageDialog(dlg, "Amount cannot be greater than balance.");
                return;
            }

            boolean ok = insertPayment(invoiceId, amountToPay, (String) cmbMethod.getSelectedItem());
            if (!ok) {
                JOptionPane.showMessageDialog(dlg, "Payment failed.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            updatePaymentSummary();
            refresh.run();

            if (getCurrentBalance(invoiceId) <= 0.001) {
                dlg.dispose();
                completeCheckout(); // auto
            } else {
                JOptionPane.showMessageDialog(dlg,
                        "Payment recorded.\nRemaining balance: " + money(getCurrentBalance(invoiceId)));
            }
        });

        dlg.add(south, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private boolean insertPayment(int invoiceId, double amount, String method) {
        String sql = "INSERT INTO payments (invoice_id, amount, method, paid_at) VALUES (?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, invoiceId);
            ps.setDouble(2, amount);
            ps.setString(3, method);
            ps.setString(4, LocalDateTime.now().toString());
            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void refundQuick() {
        if (currentInvoiceId == null) {
            JOptionPane.showMessageDialog(this, "Generate an invoice first.");
            return;
        }

        String amountStr = JOptionPane.showInputDialog(this, "Refund amount:", "0.00");
        if (amountStr == null) return;

        double amount = parseDouble(amountStr);
        if (amount <= 0) {
            JOptionPane.showMessageDialog(this, "Enter a valid refund amount (> 0).");
            return;
        }

        String[] methods = {"Cash", "Card", "Transfer"};
        String method = (String) JOptionPane.showInputDialog(
                this, "Refund method:", "Refund Method",
                JOptionPane.QUESTION_MESSAGE, null, methods, methods[0]
        );
        if (method == null) return;

        String sql = "INSERT INTO payments (invoice_id, amount, method, paid_at) VALUES (?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, currentInvoiceId);
            ps.setDouble(2, -amount); // negative = refund
            ps.setString(3, "Refund-" + method);
            ps.setString(4, LocalDateTime.now().toString());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Refund recorded.");
            updatePaymentSummary();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to record refund.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void completeCheckout() {
        int reservationId = parseReservationId();
        if (reservationId == 0) return;

        Reservation r = reservationDAO.getReservationById(reservationId);
        if (r == null) return;

        if (currentInvoiceId == null) {
            JOptionPane.showMessageDialog(this, "Please generate the invoice first.");
            return;
        }

        double balance = getCurrentBalance(currentInvoiceId);
        if (balance > 0.001) {
            JOptionPane.showMessageDialog(this,
                    "Cannot complete check-out.\nRemaining balance: " + money(balance),
                    "Payment Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

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
            JOptionPane.showMessageDialog(this, "Check-Out failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= Summary helpers =================

    private void setPaymentActionsEnabled(boolean enabled) {
        btnPay.setEnabled(enabled);
        btnRefund.setEnabled(enabled);
    }

    private double getPaidTotal(int invoiceId) {
        String sql = "SELECT COALESCE(SUM(amount),0) AS s FROM payments WHERE invoice_id=? AND amount > 0";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("s");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private double getRefundTotalAbs(int invoiceId) {
        String sql = "SELECT COALESCE(SUM(ABS(amount)),0) AS s FROM payments WHERE invoice_id=? AND amount < 0";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("s");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void updatePaymentSummary() {
        if (lblTotal == null) return;

        lblTotal.setText("Total: " + money(totalAmount));

        if (currentInvoiceId == null) {
            lblPaid.setText("Paid: $0.00");
            lblRefunds.setText("Refunds: $0.00");
            lblBalance.setText("Balance: " + money(totalAmount));
            return;
        }

        double paid = getPaidTotal(currentInvoiceId);
        double refunds = getRefundTotalAbs(currentInvoiceId);
        double balance = totalAmount - paid + refunds;

        lblPaid.setText("Paid: " + money(paid));
        lblRefunds.setText("Refunds: " + money(refunds));
        lblBalance.setText("Balance: " + money(balance));
    }

    private double getCurrentBalance(int invoiceId) {
        double paid = getPaidTotal(invoiceId);
        double refunds = getRefundTotalAbs(invoiceId);
        double balance = totalAmount - paid + refunds;
        return Math.max(0, balance);
    }

    // ================= Export / Print =================

    private void exportTextFromDialog(String text) {
        JFileChooser fc = new JFileChooser();
        String name = (currentInvoiceId != null) ? ("invoice_" + currentInvoiceId + ".txt") : "invoice.txt";
        fc.setSelectedFile(new java.io.File(name));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (java.io.FileWriter w = new java.io.FileWriter(fc.getSelectedFile())) {
            w.write(text);
            JOptionPane.showMessageDialog(this, "Exported successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Export failed: " + e.getMessage());
        }
    }

    private void printText(String text) {
        JTextArea area = new JTextArea(text);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        try {
            boolean done = area.print();
            if (done) JOptionPane.showMessageDialog(this, "Printed successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Print failed: " + e.getMessage());
        }
    }

    private String buildInvoiceText(int invoiceId, int reservationId) {
        StringBuilder sb = new StringBuilder();
        sb.append("HOTEL INVOICE\n");
        sb.append("Invoice ID: ").append(invoiceId).append("\n");
        sb.append("Reservation ID: ").append(reservationId).append("\n");
        sb.append("Created At: ").append(LocalDateTime.now()).append("\n");
        sb.append("--------------------------------------------------\n");
        sb.append("ITEMIZED CHARGES:\n");

        for (int r = 0; r < tableModel.getRowCount(); r++) {
            String desc = String.valueOf(tableModel.getValueAt(r, 0));
            String amt = String.valueOf(tableModel.getValueAt(r, 1));
            sb.append(String.format("- %-28s %12s%n", desc, amt));
        }

        sb.append("--------------------------------------------------\n");
        double paid = getPaidTotal(invoiceId);
        double refunds = getRefundTotalAbs(invoiceId);
        double balance = totalAmount - paid + refunds;

        sb.append("Paid: ").append(money(paid)).append("\n");
        sb.append("Refunds: ").append(money(refunds)).append("\n");
        sb.append("Balance: ").append(money(balance)).append("\n");

        return sb.toString();
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
        String sql =
                "SELECT rt.base_price " +
                        "FROM rooms r " +
                        "JOIN room_types rt ON r.type_id = rt.type_id " +
                        "WHERE r.room_id = ?";

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
            String s = t.trim().replace("$", "").replace(",", "");
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private String money(double v) {
        return String.format("$%.2f", v);
    }
}
