package com.mycompany.hotelreservationsystem.ui.billing;

import com.mycompany.hotelreservationsystem.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class IncomeReportScreen extends JDialog {

    private JTable table;
    private DefaultTableModel model;

    private JLabel lblTotalInvoices;
    private JLabel lblTotalPayments;
    private JLabel lblBalance;

    public IncomeReportScreen(JFrame parent) {
        super(parent, "Income Report", true);
        setSize(900, 500);
        setLocationRelativeTo(parent);
        applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 10));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        lblTotalInvoices = new JLabel("Total Invoices: 0.00");
        lblTotalPayments = new JLabel("Total Payments: 0.00");
        lblBalance = new JLabel("Balance: 0.00");

        Font f = new Font("Arial", Font.BOLD, 14);
        lblTotalInvoices.setFont(f);
        lblTotalPayments.setFont(f);
        lblBalance.setFont(f);

        summaryPanel.add(lblTotalInvoices);
        summaryPanel.add(lblTotalPayments);
        summaryPanel.add(lblBalance);

        add(summaryPanel, BorderLayout.NORTH);

        String[] cols = {"Invoice ID", "Reservation ID", "Invoice Amount", "Invoice Date", "Paid Amount"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("Refresh");
        JButton btnClose = new JButton("Close");

        btnRefresh.addActionListener(e -> loadData());
        btnClose.addActionListener(e -> dispose());

        bottom.add(btnRefresh);
        bottom.add(btnClose);

        add(bottom, BorderLayout.SOUTH);
    }

    private void loadData() {
        model.setRowCount(0);

        double totalInvoices = 0.0;
        double totalPayments = 0.0;

        try (Connection conn = DatabaseConnection.getConnection()) {

            String invIdCol = pickFirstExisting(conn, "invoices", new String[]{"invoice_id", "id"});
            String invResCol = pickFirstExisting(conn, "invoices", new String[]{"reservation_id", "res_id"});
            String invAmountCol = pickFirstExisting(conn, "invoices", new String[]{"amount", "total_amount", "total", "invoice_amount"});
            String invDateCol = pickFirstExisting(conn, "invoices", new String[]{"date", "invoice_date", "created_at", "issue_date"});

            if (invIdCol == null || invResCol == null || invAmountCol == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Income Report Error: invoices table columns not compatible.\n" +
                                "Need: invoice_id/id, reservation_id, amount/total_amount/total.",
                        "SQL Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            String payAmountCol = pickFirstExisting(conn, "payments", new String[]{"amount", "paid_amount", "payment_amount"});
            String payInvoiceCol = pickFirstExisting(conn, "payments", new String[]{"invoice_id", "inv_id"});
            String payResCol = pickFirstExisting(conn, "payments", new String[]{"reservation_id", "res_id"});

            String joinOn;
            if (payAmountCol != null && payInvoiceCol != null) {
                joinOn = "p." + payInvoiceCol + " = i." + invIdCol;
            } else if (payAmountCol != null && payResCol != null) {
                joinOn = "p." + payResCol + " = i." + invResCol;
            } else {
                joinOn = null;
            }

            String dateSelect = (invDateCol != null) ? ("i." + invDateCol + " AS invoice_date") : ("'' AS invoice_date");

            String paidSelect = (joinOn != null)
                    ? ("IFNULL(SUM(p." + payAmountCol + "), 0) AS paid_amount")
                    : ("0 AS paid_amount");

            String fromJoin = (joinOn != null)
                    ? ("FROM invoices i LEFT JOIN payments p ON " + joinOn)
                    : ("FROM invoices i");

            String groupBy = "GROUP BY i." + invIdCol;
            String orderBy = "ORDER BY i." + invIdCol + " DESC";

            String sql = "SELECT " +
                    "i." + invIdCol + " AS invoice_id, " +
                    "i." + invResCol + " AS reservation_id, " +
                    "i." + invAmountCol + " AS invoice_amount, " +
                    dateSelect + ", " +
                    paidSelect + " " +
                    fromJoin + " " +
                    groupBy + " " +
                    orderBy;

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    int invoiceId = rs.getInt("invoice_id");
                    int reservationId = rs.getInt("reservation_id");
                    double invoiceAmount = rs.getDouble("invoice_amount");
                    String invoiceDate = rs.getString("invoice_date");
                    double paidAmount = rs.getDouble("paid_amount");

                    totalInvoices += invoiceAmount;
                    totalPayments += paidAmount;

                    model.addRow(new Object[]{
                            invoiceId,
                            reservationId,
                            money(invoiceAmount),
                            invoiceDate,
                            money(paidAmount)
                    });
                }
            }

            lblTotalInvoices.setText("Total Invoices: " + money(totalInvoices));
            lblTotalPayments.setText("Total Payments: " + money(totalPayments));
            lblBalance.setText("Balance: " + money(totalInvoices - totalPayments));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading income report:\n" + e.getMessage(),
                    "SQL Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String money(double v) {
        return String.format(Locale.US, "%.2f", v);
    }

    private String pickFirstExisting(Connection conn, String tableName, String[] candidates) {
        Set<String> cols = new HashSet<>();
        try (PreparedStatement ps = conn.prepareStatement("PRAGMA table_info(" + tableName + ")");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("name");
                if (name != null) cols.add(name.trim().toLowerCase());
            }
        } catch (Exception e) {
            return null;
        }

        for (String c : candidates) {
            if (cols.contains(c.toLowerCase())) return c;
        }
        return null;
    }
}
