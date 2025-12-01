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
   
   public boolean checkAvailability(int roomTypeId, String in, String out) {

    String sql = """
        SELECT * FROM reservations r
        JOIN rooms rm ON r.room_id = rm.room_id
        WHERE rm.type_id = ?
        AND (
            (check_in_date <= ? AND check_out_date >= ?)
         OR (check_in_date <= ? AND check_out_date >= ?)
        )
        """;

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, roomTypeId);
        stmt.setString(2, out);
        stmt.setString(3, in);
        stmt.setString(4, out);
        stmt.setString(5, in);

        ResultSet rs = stmt.executeQuery();
        return !rs.next(); // لو ما فيه نتيجة → ما فيه تعارض

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}

   
   public boolean createReservation(Reservation r) {

    String sql = """
        INSERT INTO reservations(guest_id, room_id, check_in_date, check_out_date, status)
        VALUES(?, ?, ?, ?, 'active')
    """;

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, r.getGuestId());
        stmt.setInt(2, r.getRoomId());
        stmt.setString(3, r.getCheckInDate());
        stmt.setString(4, r.getCheckOutDate());

        stmt.executeUpdate();
        return true;

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}
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
                    0.0  // تسعير الحجز لاحقًا
            );
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
}

}

    
    
    
    

