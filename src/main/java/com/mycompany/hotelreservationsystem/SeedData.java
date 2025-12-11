/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SeedData {

    public static void insertSampleUsers() {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // admin
            ps.setString(1, "admin");
            ps.setString(2, "112211");
            ps.setString(3, "MANAGER");
            ps.executeUpdate();

            // reception
            ps.setString(1, "reception");
            ps.setString(2, "990099");
            ps.setString(3, "RECEPTIONIST");
            ps.executeUpdate();

            System.out.println("Sample users inserted.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertSampleRoomTypes() {
        String sql = "INSERT INTO room_types (name, capacity, base_price) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Single
            ps.setString(1, "Single");
            ps.setInt(2, 1);
            ps.setDouble(3, 200);
            ps.executeUpdate();

            // Double
            ps.setString(1, "Double");
            ps.setInt(2, 2);
            ps.setDouble(3, 350);
            ps.executeUpdate();

            // Suite
            ps.setString(1, "Suite");
            ps.setInt(2, 4);
            ps.setDouble(3, 600);
            ps.executeUpdate();

            System.out.println("Sample room types inserted.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertSampleRooms() {
        String sql = "INSERT INTO rooms (room_number, floor, type_id, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 101 Single
            ps.setString(1, "101");
            ps.setInt(2, 1);
            ps.setInt(3, 1); // type_id حق الـ Single
            ps.setString(4, "Available");
            ps.executeUpdate();

            // 102 Double
            ps.setString(1, "102");
            ps.setInt(2, 1);
            ps.setInt(3, 2); // Double
            ps.setString(4, "Available");
            ps.executeUpdate();

            // 201 Suite
            ps.setString(1, "201");
            ps.setInt(2, 2);
            ps.setInt(3, 3); // Suite
            ps.setString(4, "Available");
            ps.executeUpdate();

            System.out.println("Sample rooms inserted.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
