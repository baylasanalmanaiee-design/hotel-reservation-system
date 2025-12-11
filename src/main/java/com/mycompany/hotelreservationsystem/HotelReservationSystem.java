/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.hotelreservationsystem;
import com.mycompany.hotelreservationsystem.ui.common.LoginForm;
import java.sql.Connection;
/**
 *
 * @author kady
 */
public class HotelReservationSystem {

    public static void main(String[] args) {
        //CreateTables.createAllTables();  
        //System.out.println("Database created successfully!");

        new LoginForm();
        Connection conn = DatabaseConnection.getConnection();
    if (conn != null) {
        System.out.println("Connected to database successfully!");
    } else {
        System.out.println("Failed to connect.");
        }
    //SeedData.insertSampleUsers();
    //SeedData.insertSampleRoomTypes();
    //SeedData.insertSampleRooms();
    
    }
}
