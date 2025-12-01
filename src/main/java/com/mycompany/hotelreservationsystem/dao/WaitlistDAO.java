/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.dao;

/**
 *
 * @author Bilsan
 */

import com.mycompany.hotelreservationsystem.DatabaseConnection;
import com.mycompany.hotelreservationsystem.model.Waitlist;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WaitlistDAO {

    // يضيف سجل إلى لائحة الانتظار ويعيد الـ generated id أو -1 إذا فشل
    public int addToWaitlist(Waitlist w) {
        String sql = "INSERT INTO waitlist(guest_id, room_type_id, check_in, check_out, added_at) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, w.getGuestId());
            stmt.setInt(2, w.getRoomTypeId());
            stmt.setString(3, w.getCheckIn());
            stmt.setString(4, w.getCheckOut());
            stmt.setString(5, w.getAddedAt());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // يجلب جميع السجلات في لائحة الانتظار
    public List<Waitlist> getAllWaitlist() {
    List<Waitlist> list = new ArrayList<>();

    String sql = "SELECT * FROM waitlist ORDER BY added_at ASC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            Waitlist w = new Waitlist(
                    rs.getInt("id"),
                    rs.getInt("guest_id"),
                    rs.getInt("room_type_id"),
                    rs.getString("check_in"),
                    rs.getString("check_out"),
                    rs.getString("added_at")
            );
            list.add(w);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}

    // يزيل سجل من القائمة بعد معالجته أو إلغائه
    public boolean removeFromWaitlist(int id) {
        String sql = "DELETE FROM waitlist WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            return affected > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // جلب أول شخص في الانتظار لنوع غرفة معين (لـ "promote" عندما تتوفر غرفة)
    public Waitlist getFirstByRoomType(int roomTypeId) {
        String sql = "SELECT * FROM waitlist WHERE room_type_id = ? ORDER BY added_at ASC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomTypeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Waitlist(
                        rs.getInt("id"),
                        rs.getInt("guest_id"),
                        rs.getInt("room_type_id"),
                        rs.getString("check_in"),
                        rs.getString("check_out"),
                        rs.getString("added_at")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
