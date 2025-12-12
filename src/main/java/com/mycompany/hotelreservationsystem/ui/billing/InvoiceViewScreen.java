package com.mycompany.hotelreservationsystem.ui.billing;

import com.mycompany.hotelreservationsystem.dao.InvoiceDAO;
import com.mycompany.hotelreservationsystem.dao.PaymentDAO;
import com.mycompany.hotelreservationsystem.model.Invoice;
import com.mycompany.hotelreservationsystem.model.Payment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InvoiceViewScreen extends JDialog {

    private JLabel lblInvoiceId;
    private JLabel lblReservation;
    private JLabel lblDate;
    private JLabel lblTotal;
    private JLabel lblPaid;
    private JLabel lblBalance;

    private JTable paymentsTable;
    private DefaultTableModel paymentsModel;

    private JTextField txtPayAmount;
    private JComboBox<String> cmbPayMethod;
    private JButton btnAddPayment;
    private JButton btnClose;

    private Invoice invoice;

    public InvoiceViewScreen(JFrame parent, String reservationCode) {
        super(parent, "Invoice View", true);
        setSize(700, 500);
        setLocationRelativeTo(parent);
        initUI();
        loadInvoiceAndPayments(reservationCode);
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel header = new JPanel(new GridLayout(3, 2, 10, 8));
        header.setBorder(BorderFactory.createTitledBorder("Invoice Info"));

        lblInvoiceId = new JLabel("-");
        lblReservation = new JLabel("-");
        lblDate = new JLabel("-");
        lblTotal = new JLabel("-");
        lblPaid = new JLabel("-");
        lblBalance = new JLabel("-");

        header.add(new JLabel("Invoice ID:"));
        header.add(lblInvoiceId);
        header.add(new JLabel("Reservation:"));
        header.add(lblReservation);
        header.add(new JLabel("Created At:"));
        header.add(lblDate);

        JPanel totals = new JPanel(new GridLayout(1, 6, 10, 8));
        totals.setBorder(BorderFactory.createTitledBorder("Totals"));

        totals.add(new JLabel("Total:"));
        totals.add(lblTotal);
        totals.add(new JLabel("Paid:"));
        totals.add(lblPaid);
        totals.add(new JLabel("Balance:"));
        totals.add(lblBalance);

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.add(header, BorderLayout.CENTER);
        top.add(totals, BorderLayout.SOUTH);

        paymentsModel = new DefaultTableModel(new String[]{"Payment ID", "Amount", "Method", "Paid At"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        paymentsTable = new JTable(paymentsModel);
        JScrollPane scroll = new JScrollPane(paymentsTable);
        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createTitledBorder("Payments"));
        center.add(scroll, BorderLayout.CENTER);

        JPanel payPanel = new JPanel(new GridLayout(1, 6, 10, 8));
        payPanel.setBorder(BorderFactory.createTitledBorder("Add Payment"));

        txtPayAmount = new JTextField();
        cmbPayMethod = new JComboBox<>(new String[]{"CASH", "CARD", "TRANSFER"});
        btnAddPayment = new JButton("Add Payment");
        btnClose = new JButton("Close");

        payPanel.add(new JLabel("Amount:"));
        payPanel.add(txtPayAmount);
        payPanel.add(new JLabel("Method:"));
        payPanel.add(cmbPayMethod);
        payPanel.add(btnAddPayment);
        payPanel.add(btnClose);

        btnAddPayment.addActionListener(e -> addPayment());
        btnClose.addActionListener(e -> dispose());

        main.add(top, BorderLayout.NORTH);
        main.add(center, BorderLayout.CENTER);
        main.add(payPanel, BorderLayout.SOUTH);

        add(main);
    }

    private int parseReservationId(String reservationCode) {
        if (reservationCode == null) return 0;
        String code = reservationCode.trim();
        if (code.startsWith("RES")) {
            String num = code.substring(3).replaceAll("\\D+", "");
            if (num.isEmpty()) return 0;
            try {
                return Integer.parseInt(num);
            } catch (NumberFormatException ignored) {
                return 0;
            }
        }
        String digits = code.replaceAll("\\D+", "");
        if (digits.isEmpty()) return 0;
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private void loadInvoiceAndPayments(String reservationCode) {
        int reservationId = parseReservationId(reservationCode);

        invoice = InvoiceDAO.getByReservationId(reservationId);

        if (invoice == null) {
            lblInvoiceId.setText("-");
            lblReservation.setText(reservationCode == null ? "-" : reservationCode);
            lblDate.setText("-");
            lblTotal.setText("$0.00");
            lblPaid.setText("$0.00");
            lblBalance.setText("$0.00");
            paymentsModel.setRowCount(0);
            JOptionPane.showMessageDialog(this, "No invoice found for this reservation.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        lblInvoiceId.setText(String.valueOf(invoice.getId()));
        lblReservation.setText("RES" + String.format("%03d", invoice.getReservationId()));
        lblDate.setText(invoice.getDate());

        refreshPayments();
    }

    private void refreshPayments() {
        paymentsModel.setRowCount(0);

        double total = invoice.getAmount();
        double paid = 0.0;

        List<Payment> payments = PaymentDAO.getByInvoice(invoice.getId());
        for (Payment p : payments) {
            paid += p.getAmount();
            paymentsModel.addRow(new Object[]{
                    p.getId(),
                    String.format("$%.2f", p.getAmount()),
                    p.getMethod(),
                    p.getDate()
            });
        }

        double balance = total - paid;
        if (balance < 0) balance = 0.0;

        lblTotal.setText(String.format("$%.2f", total));
        lblPaid.setText(String.format("$%.2f", paid));
        lblBalance.setText(String.format("$%.2f", balance));
    }

    private void addPayment() {
        if (invoice == null) {
            JOptionPane.showMessageDialog(this, "No invoice loaded.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String amtText = txtPayAmount.getText().trim();
        if (amtText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter payment amount.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double amt;
        try {
            amt = Double.parseDouble(amtText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Amount must be numeric.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (amt <= 0) {
            JOptionPane.showMessageDialog(this, "Amount must be greater than 0.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Payment p = new Payment();
        p.setInvoiceId(invoice.getId());
        p.setAmount(amt);
        p.setMethod(String.valueOf(cmbPayMethod.getSelectedItem()));
        p.setDate(java.time.LocalDateTime.now().toString());

        int newId = PaymentDAO.insert(p);
        if (newId > 0) {
            txtPayAmount.setText("");
            refreshPayments();
            JOptionPane.showMessageDialog(this, "Payment saved. ID: " + newId, "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save payment.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
