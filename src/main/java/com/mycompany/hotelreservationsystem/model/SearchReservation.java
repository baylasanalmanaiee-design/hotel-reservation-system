/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.model;

/**
 *
 * @author Bilsan
 */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SearchReservation extends JFrame {

    JTextField searchFld = new JTextField(20);
    JButton searchBtn = new JButton("Search");
    JButton viewDetailsBtn = new JButton("View Details");

    DefaultTableModel reservationsModel = new DefaultTableModel(
            new Object[]{"Reservation ID", "Guest Name", "Room Type", "Room No",
                    "Check-In", "Check-Out", "Status"}, 0);
    JTable reservationsTable = new JTable(reservationsModel);

    public SearchReservation() {

        setTitle("Search Reservations");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel top = new JPanel();
        top.add(new JLabel("Search:"));
        top.add(searchFld);
        top.add(searchBtn);

        JPanel bottom = new JPanel();
        bottom.add(viewDetailsBtn);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(reservationsTable), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> {
            reservationsModel.setRowCount(0);
            reservationsModel.addRow(new Object[]{1, "John Doe", "Single", 101, "2025-01-01", "2025-01-05", "BOOKED"
            });
        });

        viewDetailsBtn.addActionListener(e -> openDetailsDialog());
    }

    private void openDetailsDialog() {
        int row = reservationsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a reservation first.");
            return;
        }

        new ReservationDetailsDialog(
                (int) reservationsModel.getValueAt(row, 0),
                reservationsModel.getValueAt(row, 1).toString(),
                reservationsModel.getValueAt(row, 2).toString(),
                reservationsModel.getValueAt(row, 3).toString(),
                reservationsModel.getValueAt(row, 4).toString(),
                reservationsModel.getValueAt(row, 5).toString(),
                reservationsModel.getValueAt(row, 6).toString()
        ).setVisible(true);
    }

    public static void main(String[] args) {
        new SearchReservation().setVisible(true);
    }
}

