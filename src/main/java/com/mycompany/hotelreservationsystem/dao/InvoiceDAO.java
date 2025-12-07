// Billing / Invoices DAO - Work by Aroob

package com.mycompany.hotelreservationsystem.dao;

import com.mycompany.hotelreservationsystem.DatabaseConnection;
import com.mycompany.hotelreservationsystem.model.Invoice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    // insert new invoice, returns generated id or -1
    public static int insert(Invoice invoice) {
        String sql = "INSERT INTO invoices " +
                     "(reservation_id, subtotal, discount_amount, tax_amount, total_amount, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, invoice.getReservationId());

            // we only have one amount in model, use it as subtotal & total for now
            ps.setDouble(2, invoice.getAmount()); // subtotal
            ps.setDouble(3, 0.0);                 // discount_amount
            ps.setDouble(4, 0.0);                 // tax_amount
            ps.setDouble(5, invoice.getAmount()); // total_amount

            ps.setString(6, invoice.getDate());   // created_at (text)

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    invoice.setId(id);
                    return id;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    // get invoice by primary key
    public static Invoice getById(int invoiceId) {
        String sql = "SELECT * FROM invoices WHERE invoice_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, invoiceId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // get invoice for specific reservation (used in billing)
    public static Invoice getByReservationId(int reservationId) {
        String sql = "SELECT * FROM invoices WHERE reservation_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reservationId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // list all invoices (for reports)
    public static List<Invoice> getAll() {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT * FROM invoices ORDER BY created_at DESC";

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

    // map DB row to Invoice model
    private static Invoice mapRow(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setId(rs.getInt("invoice_id"));
        invoice.setReservationId(rs.getInt("reservation_id"));
        invoice.setAmount(rs.getDouble("total_amount"));
        invoice.setDate(rs.getString("created_at"));
        return invoice;
    }
}
