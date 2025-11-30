/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.model;

/**
 *
 * @author kady
 */
public class Room {
    private int id;
    private int roomNumber;
    private int roomTypeId;
    private String status; // Available, Occupied, Cleaning Required, Maintenance

    public Room() {}

    public Room(int id, int roomNumber, int roomTypeId, String status) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.roomTypeId = roomTypeId;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRoomNumber() { return roomNumber; }
    public void setRoomNumber(int roomNumber) { this.roomNumber = roomNumber; }

    public int getRoomTypeId() { return roomTypeId; }
    public void setRoomTypeId(int roomTypeId) { this.roomTypeId = roomTypeId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
