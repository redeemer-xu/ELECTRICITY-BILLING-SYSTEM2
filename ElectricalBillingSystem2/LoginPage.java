package ElectricalBillingSystem2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton togglePasswordButton;
    private JLabel messageLabel;
    private boolean passwordVisible = false;
    private ImageIcon eyeOpenIcon;
    private ImageIcon eyeClosedIcon;
    
    public LoginPage() {
        setTitle("Electrical Billing System - Login");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 240, 240));
        
        // Load icons
        loadIcons();
        
        // Title Label
        JLabel titleLabel = new JLabel("Admin Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setBounds(350, 150, 250, 40);
        add(titleLabel);
        
        // Username Label and Field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameLabel.setBounds(250, 250, 120, 30);
        add(usernameLabel);
        
        usernameField = new JTextField();
        usernameField.setBounds(380, 250, 250, 30);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        add(usernameField);
        
        // Password Label and Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordLabel.setBounds(250, 310, 120, 30);
        add(passwordLabel);
        
        passwordField = new JPasswordField();
        passwordField.setBounds(380, 310, 250, 30);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        add(passwordField);
        
        // Toggle Password Visibility Button
        togglePasswordButton = new JButton();
        togglePasswordButton.setIcon(eyeClosedIcon);
        togglePasswordButton.setBounds(635, 310, 30, 30);
        togglePasswordButton.setFocusPainted(false);
        togglePasswordButton.setToolTipText("Show/Hide Password");
        togglePasswordButton.setBorderPainted(false);
        togglePasswordButton.setContentAreaFilled(false);
        add(togglePasswordButton);
        
        // Login Button
        loginButton = new JButton("Login");
        loginButton.setBounds(380, 380, 120, 40);
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        add(loginButton);
        
        // Message Label
        messageLabel = new JLabel("");
        messageLabel.setBounds(250, 450, 400, 30);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
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
        
        // Toggle Password Visibility
        togglePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePasswordVisibility();
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
    
    private void togglePasswordVisibility() {
        if (passwordVisible) {
            // Hide password
            passwordField.setEchoChar('•');
            togglePasswordButton.setIcon(eyeClosedIcon);
            passwordVisible = false;
        } else {
            // Show password
            passwordField.setEchoChar((char) 0);
            togglePasswordButton.setIcon(eyeOpenIcon);
            passwordVisible = true;
        }
    }

    private void loadIcons() {
        try {
            // Load eye show icon (when password is visible)
            File eyeOpenFile = new File("images/eye-show.png");
            if (eyeOpenFile.exists()) {
                BufferedImage eyeOpenImg = ImageIO.read(eyeOpenFile);
                Image scaledOpen = eyeOpenImg.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                eyeOpenIcon = new ImageIcon(scaledOpen);
            }
            
            // Load eye closed icon (when password is hidden)
            File eyeClosedFile = new File("images/eye.png");
            if (eyeClosedFile.exists()) {
                BufferedImage eyeClosedImg = ImageIO.read(eyeClosedFile);
                Image scaledClosed = eyeClosedImg.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                eyeClosedIcon = new ImageIcon(scaledClosed);
            }
            
            // If only one icon exists, use it for both
            if (eyeOpenIcon == null && eyeClosedIcon != null) {
                eyeOpenIcon = eyeClosedIcon;
            }
            
        } catch (Exception e) {
            System.out.println("Could not load icons, using emoji fallback");
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginPage().setVisible(true);
        });
    }
}