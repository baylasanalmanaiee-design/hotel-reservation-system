/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.ui.reservation;

/**
 *
 * @author Bilsan
 */

/*public class WaitlistForm extends JFrame {

    public WaitlistForm() {

        setTitle("Waitlist");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel title = new JLabel("Waitlist", SwingConstants.CENTER);
        title.setBounds(0, 10, 350, 30);

        JTextArea listArea = new JTextArea();
        listArea.setEditable(false);
        // ارجع اخذ البيانات من قاعدة البيانات 

        JScrollPane scroll = new JScrollPane(listArea);
        scroll.setBounds(40, 60, 250, 120);

        add(title);
        add(scroll);

        setVisible(true);
    }
}
*/

import com.mycompany.hotelreservationsystem.dao.WaitlistDAO;
import com.mycompany.hotelreservationsystem.model.Waitlist;
import javax.swing.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class WaitlistForm extends JFrame {

    public WaitlistForm() {

        setTitle("Waitlist");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        WaitlistDAO waitlistDAO = new WaitlistDAO();

        JTextArea area = new JTextArea();
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBounds(20, 20, 400, 250);

        JButton refresh = new JButton("Refresh");
        refresh.setBounds(160, 285, 120, 30);

        refresh.addActionListener(e -> {
            List<Waitlist> list = waitlistDAO.getAllWaitlist();
            area.setText("");
            for (Waitlist w : list) {
                area.append("ID: " + w.getId()
                        + " | guestId: " + w.getGuestId()
                        + " | typeId: " + w.getRoomTypeId()
                        + " | " + w.getCheckIn() + " → " + w.getCheckOut()
                        + " | added: " + w.getAddedAt() + "\n");
            }
            if (list.isEmpty()) area.setText("No waitlist entries.");
        });

        add(scroll);
        add(refresh);

        setVisible(true);
    }
}
