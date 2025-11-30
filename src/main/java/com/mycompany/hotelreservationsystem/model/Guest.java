/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.model;

/**
 *
 * @author kady
 */
public class Guest {
    private int id;
    private String fullName;
    private String phone;
    private String idNumber;

    public Guest() {}

    public Guest(int id, String fullName, String phone, String idNumber) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.idNumber = idNumber;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
}
