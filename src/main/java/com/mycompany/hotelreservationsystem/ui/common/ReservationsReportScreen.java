package com.mycompany.hotelreservationsystem.ui.common;

import com.mycompany.hotelreservationsystem.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ReservationsReportScreen extends JDialog {

    private JTextField txtFrom, txtTo;
    private JButton btnToday, btnThisWeek, btnRefresh, btnExportTxt, btnClose;

    private DefaultTableModel model;
    private JTable table;

    private JTextArea txtNotes;

    public ReservationsReportScreen(JFrame parent) {
        super(parent, "Reservations Report (Analytics)", true);
        setSize(920, 650);
        setLocationRelativeTo(parent);
        initUI();
        setDefaultRangeThisWeek();
        loadReport();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(main);

        // ===== Top Filters =====
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filters.setBorder(BorderFactory.createTitledBorder("Report Range (YYYY-MM-DD)"));

        txtFrom = new JTextField(10);
        txtTo = new JTextField(10);

        btnToday = new JButton("Today");
        btnThisWeek = new JButton("This Week");
        btnRefresh = new JButton("Refresh");

        filters.add(new JLabel("From:"));
        filters.add(txtFrom);
        filters.add(new JLabel("To:"));
        filters.add(txtTo);
        filters.add(btnToday);
        filters.add(btnThisWeek);
        filters.add(btnRefresh);

        btnToday.addActionListener(e -> {
            LocalDate d = LocalDate.now();
            txtFrom.setText(d.toString());
            txtTo.setText(d.toString());
            loadReport();
        });

        btnThisWeek.addActionListener(e -> {
            setDefaultRangeThisWeek();
            loadReport();
        });

        btnRefresh.addActionListener(e -> loadReport());

        main.add(filters, BorderLayout.NORTH);

        // ===== Center Table =====
        model = new DefaultTableModel(new String[]{"Metric", "Value"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(26);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder("Reservations Analytics"));
        main.add(sp, BorderLayout.CENTER);

        // ===== Bottom Notes + Buttons =====
        JPanel bottom = new JPanel(new BorderLayout(10, 10));

        txtNotes = new JTextArea(5, 40);
        txtNotes.setEditable(false);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);

        JScrollPane notesScroll = new JScrollPane(txtNotes);
        notesScroll.setBorder(BorderFactory.createTitledBorder("Notes"));
        bottom.add(notesScroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnExportTxt = new JButton("Export TXT");
        btnClose = new JButton("Close");

        btnExportTxt.addActionListener(e -> exportTxt());
        btnClose.addActionListener(e -> dispose());

        actions.add(btnExportTxt);
        actions.add(btnClose);

        bottom.add(actions, BorderLayout.SOUTH);

        main.add(bottom, BorderLayout.SOUTH);
    }

    private void setDefaultRangeThisWeek() {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(today.getDayOfWeek().getValue() - 1L);
        LocalDate end = start.plusDays(6);
        txtFrom.setText(start.toString());
        txtTo.setText(end.toString());
    }

    private void loadReport() {
        model.setRowCount(0);
        txtNotes.setText("");

        LocalDate from = parseDate(txtFrom.getText());
        LocalDate to = parseDate(txtTo.getText());

        if (from == null || to == null) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
            return;
        }
        if (to.isBefore(from)) {
            JOptionPane.showMessageDialog(this, "To date must be >= From date.");
            return;
        }

        LocalDate endExclusive = to.plusDays(1);

        int totalRooms = getInt("SELECT COUNT(*) FROM rooms");
        int occupiedDistinctRooms = getOccupiedDistinctRooms(from, endExclusive);
        double occupancyRate = (totalRooms <= 0) ? 0 : (occupiedDistinctRooms * 100.0 / totalRooms);

        AdrResult adr = computeADR(from, endExclusive);
        RevenueResult revenue = computeRevenue(from, endExclusive);

        int cancellations = getInt(
                "SELECT COUNT(*) FROM reservations " +
                        "WHERE status='CANCELLED' " +
                        "AND date(check_in_date) >= date(?) AND date(check_in_date) < date(?)",
                from.toString(), endExclusive.toString()
        );

        int noShows = getInt(
                "SELECT COUNT(*) FROM reservations " +
                        "WHERE date(check_in_date) >= date(?) AND date(check_in_date) < date(?) " +
                        "AND status NOT IN ('CHECKED_IN','CHECKED_OUT','CANCELLED') " +
                        "AND date(check_in_date) < date('now')",
                from.toString(), endExclusive.toString()
        );

        addMetric("Report Range", from + " → " + to);

        addMetric("Total Rooms", String.valueOf(totalRooms));
        addMetric("Occupied Rooms (distinct overlap)", String.valueOf(occupiedDistinctRooms));
        addMetric("Occupancy Rate", String.format("%.2f%%", occupancyRate));

        addMetric("Total Room Nights (in range)", String.valueOf(adr.totalNights));
        addMetric("Room Revenue (from reservations.total_price)", money(adr.totalRoomRevenue));
        addMetric("ADR (Avg Daily Rate)", (adr.totalNights > 0) ? money(adr.adr) : "$0.00");

        addMetric("Invoices Count (created in range)", String.valueOf(revenue.invoiceCount));
        addMetric("Invoices Total (sum invoices.total_amount)", money(revenue.invoicesTotal));
        addMetric("Payments Received (sum payments.amount > 0)", money(revenue.paymentsPositive));
        addMetric("Refunds (abs of payments.amount < 0)", money(revenue.refundsAbs));
        addMetric("Net Cash Flow (payments - refunds)", money(revenue.netCash));
        addMetric("Outstanding Balance (invoices - net cash)", money(revenue.outstanding));

        addMetric("Cancellations", String.valueOf(cancellations));
        addMetric("No-Show (approx)", String.valueOf(noShows));

        txtNotes.setText(
                "Occupancy: distinct rooms having reservations overlapping the selected range.\n" +
                "ADR: total room revenue / total room nights within range.\n" +
                "Revenue: invoices.total_amount and payments (positive=payments, negative=refunds).\n" +
                "No-Show: approximated as reservations whose check-in date passed without CHECKED_IN/CHECKED_OUT/CANCELLED.\n"
        );
    }

    private void addMetric(String metric, String value) {
        model.addRow(new Object[]{metric, value});
    }

    // ================== Queries ==================

    private int getOccupiedDistinctRooms(LocalDate from, LocalDate endExclusive) {
        String sql =
                "SELECT COUNT(DISTINCT room_id) " +
                        "FROM reservations " +
                        "WHERE status <> 'CANCELLED' " +
                        "AND date(check_in_date) < date(?) " +
                        "AND date(check_out_date) > date(?)";
        return getInt(sql, endExclusive.toString(), from.toString());
    }

    private AdrResult computeADR(LocalDate from, LocalDate endExclusive) {
        String sql =
                "SELECT check_in_date, check_out_date, total_price " +
                        "FROM reservations " +
                        "WHERE status <> 'CANCELLED' " +
                        "AND date(check_in_date) < date(?) " +
                        "AND date(check_out_date) > date(?)";

        double revenue = 0;
        long nightsSum = 0;

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, endExclusive.toString());
            ps.setString(2, from.toString());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate in = safeDate(rs.getString("check_in_date"));
                    LocalDate out = safeDate(rs.getString("check_out_date"));
                    if (in == null || out == null) continue;

                    LocalDate start = in.isBefore(from) ? from : in;
                    LocalDate end = out.isAfter(endExclusive) ? endExclusive : out;

                    long nights = ChronoUnit.DAYS.between(start, end);
                    if (nights <= 0) continue;

                    double totalPrice = rs.getDouble("total_price");
                    long totalNightsReservation = Math.max(1, ChronoUnit.DAYS.between(in, out));

                    double portion = totalPrice * (nights * 1.0 / totalNightsReservation);

                    nightsSum += nights;
                    revenue += portion;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        double adr = (nightsSum > 0) ? (revenue / nightsSum) : 0;
        return new AdrResult(nightsSum, revenue, adr);
    }

    private RevenueResult computeRevenue(LocalDate from, LocalDate endExclusive) {
        String invSql =
                "SELECT COUNT(*) AS cnt, COALESCE(SUM(total_amount),0) AS s " +
                        "FROM invoices " +
                        "WHERE date(created_at) >= date(?) AND date(created_at) < date(?)";

        int invCount = 0;
        double invTotal = 0;

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(invSql)) {

            ps.setString(1, from.toString());
            ps.setString(2, endExclusive.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    invCount = rs.getInt("cnt");
                    invTotal = rs.getDouble("s");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String paySql =
                "SELECT " +
                        "COALESCE(SUM(CASE WHEN p.amount > 0 THEN p.amount ELSE 0 END),0) AS paid_pos, " +
                        "COALESCE(SUM(CASE WHEN p.amount < 0 THEN ABS(p.amount) ELSE 0 END),0) AS refunds_abs " +
                        "FROM payments p " +
                        "JOIN invoices i ON p.invoice_id = i.invoice_id " +
                        "WHERE date(i.created_at) >= date(?) AND date(i.created_at) < date(?)";

        double paidPos = 0;
        double refundsAbs = 0;

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(paySql)) {

            ps.setString(1, from.toString());
            ps.setString(2, endExclusive.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    paidPos = rs.getDouble("paid_pos");
                    refundsAbs = rs.getDouble("refunds_abs");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        double netCash = paidPos - refundsAbs;
        double outstanding = invTotal - netCash;

        return new RevenueResult(invCount, invTotal, paidPos, refundsAbs, netCash, outstanding);
    }

    // ================== Export ==================

    private void exportTxt() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("reservations_report.txt"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (FileWriter w = new FileWriter(fc.getSelectedFile())) {
            w.write("RESERVATIONS REPORT\n");
            w.write("Range: " + txtFrom.getText() + " -> " + txtTo.getText() + "\n");
            w.write("====================================\n");
            for (int r = 0; r < model.getRowCount(); r++) {
                w.write(model.getValueAt(r, 0) + " : " + model.getValueAt(r, 1) + "\n");
            }
            w.write("\nNOTES:\n" + txtNotes.getText());
            JOptionPane.showMessageDialog(this, "Exported successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Export failed: " + e.getMessage());
        }
    }

    // ================== Helpers ==================

    private LocalDate parseDate(String s) {
        try { return LocalDate.parse(s.trim()); } catch (Exception e) { return null; }
    }

    private LocalDate safeDate(String s) {
        try {
            String t = s.trim();
            if (t.length() >= 10) t = t.substring(0, 10);
            return LocalDate.parse(t);
        } catch (Exception e) {
            return null;
        }
    }

    private int getInt(String sql, String... params) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) ps.setString(i + 1, params[i]);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String money(double v) {
        return String.format("$%.2f", v);
    }

    // ================== Small structs ==================

    private static class AdrResult {
        long totalNights;
        double totalRoomRevenue;
        double adr;
        AdrResult(long n, double rev, double adr) {
            this.totalNights = n;
            this.totalRoomRevenue = rev;
            this.adr = adr;
        }
    }

    private static class RevenueResult {
        int invoiceCount;
        double invoicesTotal;
        double paymentsPositive;
        double refundsAbs;
        double netCash;
        double outstanding;

        RevenueResult(int invoiceCount, double invoicesTotal, double paymentsPositive,
                      double refundsAbs, double netCash, double outstanding) {
            this.invoiceCount = invoiceCount;
            this.invoicesTotal = invoicesTotal;
            this.paymentsPositive = paymentsPositive;
            this.refundsAbs = refundsAbs;
            this.netCash = netCash;
            this.outstanding = outstanding;
        }
    }
}
