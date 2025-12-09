package ElectricalBillingSystem2;

import javax.swing.*; // GUI components like JFrame, JButton, JLabel, JTextField, JPasswordField
import java.awt.*;    // Para sa layout managers and colors like Fonts, colors, graphics etc.
import java.awt.event.ActionEvent; // para sa action events like button clicks
import java.awt.event.ActionListener; // para sa action listeners for handling events
import java.sql.Connection; // para sa database connection
import java.sql.PreparedStatement; // for executing sql queries safely
import java.sql.ResultSet; // mag store sa results gikan sa database sql select queries
import java.io.File; // para sa file handling 
import javax.imageio.ImageIO; // para sa reading image files
import java.awt.image.BufferedImage; // para sa buffered image handling

public class LoginPage extends JFrame { // here we create a class  & inheritance

    // we use encapsulation concept in oop  on the instance variables
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton togglePasswordButton;
    private JLabel messageLabel;
    private boolean passwordVisible = false;
    private ImageIcon eyeOpenIcon;
    private ImageIcon eyeClosedIcon;
    
    public LoginPage() { // constructor ang concpet ge gamit diri

        setTitle("Electrical Billing System - Login");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set what happens when user clicks the X button (exit entire application)
        setLocationRelativeTo(null); // null means center of the screen
        
        // Load eye icons for password toggle button
        loadIcons();
        
        // Create main panel with background
        BackgroundPanel mainPanel = new BackgroundPanel();
        mainPanel.setLayout(null);
        setContentPane(mainPanel);
        
        // CREATE AND POSITION GUI COMPONENTS

        // Title Label
        JLabel titleLabel = new JLabel("Admin Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setBounds(330, 150, 300, 50);
        titleLabel.setForeground(Color.WHITE); // Set text color to white
        mainPanel.add(titleLabel); // Add to panel
        
        // Username Label
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        usernameLabel.setBounds(250, 250, 120, 30);
        usernameLabel.setForeground(Color.WHITE);
        mainPanel.add(usernameLabel);
        
         // Username Text Field - where user types username
        usernameField = new JTextField(); // Create text field para maka input ang user sa username
        usernameField.setBounds(380, 250, 250, 30); // Position it
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font
        mainPanel.add(usernameField); // eh add sa panel
        
        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 18));
        passwordLabel.setBounds(250, 310, 120, 30);
        passwordLabel.setForeground(Color.WHITE);
        mainPanel.add(passwordLabel);
        
        // Password Field - hides characters as user types
        passwordField = new JPasswordField(); // Create password field para maka input ang user sa password
        passwordField.setBounds(380, 310, 250, 30);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(passwordField);
        
        // Toggle Password Visibility Button
        togglePasswordButton = new JButton();
        togglePasswordButton.setIcon(eyeClosedIcon); // Initially shows "closed eye"
        togglePasswordButton.setBounds(635, 310, 30, 30);
        togglePasswordButton.setFocusPainted(false); // Remove focus border
        togglePasswordButton.setToolTipText("Show/Hide Password"); // Hover text
        togglePasswordButton.setBorderPainted(false);  // Remove border
        togglePasswordButton.setContentAreaFilled(false); // Transparent background
        mainPanel.add(togglePasswordButton);
        
        // Login Button - main action button   
        loginButton = new JButton("Login");
        loginButton.setBounds(380, 380, 120, 40);
        loginButton.setBackground(new Color(0, 123, 255));  // Blue background
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setFocusPainted(false); // Remove focus border
        mainPanel.add(loginButton);
        
        // Message Label - shows "Invalid login" or "Login successful"
        messageLabel = new JLabel(""); // Initially empty
        messageLabel.setBounds(250, 450, 400, 30);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setForeground(Color.RED);  // Error messages na red ang color
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center text
        mainPanel.add(messageLabel);
        

         //oop concept used: POLYMORPHISM - ActionListener Interface

