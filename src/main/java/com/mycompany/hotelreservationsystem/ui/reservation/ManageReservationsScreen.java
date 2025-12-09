/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.ui.reservation;

/**
 *
 * @author abeer
 */

import com.mycompany.hotelreservationsystem.DatabaseConnection;
import com.mycompany.hotelreservationsystem.ui.billing.CheckInScreen;
import com.mycompany.hotelreservationsystem.ui.billing.CheckOutScreen;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ManageReservationsScreen extends JDialog {
    private JTextField txtSearch;
    private JButton btnSearch, btnView, btnEdit, btnCancel, btnCheckIn, btnCheckOut;
    private JTable reservationsTable;
    private DefaultTableModel tableModel;

    public ManageReservationsScreen(JFrame parent) {
        super(parent, "Manage Reservations", true);
        setSize(1000, 600);
        setLocationRelativeTo(parent);

        initializeComponents();
        loadReservationsFromDB();
    }

    private void initializeComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel searchPanel = createSearchPanel();
        JPanel tablePanel = createTablePanel();
        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Search Reservations"));

        panel.add(new JLabel("Search:"));
        txtSearch = new JTextField(30);
        panel.add(txtSearch);

        btnSearch = new JButton("Search");
        btnSearch.setBackground(new Color(70, 130, 180));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> searchReservations());
        panel.add(btnSearch);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Reservation ID", "Guest Name", "Room No", "Check-in", "Check-out", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationsTable = new JTable(tableModel);
        reservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        btnView = new JButton("View Details");
        btnEdit = new JButton("Edit");
        btnCancel = new JButton("Cancel Reservation");
        btnCheckIn = new JButton("Go to Check-In");
        btnCheckOut = new JButton("Go to Check-Out");

        styleActionButton(btnView);
        styleActionButton(btnEdit);
        btnCancel.setBackground(Color.ORANGE);
        btnCancel.setForeground(Color.BLACK);
        btnCheckIn.setBackground(new Color(40, 167, 69));
        btnCheckIn.setForeground(Color.WHITE);
        btnCheckOut.setBackground(new Color(23, 162, 184));
        btnCheckOut.setForeground(Color.WHITE);

        btnView.addActionListener(e -> viewReservationDetails());
        btnEdit.addActionListener(e -> editReservation());
        btnCancel.addActionListener(e -> cancelReservation());
        btnCheckIn.addActionListener(e -> goToCheckIn());
        btnCheckOut.addActionListener(e -> goToCheckOut());

        panel.add(btnView);
        panel.add(btnEdit);
        panel.add(btnCancel);
        panel.add(btnCheckIn);
        panel.add(btnCheckOut);

        return panel;
    }

    private void styleActionButton(JButton button) {
        button.setBackground(new Color(108, 117, 125));
        button.setForeground(Color.WHITE);
    }

    // === تحميل الحجوزات من قاعدة البيانات ===
    private void loadReservationsFromDB() {
        tableModel.setRowCount(0);

        String sql = """
            SELECT r.reservation_id,
                   g.full_name,
                   rm.room_number,
                   r.check_in_date,
                   r.check_out_date,
                   r.status
            FROM reservations r
            JOIN guests g ON r.guest_id = g.guest_id
            JOIN rooms rm ON r.room_id = rm.room_id
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        String.valueOf(rs.getInt("reservation_id")),
                        rs.getString("full_name"),
                        rs.getString("room_number"),
                        rs.getString("check_in_date"),
                        rs.getString("check_out_date"),
                        rs.getString("status")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading reservations:\n" + e.getMessage());
        }
    }

    // === البحث ===
    private void searchReservations() {
        String searchTerm = txtSearch.getText().trim().toLowerCase();

        if (searchTerm.isEmpty()) {
            loadReservationsFromDB();
            return;
        }

        tableModel.setRowCount(0);

        String sql = """
            SELECT r.reservation_id,
                   g.full_name,
                   rm.room_number,
                   r.check_in_date,
                   r.check_out_date,
                   r.status
            FROM reservations r
            JOIN guests g ON r.guest_id = g.guest_id
            JOIN rooms rm ON r.room_id = rm.room_id
            WHERE LOWER(g.full_name)     LIKE ?
               OR LOWER(rm.room_number)  LIKE ?
               OR LOWER(r.status)        LIKE ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String like = "%" + searchTerm + "%";
            stmt.setString(1, like);
            stmt.setString(2, like);
            stmt.setString(3, like);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        String.valueOf(rs.getInt("reservation_id")),
                        rs.getString("full_name"),
                        rs.getString("room_number"),
                        rs.getString("check_in_date"),
                        rs.getString("check_out_date"),
                        rs.getString("status")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Search error:\n" + e.getMessage());
        }
    }

    private void viewReservationDetails() {
        int row = reservationsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation first.");
            return;
        }

        String reservationId = tableModel.getValueAt(row, 0).toString();
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        ReservationDetailsScreen details = new ReservationDetailsScreen(parentFrame, reservationId);
        details.setVisible(true);
    }

    private void editReservation() {
        int row = reservationsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation first.");
            return;
        }

        String reservationId = tableModel.getValueAt(row, 0).toString();
        JOptionPane.showMessageDialog(this,
                "Edit reservation: " + reservationId + "\n(Edit form can be implemented here)",
                "Edit Reservation",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelReservation() {
        int row = reservationsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a reservation first!");
            return;
        }

        int resId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        String roomNumber = tableModel.getValueAt(row, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel this reservation?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement()) {

            st.executeUpdate("UPDATE reservations SET status='Cancelled' WHERE reservation_id=" + resId);
            st.executeUpdate("UPDATE rooms SET status='Available' WHERE room_number='" + roomNumber + "'");

            JOptionPane.showMessageDialog(this, "Reservation cancelled and room set to Available.");
            loadReservationsFromDB();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error while cancelling:\n" + e.getMessage());
        }
    }

   private void goToCheckIn() {
    int selectedRow = reservationsTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Select a reservation!");
        return;
    }

    String status = tableModel.getValueAt(selectedRow, 5).toString();
    if (!status.equalsIgnoreCase("Booked")) {
        JOptionPane.showMessageDialog(this, 
            "Only Booked reservations can Check-In!");
        return;
    }

    int resId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
    new CheckInScreen((JFrame) this.getParent()).setVisible(true);

    loadReservationsFromDB();
}


    private void goToCheckOut() {
        int row = reservationsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation first.");
            return;
        }

        String status = tableModel.getValueAt(row, 5).toString();
        if (!status.equalsIgnoreCase("Checked-in")) {
            JOptionPane.showMessageDialog(this,
                    "Only 'Checked-in' reservations can be checked out.\nCurrent status: " + status);
            return;
        }

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        new CheckOutScreen(parentFrame).setVisible(true);

        loadReservationsFromDB();
    }

    // test main
    public static void main(String[] args) {
        JFrame f = new JFrame();
        ManageReservationsScreen s = new ManageReservationsScreen(f);
        s.setVisible(true);
    }
}
