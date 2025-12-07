// Billing / Payments DAO - Work by Aroob

package com.mycompany.hotelreservationsystem.dao;

import com.mycompany.hotelreservationsystem.DatabaseConnection;
import com.mycompany.hotelreservationsystem.model.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    // insert new payment
    public static int insert(Payment payment) {
        String sql = "INSERT INTO payments (invoice_id, amount, method, paid_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, payment.getInvoiceId());
            ps.setDouble(2, payment.getAmount());
            ps.setString(3, payment.getMethod());
            ps.setString(4, payment.getDate()); // أو getPaidAt() حسب اسم الفيلد عندك

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int paymentId = rs.getInt(1);
                    payment.setId(paymentId);
                    return paymentId;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    // get all payments for a specific invoice
    public static List<Payment> getByInvoice(int invoiceId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE invoice_id = ? ORDER BY paid_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, invoiceId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // list all payments (for reports)
    public static List<Payment> getAll() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments ORDER BY paid_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // map DB row to model
    private static Payment mapRow(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getInt("payment_id"));
        payment.setInvoiceId(rs.getInt("invoice_id"));
        payment.setAmount(rs.getDouble("amount"));
        payment.setMethod(rs.getString("method"));
        payment.setDate(rs.getString("paid_at")); // نفس الكلام هنا
        return payment;
    }
}