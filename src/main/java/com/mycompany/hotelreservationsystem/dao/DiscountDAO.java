package com.mycompany.hotelreservationsystem.dao;

import com.mycompany.hotelreservationsystem.DatabaseConnection;
// aroob
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DiscountDAO {

    public double getPercentageIfValid(String code) {

        String sql = "SELECT percentage FROM discounts WHERE code = ? AND active = 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("percentage");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0.0;
    }
}
