/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem;

import java.sql.Connection;
import java.sql.Statement;
/**
 *
 * @author kady
 */
public class CreateTables {

    public static void createAllTables() {
        createUsersTable();
        createRoomTypesTable();
        createRoomsTable();
        createGuestsTable();
        createReservationsTable();
        createDiscountsTable();
        createInvoicesTable();
        createPaymentsTable();
    }

    private static void createUsersTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id    INTEGER PRIMARY KEY AUTOINCREMENT,
                username   TEXT NOT NULL,
                password   TEXT NOT NULL,
                role       TEXT NOT NULL
            );
            """;
        execute(sql, "users");
    }

    private static void createRoomTypesTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS room_types (
                type_id    INTEGER PRIMARY KEY AUTOINCREMENT,
                name       TEXT NOT NULL,
                capacity   INTEGER NOT NULL,
                base_price REAL NOT NULL
            );
            """;
        execute(sql, "room_types");
    }

    private static void createRoomsTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS rooms (
                room_id     INTEGER PRIMARY KEY AUTOINCREMENT,
                room_number TEXT NOT NULL,
                floor       INTEGER NOT NULL,
                type_id     INTEGER NOT NULL,
                status      TEXT NOT NULL
            );
            """;
        execute(sql, "rooms");
    }

    private static void createGuestsTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS guests (
                guest_id    INTEGER PRIMARY KEY AUTOINCREMENT,
                full_name   TEXT NOT NULL,
                national_id TEXT,
                phone       TEXT,
                email       TEXT
            );
            """;
        execute(sql, "guests");
    }

    private static void createReservationsTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS reservations (
                reservation_id INTEGER PRIMARY KEY AUTOINCREMENT,
                guest_id       INTEGER NOT NULL,
                room_id        INTEGER NOT NULL,
                check_in_date  TEXT NOT NULL,
                check_out_date TEXT NOT NULL,
                status         TEXT NOT NULL,
                created_by     INTEGER
            );
            """;
        execute(sql, "reservations");
    }

    private static void createDiscountsTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS discounts (
                discount_id INTEGER PRIMARY KEY AUTOINCREMENT,
                code        TEXT NOT NULL,
                description TEXT,
                percentage  REAL NOT NULL,
                active      INTEGER NOT NULL
            );
            """;
        execute(sql, "discounts");
    }

    private static void createInvoicesTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS invoices (
                invoice_id      INTEGER PRIMARY KEY AUTOINCREMENT,
                reservation_id  INTEGER NOT NULL,
                subtotal        REAL NOT NULL,
                discount_amount REAL NOT NULL,
                tax_amount      REAL NOT NULL,
                total_amount    REAL NOT NULL,
                created_at      TEXT NOT NULL
            );
            """;
        execute(sql, "invoices");
    }

    private static void createPaymentsTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS payments (
                payment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                invoice_id INTEGER NOT NULL,
                amount     REAL NOT NULL,
                method     TEXT NOT NULL,
                paid_at    TEXT NOT NULL
            );
            """;
        execute(sql, "payments");
    }

    private static void execute(String sql, String tableName) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("Table '" + tableName + "' created (if not exists).");

        } catch (Exception e) {
            System.out.println("Error creating table: " + tableName);
            e.printStackTrace();
        }
    }
}

