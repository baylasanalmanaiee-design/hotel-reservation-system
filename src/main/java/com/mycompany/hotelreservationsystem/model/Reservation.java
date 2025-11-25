/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.model;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;  // <-- مهم جداً



public class Reservation {
    
   String[] reservationColumns = {
    "Reservation ID", "Guest Name", "Room Type",
    "Room No", "Check-In", "Check-Out", "Status"
    };
DefaultTableModel reservationModel = new DefaultTableModel(reservationColumns, 0);
JTable reservationsTable = new JTable(reservationModel);


}
