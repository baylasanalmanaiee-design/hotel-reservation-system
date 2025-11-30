/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.ui.reservation;

/**
 *
 * @author Bilsan
 */
import javax.swing.*;

public class WaitlistForm extends JFrame {

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
