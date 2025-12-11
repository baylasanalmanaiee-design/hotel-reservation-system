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
import com.mycompany.hotelreservationsystem.model.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    // الحصول على غرفة بواسطة ID
    public Room getRoomById(int id) {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // الطريقة الآمنة
                Room room = new Room();
                room.setId(rs.getInt("room_id"));
                room.setRoomNumber(Integer.parseInt(rs.getString("room_number")));
                room.setRoomTypeId(rs.getInt("type_id"));
                room.setStatus(rs.getString("status"));
                return room;
            }
        } catch (Exception e) {
            System.out.println("Error in getRoomById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // الحصول على جميع الغرف
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("room_id"));
                room.setRoomNumber(Integer.parseInt(rs.getString("room_number")));
                room.setRoomTypeId(rs.getInt("type_id"));
                room.setStatus(rs.getString("status"));
                rooms.add(room);
            }
        } catch (Exception e) {
            System.out.println("Error in getAllRooms: " + e.getMessage());
            e.printStackTrace();
        }
        return rooms;
    }
    
    // إضافة غرفة جديدة
    public int addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_number, floor, type_id, status) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, String.valueOf(room.getRoomNumber()));
            stmt.setInt(2, 1); // floor - قيمة افتراضية
            stmt.setInt(3, room.getRoomTypeId());
            stmt.setString(4, room.getStatus());
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            
        } catch (Exception e) {
            System.out.println("Error in addRoom: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
    
    // تحديث غرفة
    public boolean updateRoom(Room room) {
        String sql = "UPDATE rooms SET room_number = ?, floor = ?, type_id = ?, status = ? WHERE room_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, String.valueOf(room.getRoomNumber()));
            stmt.setInt(2, 1); // floor
            stmt.setInt(3, room.getRoomTypeId());
            stmt.setString(4, room.getStatus());
            stmt.setInt(5, room.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (Exception e) {
            System.out.println("Error in updateRoom: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // حذف غرفة
    public boolean deleteRoom(int id) {
        String sql = "DELETE FROM rooms WHERE room_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (Exception e) {
            System.out.println("Error in deleteRoom: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // تحديث حالة الغرفة
    public boolean updateRoomStatus(int roomNumber, String status) {
        String sql = "UPDATE rooms SET status = ? WHERE room_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setString(2, String.valueOf(roomNumber));
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (Exception e) {
            System.out.println("Error in updateRoomStatus: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    ///////////
    
    // ✅ جديد: تحديث الحالة بالـ room_id
    public boolean updateRoomStatusById(int roomId, String status) {
        String sql = "UPDATE rooms SET status = ? WHERE room_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, roomId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            System.out.println("Error in updateRoomStatusById: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}

    ///