        // Login Button Action
        loginButton.addActionListener(new ActionListener() {
            @Override // This overrides the actionPerformed method from ActionListener
            public void actionPerformed(ActionEvent e) {
                authenticateUser(); // Call the authenticateUser method when button is clicked
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

    // oop concept used: INNER CLASS
    
    // Custom JPanel that draws background image
    class BackgroundPanel extends JPanel {
        private Image backgroundImage; // Variable to store the background image
        
        public BackgroundPanel() {  // Constructor - loads the background image
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

        // oop concept used: method overriding (polymorphism)
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Call the parent class's paintComponent first

            if (backgroundImage != null) {
                // Draw the image scaled to panel size
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                // Fallback color if image not loaded
                setBackground(new Color(240, 240, 240));
            }
        }
    }
    
    // OOP CONCEPT: METHOD / ENCAPSULATION
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
            PreparedStatement pst = conn.prepareStatement(query); // to prevent sql injection
            pst.setString(1, username); // Replace first ? with username
            pst.setString(2, password);  // Replace second ? with password
            
            ResultSet rs = pst.executeQuery(); // Execute the query and get results
            
            if (rs.next()) {
                messageLabel.setForeground(new Color(0, 128, 0));  // message color to green and show success
                messageLabel.setText("Login Successful!");
                
                SwingUtilities.invokeLater(() -> { // this "->" lambda expressions, a feature in Java 8 and later versions
                    new Dashboard().setVisible(true); // Create and show the Dashboard window
                    dispose(); // Close the login window
                });
            } else {
                // LOGIN FAILED
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Invalid username or password!");
            }
            // // Close database resources to free memory 
            rs.close();
            pst.close();
            conn.close();
            
        } catch (Exception ex) { // If any error occurs during database operations
            messageLabel.setText("Error: " + ex.getMessage());
            ex.printStackTrace(); // Print error details for debugging
        }
    }
    // METHOD: Toggle Password Visibility
    private void togglePasswordVisibility() {
        if (passwordVisible) {
            passwordField.setEchoChar('•'); // Show bullets 
            togglePasswordButton.setIcon(eyeClosedIcon); // Change to closed eye
            passwordVisible = false;
        } else {
            passwordField.setEchoChar((char) 0);  // Show actual characters
            togglePasswordButton.setIcon(eyeOpenIcon); // Change to open eye
            passwordVisible = true;
        }
    }


    // METHOD: Load Eye Icons
    private void loadIcons() {
        try {
             // Try to load "eye open" icon
            File eyeOpenFile = new File("images/eye-show.png");
            if (eyeOpenFile.exists()) {
                BufferedImage eyeOpenImg = ImageIO.read(eyeOpenFile);
                Image scaledOpen = eyeOpenImg.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                eyeOpenIcon = new ImageIcon(scaledOpen);
            }
            // Try to load "eye closed" icon
            File eyeClosedFile = new File("images/eye.png");
            if (eyeClosedFile.exists()) {
                BufferedImage eyeClosedImg = ImageIO.read(eyeClosedFile);
                Image scaledClosed = eyeClosedImg.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                eyeClosedIcon = new ImageIcon(scaledClosed);
            }
            // If only one icon loaded, use it for both
            if (eyeOpenIcon == null && eyeClosedIcon != null) {
                eyeOpenIcon = eyeClosedIcon;
            }
            
        } catch (Exception e) {
            System.out.println("Could not load icons");
            e.printStackTrace();
        }
    }
    //MAIN METHOD - Entry Point of the Application
    public static void main(String[] args) {
        //SwingUtilities.invokeLater() runs the GUI code on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new LoginPage().setVisible(true); // Create a new LoginPage object and make it visible
        });
    }
}

/*  SUMMARY OF OOP CONCEPTS USED IN LoginPage:
 * 1. CLASS: LoginPage is a class that represents the login window
 * 
 * 2. INHERITANCE: LoginPage extends JFrame (inherits window features)
 * 
 * 3. ENCAPSULATION: Private fields (usernameField, passwordField, etc.)
 *    can only be accessed within LoginPage class
 * 
 * 4. CONSTRUCTOR: LoginPage() initializes the object when created
 * 
 * 5. METHODS: authenticateUser(), togglePasswordVisibility(), loadIcons()
 *    are methods that perform specific tasks
 * 
 * 6. INNER CLASS: BackgroundPanel is defined inside LoginPage
 * 
 * 7. POLYMORPHISM: 
 *    - ActionListener interface implemented multiple times
 *    - paintComponent() method overridden
 * 
 * 8. ABSTRACTION: Complex authentication logic hidden in simple method
 */