/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.ui.reservation;

/**
 *
 * @author Bilsan
 */

/*public class NewReservationForm extends JFrame {

    public NewReservationForm() {

        setTitle("New Reservation");
        setSize(420, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel guestLabel = new JLabel("Guest Name:");
        guestLabel.setBounds(40, 30, 120, 25);

        JTextField guestField = new JTextField();
        guestField.setBounds(160, 30, 180, 25);

        JLabel typeLabel = new JLabel("Room Type:");
        typeLabel.setBounds(40, 70, 120, 25);

        JComboBox<String> typeBox = new JComboBox<>();
        typeBox.setBounds(160, 70, 180, 25);
        // ارجع اخذها من قاعدة البيانات بعدين 

        JLabel inLabel = new JLabel("Check-In Date:");
        inLabel.setBounds(40, 110, 120, 25);

        JTextField inField = new JTextField();
        inField.setBounds(160, 110, 180, 25);

        JLabel outLabel = new JLabel("Check-Out Date:");
        outLabel.setBounds(40, 150, 120, 25);

        JTextField outField = new JTextField();
        outField.setBounds(160, 150, 180, 25);

        JLabel stayLabel = new JLabel("Stay Duration:");
        stayLabel.setBounds(40, 190, 120, 25);

        JTextField stayField = new JTextField();
        stayField.setBounds(160, 190, 180, 25);
        stayField.setEditable(false);

        JButton checkBtn = new JButton("Check Availability");
        checkBtn.setBounds(120, 230, 180, 30);

        //     فقط رسالة عامة
        checkBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Checking availability (to be implemented)")
        );

        add(guestLabel);
        add(guestField);
        add(typeLabel);
        add(typeBox);
        add(inLabel);
        add(inField);
        add(outLabel);
        add(outField);
        add(stayLabel);
        add(stayField);
        add(checkBtn);

        setVisible(true);
    }
}
*/



import com.mycompany.hotelreservationsystem.dao.GuestDAO;
import com.mycompany.hotelreservationsystem.dao.ReservationDAO;
import com.mycompany.hotelreservationsystem.dao.RoomDAO;
import com.mycompany.hotelreservationsystem.dao.WaitlistDAO;
import com.mycompany.hotelreservationsystem.model.Guest;
import com.mycompany.hotelreservationsystem.model.Reservation;
import com.mycompany.hotelreservationsystem.model.Room;
import com.mycompany.hotelreservationsystem.model.Waitlist;
import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/*public class NewReservationForm extends JFrame {

    public NewReservationForm() {

        setTitle("New Reservation");
        setSize(420, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        GuestDAO guestDAO = new GuestDAO();
        ReservationDAO reservationDAO = new ReservationDAO();
        WaitlistDAO waitlistDAO = new WaitlistDAO();
       
        
        
        JLabel guestLabel = new JLabel("Guest Name:");
        guestLabel.setBounds(40, 30, 120, 25);

        JTextField guestField = new JTextField();
        guestField.setBounds(160, 30, 180, 25);

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setBounds(40, 70, 120, 25);

        JTextField phoneField = new JTextField();
        phoneField.setBounds(160, 70, 180, 25);

        JLabel idLabel = new JLabel("ID Number:");
        idLabel.setBounds(40, 110, 120, 25);

        JTextField idField = new JTextField();
        idField.setBounds(160, 110, 180, 25);
        

        JLabel typeLabel = new JLabel("Room Type ID:");
        typeLabel.setBounds(40, 150, 120, 25);

        JTextField typeField = new JTextField();
        typeField.setBounds(160, 150, 180, 25);

        JLabel inLabel = new JLabel("Check-In:");
        inLabel.setBounds(40, 190, 120, 25);

        JTextField inField = new JTextField();
        inField.setBounds(160, 190, 180, 25);

        JLabel outLabel = new JLabel("Check-Out:");
        outLabel.setBounds(40, 230, 120, 25);

        JTextField outField = new JTextField();
        outField.setBounds(160, 230, 180, 25);

        JButton saveBtn = new JButton("Create Reservation");
        saveBtn.setBounds(120, 280, 180, 30);

        saveBtn.addActionListener(e -> {
            try {
                String fullName = guestField.getText().trim();
                String phone = phoneField.getText().trim();
                String idNum = idField.getText().trim();
                int typeId = Integer.parseInt(typeField.getText().trim());
                String checkIn = inField.getText().trim();
                String checkOut = outField.getText().trim();

                // 1) إدخال الضيف أو جلبه
                Guest g = new Guest();
                g.setFullName(fullName);
                g.setPhone(phone);
                g.setIdNumber(idNum);

                int guestId = guestDAO.addGuest(g);
                if (guestId < 0) {
                    JOptionPane.showMessageDialog(this, "Error saving guest");
                    return;
                }

                // 2) التحقق من التوفر
                boolean available = reservationDAO.checkAvailability(typeId, checkIn, checkOut);

                if (available) {
                    // إنشاء حجز (room_id يتم تحديده لاحقاً من قبل منطق تخصيص الغرف)
                    Reservation r = new Reservation();
                    r.setGuestId(guestId);
                    r.setRoomId(0); // مكان مخصص: تخصيص غرفة بعداً
                    r.setCheckInDate(checkIn);
                    r.setCheckOutDate(checkOut);
                    r.setTotalPrice(0.0);

                    boolean ok = reservationDAO.createReservation(r);
                    if (ok) JOptionPane.showMessageDialog(this, "Reservation Created");
                    else JOptionPane.showMessageDialog(this, "Error while saving reservation");
                } else {
                    // 3) إضافة إلى لائحة الانتظار
                    String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    Waitlist w = new Waitlist(guestId, typeId, checkIn, checkOut, now);
                    int wid = waitlistDAO.addToWaitlist(w);
                    if (wid > 0) JOptionPane.showMessageDialog(this, "No rooms available. Added to waitlist.");
                    else JOptionPane.showMessageDialog(this, "Failed to add to waitlist.");
                }

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Invalid number format (room type id).");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error occurred.");
            }
        });

        add(guestLabel); add(guestField);
        add(phoneLabel); add(phoneField);
        add(idLabel); add(idField);
        add(typeLabel); add(typeField);
        add(inLabel); add(inField);
        add(outLabel); add(outField);
        add(saveBtn);

        setVisible(true);
    }
}
*/

