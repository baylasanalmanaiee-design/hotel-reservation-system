/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.dao;

/**
 *
 * @author kady
 */

import com.mycompany.hotelreservationsystem.DatabaseConnection;
import com.mycompany.hotelreservationsystem.model.Reservation;
import java.sql.*;

public class ReservationDAO {

    // ✅ التحقق من توفر نوع غرفة في فترة معينة
  public boolean checkAvailability(int roomTypeId, String checkIn, String checkOut) {

    String sql = """
        SELECT * FROM reservations r
        JOIN rooms rm ON r.room_id = rm.room_id
        WHERE rm.type_id = ?
          AND r.status <> 'Cancelled'
          AND NOT (
                r.check_out_date <= ?   -- new check-in
             OR r.check_in_date >= ?   -- new check-out
          )
    """;

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, roomTypeId);
        stmt.setString(2, checkIn);   // <= check_in
        stmt.setString(3, checkOut);  // >= check_out

        ResultSet rs = stmt.executeQuery();

        // إذا رجع نتائج → فيه تعارض
        return !rs.next();

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}


    // ✅ إنشاء حجز جديد مع status = CONFIRMED و total_price
    public boolean createReservation(Reservation r) {

        String sql = """
            INSERT INTO reservations
                (guest_id, room_id, check_in_date, check_out_date, status, total_price)
            VALUES(?, ?, ?, ?, 'CONFIRMED', ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, r.getGuestId());
            stmt.setInt(2, r.getRoomId());
            stmt.setString(3, r.getCheckInDate());
            stmt.setString(4, r.getCheckOutDate());
            stmt.setDouble(5, r.getTotalPrice());

            stmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ✅ جلب حجز بالـ ID
    public Reservation getReservationById(int id) {

        String sql = "SELECT * FROM reservations WHERE reservation_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Reservation(
                        rs.getInt("reservation_id"),
                        rs.getInt("guest_id"),
                        rs.getInt("room_id"),
                        rs.getString("check_in_date"),
                        rs.getString("check_out_date"),
                        rs.getDouble("total_price")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ✅ تعديل كامل على الحجز (غرفة + تواريخ + سعر)
    public boolean updateReservation(int id, int roomId, String in, String out, double totalPrice) {

        String sql = """
            UPDATE reservations
            SET room_id = ?,
                check_in_date = ?,
                check_out_date = ?,
                total_price = ?
            WHERE reservation_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            stmt.setString(2, in);
            stmt.setString(3, out);
            stmt.setDouble(4, totalPrice);
            stmt.setInt(5, id);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ تحديث حالة الحجز (CONFIRMED / CHECKED_IN / CHECKED_OUT / CANCELLED)
    public boolean updateStatus(int reservationId, String status) {
        String sql = "UPDATE reservations SET status=? WHERE reservation_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, reservationId);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}



    
    
    
    

