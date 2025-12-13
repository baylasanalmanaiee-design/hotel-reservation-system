package com.mycompany.hotelreservationsystem.ui.billing;

import com.mycompany.hotelreservationsystem.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;

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

        String sql = """
            SELECT 
                i.invoice_id,
                i.reservation_id,
                i.total_amount,
                i.created_at,
                IFNULL(SUM(p.amount), 0) AS paid_amount
            FROM invoices i
            LEFT JOIN payments p ON p.invoice_id = i.invoice_id
            GROUP BY i.invoice_id
            ORDER BY i.invoice_id DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int invoiceId = rs.getInt("invoice_id");
                int reservationId = rs.getInt("reservation_id");
                double invoiceAmount = rs.getDouble("total_amount");
                String invoiceDate = rs.getString("created_at");
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
}
