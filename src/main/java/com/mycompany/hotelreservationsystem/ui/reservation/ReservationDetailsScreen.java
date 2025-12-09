/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.ui.reservation;

/**
 *
 * @author Bilsan
 */

import com.mycompany.hotelreservationsystem.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ReservationDetailsScreen extends JDialog {

    private JTextArea txtDetails;
    private JButton btnClose;

    private String reservationId;

    public ReservationDetailsScreen(JFrame parent, String reservationId) {
        super(parent, "Reservation Details - " + reservationId, true);
        this.reservationId = reservationId;

        setSize(500, 400);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        txtDetails = new JTextArea(15, 40);
        txtDetails.setEditable(false);
        txtDetails.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtDetails);

        btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnClose);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadReservationDetails();
    }

    private void loadReservationDetails() {
        String sql = """
            SELECT r.reservation_id,
                   g.full_name,
                   g.phone,
                   g.national_id,
                   rm.room_number,
                   rm.status AS room_status,
                   r.check_in_date,
                   r.check_out_date,
                   r.status AS reservation_status,
                   IFNULL(r.total_price, 0) AS total_price
            FROM reservations r
            JOIN guests g ON r.guest_id = g.guest_id
            JOIN rooms rm ON r.room_id = rm.room_id
            WHERE r.reservation_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(reservationId));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                StringBuilder info = new StringBuilder();
                info.append("------------------------------\n");
                info.append("Reservation Details\n");
                info.append("------------------------------\n");
                info.append("Reservation ID : ").append(rs.getInt("reservation_id")).append("\n");
                info.append("Guest Name     : ").append(rs.getString("full_name")).append("\n");
                info.append("Phone          : ").append(rs.getString("phone")).append("\n");
                info.append("National ID    : ").append(rs.getString("national_id")).append("\n");
                info.append("Room Number    : ").append(rs.getString("room_number")).append("\n");
                info.append("Room Status    : ").append(rs.getString("room_status")).append("\n");
                info.append("Check-In Date  : ").append(rs.getString("check_in_date")).append("\n");
                info.append("Check-Out Date : ").append(rs.getString("check_out_date")).append("\n");
                info.append("Reservation St.: ").append(rs.getString("reservation_status")).append("\n");
                info.append("------------------------------\n");
                info.append("Total Price    : ").append(rs.getDouble("total_price")).append(" SR\n");
                info.append("------------------------------\n");

                txtDetails.setText(info.toString());
            } else {
                txtDetails.setText("Reservation not found in database!");
            }

        } catch (Exception e) {
            txtDetails.setText("Error loading details: " + e.getMessage());
        }
    }
}
