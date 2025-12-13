package com.mycompany.hotelreservationsystem.dao;

import com.mycompany.hotelreservationsystem.DatabaseConnection;
import com.mycompany.hotelreservationsystem.model.Discount;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DiscountDAO {

    // ===== CRUD for Manager Screen =====

    public List<Discount> getAll() {
        List<Discount> list = new ArrayList<>();
        String sql = """
            SELECT discount_id, code, description, percentage, active
            FROM discounts
            ORDER BY discount_id DESC
        """;

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Discount d = new Discount();
                d.setId(rs.getInt("discount_id"));
                d.setCode(rs.getString("code"));
                d.setDescription(rs.getString("description"));
                d.setPercentage(rs.getDouble("percentage"));
                d.setActive(rs.getInt("active") == 1);

                // إذا جدولك ما فيه start/end، نخليها null
                d.setStartDate(null);
                d.setEndDate(null);

                list.add(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Discount d) {
        String sql = """
            INSERT INTO discounts (code, description, percentage, active)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, d.getCode());
            ps.setString(2, d.getDescription());
            ps.setDouble(3, d.getPercentage());
            ps.setInt(4, d.isActive() ? 1 : 0);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Discount d) {
        String sql = """
            UPDATE discounts
            SET code=?, description=?, percentage=?, active=?
            WHERE discount_id=?
        """;

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, d.getCode());
            ps.setString(2, d.getDescription());
            ps.setDouble(3, d.getPercentage());
            ps.setInt(4, d.isActive() ? 1 : 0);
            ps.setInt(5, d.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM discounts WHERE discount_id=?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== Used in CheckOutScreen: validate code and return percentage =====
    // يدعم الخصم النشط فقط. وبما إن جدولك الحالي ما فيه start/end فالموسمي لاحقاً.
    public double getPercentageIfValid(String code) {
        String sql = """
            SELECT percentage
            FROM discounts
            WHERE UPPER(code) = UPPER(?) AND active = 1
            LIMIT 1
        """;

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, code);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("percentage");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
