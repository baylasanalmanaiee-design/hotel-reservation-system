package com.mycompany.hotelreservationsystem.dao;

import com.mycompany.hotelreservationsystem.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;

public class ActivityLogDAO {

    /**
     * Log a user activity
     * @param userId ID of the user who performed the action
     * @param action Description of the action
     */
    public static void log(int userId, String action) {
        String sql =
                "INSERT INTO activity_logs (user_id, action, action_time) " +
                "VALUES (?, ?, ?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.setString(3, LocalDateTime.now().toString());

            ps.executeUpdate();

        } catch (Exception e) {
            // logging should NEVER crash the system
            e.printStackTrace();
        }
    }

    /**
     * Optional: get last activity for a user (for debugging / report)
     */
    public static String getLastActivityForUser(int userId) {
        String sql =
                "SELECT action || ' at ' || action_time AS info " +
                "FROM activity_logs " +
                "WHERE user_id = ? " +
                "ORDER BY log_id DESC " +
                "LIMIT 1";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("info");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Optional: count logs (useful for reports)
     */
    public static int countLogs() {
        String sql = "SELECT COUNT(*) FROM activity_logs";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
