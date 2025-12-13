package com.mycompany.hotelreservationsystem.dao;

import com.mycompany.hotelreservationsystem.DatabaseConnection;
import com.mycompany.hotelreservationsystem.model.Invoice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InvoiceDAO {

    public static Invoice getByReservationId(int reservationId) {
        String sql = """
            SELECT invoice_id, reservation_id, subtotal, discount_amount, tax_amount, total_amount, created_at
            FROM invoices
            WHERE reservation_id = ?
            ORDER BY invoice_id DESC
            LIMIT 1
        """;

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, reservationId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Invoice inv = new Invoice();
                    inv.setId(rs.getInt("invoice_id"));
                    inv.setReservationId(rs.getInt("reservation_id"));
                    inv.setSubtotal(rs.getDouble("subtotal"));
                    inv.setDiscountAmount(rs.getDouble("discount_amount"));
                    inv.setTaxAmount(rs.getDouble("tax_amount"));
                    inv.setTotalAmount(rs.getDouble("total_amount"));
                    inv.setCreatedAt(rs.getString("created_at"));
                    return inv;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
