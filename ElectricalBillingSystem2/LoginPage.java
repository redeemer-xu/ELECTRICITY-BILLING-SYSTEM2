package ElectricalBillingSystem2;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;
    
    public LoginPage() {
        setTitle("Electrical Billing System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        
        // Title Label
        JLabel titleLabel = new JLabel("Admin Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(120, 20, 200, 30);
        add(titleLabel);
        
        // Username Label and Field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 80, 100, 25);
        add(usernameLabel);
        
        usernameField = new JTextField();
        usernameField.setBounds(150, 80, 200, 25);
        add(usernameField);
        
        // Password Label and Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 120, 100, 25);
        add(passwordLabel);
        
        passwordField = new JPasswordField();
        passwordField.setBounds(150, 120, 200, 25);
        add(passwordField);
        
        // Login Button
        loginButton = new JButton("Login");
        loginButton.setBounds(150, 170, 100, 30);
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        add(loginButton);
        
        // Message Label
        messageLabel = new JLabel("");
        messageLabel.setBounds(50, 210, 300, 25);
        messageLabel.setForeground(Color.RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(messageLabel);
        
        // Login Button Action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticateUser();
            }
        });
        
        // Press Enter to Login
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticateUser();
            }
        });
    }
    
    private void authenticateUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill in all fields!");
            return;
        }
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                messageLabel.setText("Database connection failed!");
                return;
            }
            
            String query = "SELECT * FROM admin WHERE username = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                messageLabel.setForeground(new Color(0, 128, 0));
                messageLabel.setText("Login Successful!");
                
                // Close login window and open dashboard
                SwingUtilities.invokeLater(() -> {
                    new Dashboard().setVisible(true);
                    dispose();
                });
            } else {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Invalid username or password!");
            }
            
            rs.close();
            pst.close();
            conn.close();
            
        } catch (Exception ex) {
            messageLabel.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginPage().setVisible(true);
        });
    }
}