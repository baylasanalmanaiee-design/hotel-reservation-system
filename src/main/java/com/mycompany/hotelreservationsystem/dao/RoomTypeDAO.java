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
import com.mycompany.hotelreservationsystem.model.RoomType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomTypeDAO {

    // الحصول على نوع غرفة بواسطة ID
    public RoomType getRoomTypeById(int id) {
        String sql = "SELECT * FROM room_types WHERE type_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // الطريقة الآمنة: Constructor فارغ + Setters
                RoomType roomType = new RoomType();
                roomType.setId(rs.getInt("type_id"));
                roomType.setName(rs.getString("name"));
                roomType.setBasePrice(rs.getDouble("base_price"));
                return roomType;
            }
        } catch (Exception e) {
            System.out.println("Error in getRoomTypeById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // الحصول على جميع أنواع الغرف
    public List<RoomType> getAllRoomTypes() {
        List<RoomType> roomTypes = new ArrayList<>();
        String sql = "SELECT * FROM room_types";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                RoomType roomType = new RoomType();
                roomType.setId(rs.getInt("type_id"));
                roomType.setName(rs.getString("name"));
                roomType.setBasePrice(rs.getDouble("base_price"));
                roomTypes.add(roomType);
            }
        } catch (Exception e) {
            System.out.println("Error in getAllRoomTypes: " + e.getMessage());
            e.printStackTrace();
        }
        return roomTypes;
    }
    
    // إضافة نوع غرفة جديد
    public int addRoomType(RoomType roomType) {
        String sql = "INSERT INTO room_types (name, capacity, base_price) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, roomType.getName());
            stmt.setInt(2, 2); // capacity - قيمة افتراضية
            stmt.setDouble(3, roomType.getBasePrice());
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            
        } catch (Exception e) {
            System.out.println("Error in addRoomType: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
    
    // تحديث نوع غرفة
    public boolean updateRoomType(RoomType roomType) {
        String sql = "UPDATE room_types SET name = ?, capacity = ?, base_price = ? WHERE type_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, roomType.getName());
            stmt.setInt(2, 2); // capacity
            stmt.setDouble(3, roomType.getBasePrice());
            stmt.setInt(4, roomType.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (Exception e) {
            System.out.println("Error in updateRoomType: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // حذف نوع غرفة
    public boolean deleteRoomType(int id) {
        String sql = "DELETE FROM room_types WHERE type_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (Exception e) {
            System.out.println("Error in deleteRoomType: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // الحصول على نوع غرفة بواسطة الاسم
    public RoomType getRoomTypeByName(String name) {
        String sql = "SELECT * FROM room_types WHERE name = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                RoomType roomType = new RoomType();
                roomType.setId(rs.getInt("type_id"));
                roomType.setName(rs.getString("name"));
                roomType.setBasePrice(rs.getDouble("base_price"));
                return roomType;
            }
        } catch (Exception e) {
            System.out.println("Error in getRoomTypeByName: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}