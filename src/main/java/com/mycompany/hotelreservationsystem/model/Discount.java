/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.model;

/**
 *
 * @author kady
 */
public class Discount {
    private int id;
    private String code;
    private double percentage;

    public Discount() {}

    public Discount(int id, String code, double percentage) {
        this.id = id;
        this.code = code;
        this.percentage = percentage;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }
}
