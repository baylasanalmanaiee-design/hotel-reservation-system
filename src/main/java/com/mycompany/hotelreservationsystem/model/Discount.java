package com.mycompany.hotelreservationsystem.model;

public class Discount {
    private int id;
    private String code;
    private String description;
    private double percentage;
    private boolean active;
    private String startDate; // yyyy-MM-dd (nullable)
    private String endDate;   // yyyy-MM-dd (nullable)

    public Discount() {}

    public Discount(int id, String code, String description, double percentage,
                    boolean active, String startDate, String endDate) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.percentage = percentage;
        this.active = active;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}
