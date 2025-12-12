package com.mycompany.hotelreservationsystem.ui.billing;

import com.mycompany.hotelreservationsystem.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class IncomeReportScreen extends JDialog {

    private JTextField txtFrom;
    private JTextField txtTo;
    private JButton btnGenerate;
    private JLabel lblTotal;
    private JTable table;
    private DefaultTableModel model;

    public IncomeReportScreen(JFrame parent) {
        super(parent, "Income Report", true);
        setSize(850, 520);
        setLocationRelativeTo(parent);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        main.add(createTopPanel(), BorderLayout.NORTH);
        main.add(createTablePanel(), BorderLayout.CENTER);
        main.add(createBottomPanel(), BorderLayout.SOUTH);

        add(main);

        txtFrom.setText(java.time.LocalDate.now().withDayOfMonth(1).toString());
        txtTo.setText(java.time.LocalDate.now().toString());

        generateReport();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 7, 10, 10));

        panel.add(new JLabel("From (YYYY-MM-DD):"));
        txtFrom = new JTextField();
        panel.add(txtFrom);

        panel.add(new JLabel("To (YYYY-MM-DD):"));
        txtTo = new JTextField();
        panel.add(txtTo);

        btnGenerate = new JButton("Generate");
        btnGenerate.addActionListener(e -> generateReport());
        panel.add(btnGenerate);

        panel.add(new JLabel(""));

        return panel;
    }

    private JScrollPane createTablePanel() {
        String[] cols = {"Invoice ID", "Reservation ID", "Date", "Amount"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        return new JScrollPane(table);
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Total: $0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblTotal);
        return panel;
    }

    private void generateReport() {
        model.setRowCount(0);

        String from = txtFrom.getText().trim();
        String to = txtTo.getText().trim();

        if (from.isEmpty() || to.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter From and To dates.");
            return;
        }

        String sql = """
            SELECT invoice_id, reservation_id, date, amount
            FROM invoices
            WHERE substr(replace(date,'T',' '), 1, 10) >= ?
              AND substr(replace(date,'T',' '), 1, 10) <= ?
            ORDER BY date DESC
        """;

        double total = 0.0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, from);
            stmt.setString(2, to);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int invoiceId = rs.getInt("invoice_id");
                    int reservationId = rs.getInt("reservation_id");
                    String date = rs.getString("date");
                    double amount = rs.getDouble("amount");

                    total += amount;

                    model.addRow(new Object[]{
                            invoiceId,
                            reservationId,
                            date,
                            String.format("$%.2f", amount)
                    });
                }
            }

            lblTotal.setText(String.format("Total: $%.2f", total));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
