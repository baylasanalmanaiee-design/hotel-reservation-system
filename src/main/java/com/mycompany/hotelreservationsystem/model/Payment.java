package com.mycompany.hotelreservationsystem.model;

public class Payment {
    private int id;
    private int invoiceId;
    private double amount;
    private String method;
    private String paidAt; 
    
    public Payment() {}

    public Payment(int id, int invoiceId, double amount, String method, String paidAt) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.method = method;
        this.paidAt = paidAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getInvoiceId() { return invoiceId; }
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getPaidAt() { return paidAt; }
    public void setPaidAt(String paidAt) { this.paidAt = paidAt; }
}
