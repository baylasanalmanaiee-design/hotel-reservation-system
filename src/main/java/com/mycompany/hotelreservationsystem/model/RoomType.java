/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.model;

/**
 *
 * @author kady
 */
public class RoomType {
    private int id;
    private String name;
    private double basePrice;

    public RoomType() {}

    public RoomType(int id, String name, double basePrice) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    
    ////////
      @Override
    public String toString() {
        return name; // عشان يظهر في الـ JComboBox باسم النوع
    }
    ///
}