public class NewReservationForm extends JFrame {

    public NewReservationForm() {

        setTitle("New Reservation");
        setSize(420, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        GuestDAO guestDAO = new GuestDAO();
        ReservationDAO reservationDAO = new ReservationDAO();
        WaitlistDAO waitlistDAO = new WaitlistDAO();
        RoomDAO roomDAO = new RoomDAO();

        JLabel guestLabel = new JLabel("Guest Name:");
        guestLabel.setBounds(40, 30, 120, 25);
        JTextField guestField = new JTextField();
        guestField.setBounds(160, 30, 180, 25);

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setBounds(40, 70, 120, 25);
        JTextField phoneField = new JTextField();
        phoneField.setBounds(160, 70, 180, 25);

        JLabel idLabel = new JLabel("ID Number:");
        idLabel.setBounds(40, 110, 120, 25);
        JTextField idField = new JTextField();
        idField.setBounds(160, 110, 180, 25);

        JLabel typeLabel = new JLabel("Room Type ID:");
        typeLabel.setBounds(40, 150, 120, 25);
        JTextField typeField = new JTextField();
        typeField.setBounds(160, 150, 180, 25);

        JLabel inLabel = new JLabel("Check-In:");
        inLabel.setBounds(40, 190, 120, 25);
        JTextField inField = new JTextField();
        inField.setBounds(160, 190, 180, 25);

        JLabel outLabel = new JLabel("Check-Out:");
        outLabel.setBounds(40, 230, 120, 25);
        JTextField outField = new JTextField();
        outField.setBounds(160, 230, 180, 25);

        JLabel roomLabel = new JLabel("Room ID:");
        roomLabel.setBounds(40, 270, 120, 25);
        JComboBox<Integer> roomBox = new JComboBox<>();
        roomBox.setBounds(160, 270, 180, 25);

        JButton checkBtn = new JButton("Check");
        checkBtn.setBounds(350, 150, 60, 30);
        checkBtn.addActionListener(e -> {
            try {
                int typeId = Integer.parseInt(typeField.getText().trim());
                List<Room> rooms = roomDAO.getAllRooms();
                roomBox.removeAllItems();

                for (Room rm : rooms) {
                    if (rm.getRoomTypeId() == typeId)
                        roomBox.addItem(rm.getId());
                }

                if (roomBox.getItemCount() == 0)
                    JOptionPane.showMessageDialog(this, "No rooms for this type!\nWill add to waitlist.");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enter Type first!");
            }
        });

        JButton saveBtn = new JButton("Save Reservation");
        saveBtn.setBounds(120, 310, 180, 30);
        saveBtn.addActionListener(e -> {
            try {
                Guest guest = new Guest();
                guest.setFullName(guestField.getText());
                guest.setPhone(phoneField.getText());
                guest.setIdNumber(idField.getText());

                int guestId = guestDAO.addGuest(guest);
                int typeId = Integer.parseInt(typeField.getText());
                String checkIn = inField.getText();
                String checkOut = outField.getText();

                Integer roomId = (Integer) roomBox.getSelectedItem();

                if (roomId != null) {
                    Reservation r = new Reservation();
                    r.setGuestId(guestId);
                    r.setRoomId(roomId);
                    r.setCheckInDate(checkIn);
                    r.setCheckOutDate(checkOut);
                    r.setTotalPrice(0.0);

                    reservationDAO.createReservation(r);
                    JOptionPane.showMessageDialog(this, "Reservation Created!");
                }
                else {
                    String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    Waitlist w = new Waitlist(guestId, typeId, checkIn, checkOut, now);
                    waitlistDAO.addToWaitlist(w);

                    JOptionPane.showMessageDialog(this, "Added to Waitlist.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error!");
            }
        });

        add(guestLabel); add(guestField);
        add(phoneLabel); add(phoneField);
        add(idLabel); add(idField);
        add(typeLabel); add(typeField);
        add(inLabel); add(inField);
        add(outLabel); add(outField);
        add(roomLabel); add(roomBox);
        add(checkBtn); add(saveBtn);

        setVisible(true);
    }
}
