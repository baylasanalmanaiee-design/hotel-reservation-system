/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.ui.common;
import javax.swing.*;
/**
 *
 * @author kady
 */
public class LoginForm extends JFrame{

    public LoginForm() {
        
        setTitle("Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
        setVisible(true);
    }
    private void initComponents() {
       
        JPanel panel = new JPanel();
        panel.setLayout(null); 

        
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(40, 30, 100, 25);

        JTextField userField = new JTextField();
        userField.setBounds(150, 30, 180, 25);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(40, 70, 100, 25);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(150, 70, 180, 25);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(150, 120, 100, 30);

        
        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(loginBtn);

        // add panel to frame
        add(panel);

        
        loginBtn.addActionListener(e -> {
            String u = userField.getText();
            String p = new String(passField.getPassword());

            if (u.equals("manager") && p.equals("112211")) {
                JOptionPane.showMessageDialog(this, "Welcome Manager");
                new ManagerDashboard();
                dispose();
            }
            else if (u.equals("reception") && p.equals("990099")) {
                JOptionPane.showMessageDialog(this, "Welcome Receptionist");
                new ReceptionistDashboard();
                dispose();
            }
            else {
                JOptionPane.showMessageDialog(this, "Wrong credentials!");
            }
        });
    }
}
