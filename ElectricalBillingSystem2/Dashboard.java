package ElectricalBillingSystem2;
import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Dashboard extends JFrame {
    private JTextField customerIdField;
    private JTextField customerNameField;
    private JTextField unitsField;
    private JTextArea displayArea;
    private JButton calculateButton;
    private JButton printButton;
    private JButton clearButton;
    private JButton logoutButton;
    

    // Billing rates (I can adjust these)
    private static final double RATE_PER_UNIT = 8.50;
    private static final double FIXED_CHARGE = 50.00;
    
    public Dashboard() {
        setTitle("Electrical Billing System - Dashboard");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 240, 240));
        
        // Title
        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setBounds(300, 20, 300, 35);
        add(titleLabel);
        
        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setBounds(50, 80, 800, 150);
        inputPanel.setLayout(null);
        inputPanel.setBorder(BorderFactory.createTitledBorder("Customer Information"));
        inputPanel.setBackground(Color.WHITE);
        add(inputPanel);
        
        // Customer ID
        JLabel idLabel = new JLabel("Customer ID:");
        idLabel.setBounds(30, 30, 120, 25);
        inputPanel.add(idLabel);
        
        customerIdField = new JTextField();
        customerIdField.setBounds(150, 30, 200, 25);
        inputPanel.add(customerIdField);
        
        // Customer Name
        JLabel nameLabel = new JLabel("Customer Name:");
        nameLabel.setBounds(30, 70, 120, 25);
        inputPanel.add(nameLabel);
        
        customerNameField = new JTextField();
        customerNameField.setBounds(150, 70, 200, 25);
        inputPanel.add(customerNameField);
        
        // Units Consumed
        JLabel unitsLabel = new JLabel("Units Consumed:");
        unitsLabel.setBounds(30, 110, 120, 25);
        inputPanel.add(unitsLabel);
        
        unitsField = new JTextField();
        unitsField.setBounds(150, 110, 200, 25);
        inputPanel.add(unitsField);
        
        // Calculate Button (OK Button)
        calculateButton = new JButton("OK - Calculate Bill");
        calculateButton.setBounds(450, 50, 200, 40);
        calculateButton.setBackground(new Color(40, 167, 69));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFont(new Font("Arial", Font.BOLD, 14));
        calculateButton.setFocusPainted(false);
        inputPanel.add(calculateButton);
        
        // Clear Button
        clearButton = new JButton("Clear");
        clearButton.setBounds(450, 100, 95, 30);
        clearButton.setBackground(new Color(255, 193, 7));
        clearButton.setFocusPainted(false);
        inputPanel.add(clearButton);
        
        // Print Button
        printButton = new JButton("Print");
        printButton.setBounds(555, 100, 95, 30);
        printButton.setBackground(new Color(23, 162, 184));
        printButton.setForeground(Color.WHITE);
        printButton.setFocusPainted(false);
        inputPanel.add(printButton);
        
        // Display Area
        JLabel displayLabel = new JLabel("Bill Information:");
        displayLabel.setFont(new Font("Arial", Font.BOLD, 16));
        displayLabel.setBounds(50, 250, 200, 25);
        add(displayLabel);
        
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        displayArea.setBackground(new Color(250, 250, 250));
        
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBounds(50, 280, 800, 330);
        add(scrollPane);
        
        // Logout Button
        logoutButton = new JButton("Logout");
        logoutButton.setBounds(750, 620, 100, 35);
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        add(logoutButton);
        
        // Button Actions
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateBill();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });
        
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printToFile();
            }
        });
        
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                    Dashboard.this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    new LoginPage().setVisible(true);
                    dispose();
                }
            }
        });
    }
    
    private void calculateBill() {
        String customerId = customerIdField.getText().trim();
        String customerName = customerNameField.getText().trim();
        String unitsText = unitsField.getText().trim();
        
        // Validation
        if (customerId.isEmpty() || customerName.isEmpty() || unitsText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int units;
        try {
            units = Integer.parseInt(unitsText);
            if (units < 0) {
                JOptionPane.showMessageDialog(this, "Units cannot be negative!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for units!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Calculate bill
        double unitCharge = units * RATE_PER_UNIT;
        double totalBill = FIXED_CHARGE + unitCharge;
        
        // Save to database
        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "INSERT INTO customers (customer_id, customer_name, units_consumed, total_bill) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, customerId);
            pst.setString(2, customerName);
            pst.setInt(3, units);
            pst.setDouble(4, totalBill);
            
            pst.executeUpdate();
            pst.close();
            conn.close();
            
            // Display bill
            displayBill(customerId, customerName, units, unitCharge, totalBill);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving to database: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void displayBill(String customerId, String customerName, int units, double unitCharge, double totalBill) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = dateFormat.format(new Date());
        
        StringBuilder bill = new StringBuilder();
        bill.append("═══════════════════════════════════════════════════════════\n");
        bill.append("               ELECTRICITY BILL RECEIPT\n");
        bill.append("═══════════════════════════════════════════════════════════\n\n");
        bill.append("Date & Time    : ").append(currentDate).append("\n\n");
        bill.append("Customer ID    : ").append(customerId).append("\n");
        bill.append("Customer Name  : ").append(customerName).append("\n");
        bill.append("Units Consumed : ").append(units).append(" kWh\n\n");
        bill.append("───────────────────────────────────────────────────────────\n");
        bill.append("BILLING DETAILS:\n");
        bill.append("───────────────────────────────────────────────────────────\n");
        bill.append(String.format("Fixed Charge            : ₱ %.2f\n", FIXED_CHARGE));
        bill.append(String.format("Rate per Unit           : ₱ %.2f\n", RATE_PER_UNIT));
        bill.append(String.format("Unit Charges (%d kWh)   : ₱ %.2f\n", units, unitCharge));
        bill.append("───────────────────────────────────────────────────────────\n");
        bill.append(String.format("TOTAL BILL              : ₱ %.2f\n", totalBill));
        bill.append("═══════════════════════════════════════════════════════════\n\n");
        bill.append("Thank you for your payment!\n");
        
        displayArea.setText(bill.toString());
        JOptionPane.showMessageDialog(this, "Bill calculated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void printToFile() {
        String content = displayArea.getText();
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No bill to print! Please calculate a bill first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = dateFormat.format(new Date());
            String filename = "Bill_" + timestamp + ".txt";
            
            FileWriter writer = new FileWriter(filename);
            writer.write(content);
            writer.close();
            
            JOptionPane.showMessageDialog(this, "Bill saved to: " + filename, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearFields() {
        customerIdField.setText("");
        customerNameField.setText("");
        unitsField.setText("");
        displayArea.setText("");
    }
}