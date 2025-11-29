/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem;
import java.sql.Connection;
import java.sql.DriverManager;
/**
 *
 * @author kady
 */
public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:src/main/resources/hotel.db";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}