/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hotelreservationsystem.ui.common;
import javax.swing.*;
import com.mycompany.hotelreservationsystem.dao.UserDAO;
import com.mycompany.hotelreservationsystem.model.User;

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
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        // استدعاء DAO
        UserDAO dao = new UserDAO();
        User user = dao.validateLogin(username, password);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid username or password!");
            return;
        }

        // لو موجود - نفتح الداشبورد حسب الدور (role)
        JOptionPane.showMessageDialog(this, "Welcome " + user.getUsername());
        // ✅ حفظ المستخدم الحالي في السيشن
        com.mycompany.hotelreservationsystem.util.Session.currentUserId = user.getId();
        com.mycompany.hotelreservationsystem.dao.ActivityLogDAO.log(
        com.mycompany.hotelreservationsystem.util.Session.currentUserId,
        "LOGIN"
);

        if (user.getRole().equalsIgnoreCase("manager")) {
            new ManagerDashboard().setVisible(true);
        } else if (user.getRole().equalsIgnoreCase("receptionist")) {
            new ReceptionistDashboard().setVisible(true);
        }

        dispose(); // نسكر صفحة اللوق ان
    });

    }
}
