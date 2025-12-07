/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.model;

/**
 *
 * @author kady
 */
public class Payment {
    private int id;
    private int invoiceId;
    private double amount;
    private String method;
    private String date;

    public Payment() {}

    public Payment(int id, int invoiceId, double amount, String method, String date) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.method = method;
        this.date = date;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getInvoiceId() { return invoiceId; }
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
     public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
