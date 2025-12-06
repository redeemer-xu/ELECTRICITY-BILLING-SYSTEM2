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
        
        // Load icons first
        loadIcons();
        
        // Create main panel with background
        BackgroundPanel mainPanel = new BackgroundPanel();
        mainPanel.setLayout(null);
        setContentPane(mainPanel);
        
        // Title Label
        JLabel titleLabel = new JLabel("Admin Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setBounds(330, 150, 300, 50);
        titleLabel.setForeground(Color.WHITE);
        mainPanel.add(titleLabel);
        
        // Username Label
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        usernameLabel.setBounds(250, 250, 120, 30);
        usernameLabel.setForeground(Color.WHITE);
        mainPanel.add(usernameLabel);
        
        usernameField = new JTextField();
        usernameField.setBounds(380, 250, 250, 30);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(usernameField);
        
        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 18));
        passwordLabel.setBounds(250, 310, 120, 30);
        passwordLabel.setForeground(Color.WHITE);
        mainPanel.add(passwordLabel);
        
        passwordField = new JPasswordField();
        passwordField.setBounds(380, 310, 250, 30);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(passwordField);
        
        // Toggle Password Visibility Button
        togglePasswordButton = new JButton();
        togglePasswordButton.setIcon(eyeClosedIcon);
        togglePasswordButton.setBounds(635, 310, 30, 30);
        togglePasswordButton.setFocusPainted(false);
        togglePasswordButton.setToolTipText("Show/Hide Password");
        togglePasswordButton.setBorderPainted(false);
        togglePasswordButton.setContentAreaFilled(false);
        mainPanel.add(togglePasswordButton);
        
        // Login Button
        loginButton = new JButton("Login");
        loginButton.setBounds(380, 380, 120, 40);
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        mainPanel.add(loginButton);
        
        // Message Label
        messageLabel = new JLabel("");
        messageLabel.setBounds(250, 450, 400, 30);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setForeground(Color.RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(messageLabel);
        
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
    
    // Custom JPanel that draws background image
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;
        
        public BackgroundPanel() {
            try {
                File backgroundFile = new File("images/background image 2.jpg");
                if (!backgroundFile.exists()) {
                    backgroundFile = new File("images\\background image 2.jpg");
                }
                
                if (backgroundFile.exists()) {
                    backgroundImage = ImageIO.read(backgroundFile);
                    System.out.println("Background image loaded successfully!");
                } else {
                    System.out.println("Background image not found");
                }
            } catch (Exception e) {
                System.out.println("Error loading background: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                // Draw the image scaled to panel size
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                // Fallback color if image not loaded
                setBackground(new Color(240, 240, 240));
            }
        }
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
            passwordField.setEchoChar('•');
            togglePasswordButton.setIcon(eyeClosedIcon);
            passwordVisible = false;
        } else {
            passwordField.setEchoChar((char) 0);
            togglePasswordButton.setIcon(eyeOpenIcon);
            passwordVisible = true;
        }
    }

    private void loadIcons() {
        try {
            File eyeOpenFile = new File("images/eye-show.png");
            if (eyeOpenFile.exists()) {
                BufferedImage eyeOpenImg = ImageIO.read(eyeOpenFile);
                Image scaledOpen = eyeOpenImg.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                eyeOpenIcon = new ImageIcon(scaledOpen);
            }
            
            File eyeClosedFile = new File("images/eye.png");
            if (eyeClosedFile.exists()) {
                BufferedImage eyeClosedImg = ImageIO.read(eyeClosedFile);
                Image scaledClosed = eyeClosedImg.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                eyeClosedIcon = new ImageIcon(scaledClosed);
            }
            
            if (eyeOpenIcon == null && eyeClosedIcon != null) {
                eyeOpenIcon = eyeClosedIcon;
            }
            
        } catch (Exception e) {
            System.out.println("Could not load icons");
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginPage().setVisible(true);
        });
    }
}