/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.model;

 import javax.swing.*;
import javax.swing.table.DefaultTableModel;  // <-- مهم جداً


public class Guest {
    
String[] guestColumns = {"Guest ID", "Name", "Phone", "Email"};
DefaultTableModel guestModel = new DefaultTableModel(guestColumns, 0);
JTable guestsTable = new JTable(guestModel);


}
