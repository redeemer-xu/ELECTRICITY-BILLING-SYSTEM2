package ElectricalBillingSystem2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Dashboard extends JFrame {
    private JTextField customerNameField;
    private JTextField unitsField;
    private JTextArea displayArea;
    private JButton calculateButton;
    private JButton printButton;
    private JButton clearButton;
    private JButton logoutButton;
    private JButton viewCustomersButton;
    private JButton refreshButton;
    private JButton editButton;
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JScrollPane tableScrollPane;
    private JLabel generatedIdLabel;
    
    // Billing rates
    private static final double RATE_PER_UNIT = 8.50;
    private static final double FIXED_CHARGE = 50.00;
    
    public Dashboard() {
        setTitle("Electrical Billing System - Dashboard");
        setSize(1100, 900); // Extended size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 240, 240));
        
        // Title
        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setBounds(400, 20, 300, 35);
        add(titleLabel);
        
        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setBounds(50, 80, 1000, 170);
        inputPanel.setLayout(null);
        inputPanel.setBorder(BorderFactory.createTitledBorder("Customer Information"));
        inputPanel.setBackground(Color.WHITE);
        add(inputPanel);
        
        // Customer Name
        JLabel nameLabel = new JLabel("Customer Name:");
        nameLabel.setBounds(30, 30, 120, 25);
        inputPanel.add(nameLabel);
        
        customerNameField = new JTextField();
        customerNameField.setBounds(150, 30, 200, 25);
        inputPanel.add(customerNameField);
        
        // Units Consumed
        JLabel unitsLabel = new JLabel("Units Consumed:");
        unitsLabel.setBounds(30, 70, 120, 25);
        inputPanel.add(unitsLabel);
        
        unitsField = new JTextField();
        unitsField.setBounds(150, 70, 200, 25);
        inputPanel.add(unitsField);
        
        // Generated ID Display (shows after creation)
        JLabel idDisplayLabel = new JLabel("Generated ID:");
        idDisplayLabel.setBounds(30, 110, 120, 25);
        idDisplayLabel.setFont(new Font("Arial", Font.BOLD, 12));
        inputPanel.add(idDisplayLabel);
        
        generatedIdLabel = new JLabel("---");
        generatedIdLabel.setBounds(150, 110, 200, 25);
        generatedIdLabel.setFont(new Font("Arial", Font.BOLD, 14));
        generatedIdLabel.setForeground(new Color(0, 123, 255));
        inputPanel.add(generatedIdLabel);
        
        // Calculate Button
        calculateButton = new JButton("OK - Calculate Bill");
        calculateButton.setBounds(450, 50, 200, 40);
        calculateButton.setBackground(new Color(40, 167, 69));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFont(new Font("Arial", Font.BOLD, 14));
        calculateButton.setFocusPainted(false);
        inputPanel.add(calculateButton);
        
        // Clear Button
        clearButton = new JButton("Clear");
        clearButton.setBounds(670, 50, 95, 30);
        clearButton.setBackground(new Color(255, 193, 7));
        clearButton.setFocusPainted(false);
        inputPanel.add(clearButton);
        
        // Print Button
        printButton = new JButton("Print");
        printButton.setBounds(670, 90, 95, 30);
        printButton.setBackground(new Color(23, 162, 184));
        printButton.setForeground(Color.WHITE);
        printButton.setFocusPainted(false);
        inputPanel.add(printButton);
        
        // View Customers Button
        viewCustomersButton = new JButton("View All Customers");
        viewCustomersButton.setBounds(790, 50, 180, 40);
        viewCustomersButton.setBackground(new Color(0, 123, 255));
        viewCustomersButton.setForeground(Color.WHITE);
        viewCustomersButton.setFont(new Font("Arial", Font.BOLD, 13));
        viewCustomersButton.setFocusPainted(false);
        inputPanel.add(viewCustomersButton);
        
        // Display Area
        JLabel displayLabel = new JLabel("Bill Information:");
        displayLabel.setFont(new Font("Arial", Font.BOLD, 16));
        displayLabel.setBounds(50, 270, 200, 25);
        add(displayLabel);
        
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        displayArea.setBackground(new Color(250, 250, 250));
        
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBounds(50, 300, 1000, 230);
        add(scrollPane);
        
        // Customer Table Section
        JLabel tableLabel = new JLabel("Customer Records:");
        tableLabel.setFont(new Font("Arial", Font.BOLD, 16));
        tableLabel.setBounds(50, 550, 200, 25);
        add(tableLabel);
        
        // Table setup
        String[] columnNames = {"Customer ID", "Name", "Units", "Total Bill", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        customerTable = new JTable(tableModel);
        customerTable.setFont(new Font("Arial", Font.PLAIN, 12));
        customerTable.setRowHeight(25);
        customerTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        tableScrollPane = new JScrollPane(customerTable);
        tableScrollPane.setBounds(50, 580, 1000, 200);
        tableScrollPane.setVisible(false); // Hidden by default
        add(tableScrollPane);
        
        // Refresh Button
        refreshButton = new JButton("Refresh");
        refreshButton.setBounds(50, 790, 120, 35);
        refreshButton.setBackground(new Color(23, 162, 184));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setVisible(false);
        add(refreshButton);
        
        // Edit Button
        editButton = new JButton("Edit Selected");
        editButton.setBounds(190, 790, 150, 35);
        editButton.setBackground(new Color(255, 193, 7));
        editButton.setFocusPainted(false);
        editButton.setVisible(false);
        add(editButton);
        
        // Logout Button
        logoutButton = new JButton("Logout");
        logoutButton.setBounds(950, 790, 100, 35);
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
        
        viewCustomersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleCustomerView();
            }
        });
        
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCustomerData();
            }
        });
        
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedCustomer();
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
    
    private void toggleCustomerView() {
        boolean isVisible = tableScrollPane.isVisible();
        
        if (!isVisible) {
            // Show customer table
            tableScrollPane.setVisible(true);
            refreshButton.setVisible(true);
            editButton.setVisible(true);
            viewCustomersButton.setText("Hide Customers");
            loadCustomerData();
        } else {
            // Hide customer table
            tableScrollPane.setVisible(false);
            refreshButton.setVisible(false);
            editButton.setVisible(false);
            viewCustomersButton.setText("View All Customers");
        }
    }
    
    private void loadCustomerData() {
        tableModel.setRowCount(0);
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "SELECT customer_id, customer_name, units_consumed, total_bill, bill_date FROM customers ORDER BY bill_date DESC";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            
            while (rs.next()) {
                String customerId = rs.getString("customer_id");
                String customerName = rs.getString("customer_name");
                int units = rs.getInt("units_consumed");
                double totalBill = rs.getDouble("total_bill");
                String date = dateFormat.format(rs.getTimestamp("bill_date"));
                
                Object[] row = {customerId, customerName, units, "₱" + String.format("%.2f", totalBill), date};
                tableModel.addRow(row);
            }
            
            rs.close();
            pst.close();
            conn.close();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void editSelectedCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to edit!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String currentId = (String) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentUnitsStr = tableModel.getValueAt(selectedRow, 2).toString();
        
        // Create edit dialog
        JDialog editDialog = new JDialog(this, "Edit Customer - " + currentId, true);
        editDialog.setSize(450, 300);
        editDialog.setLocationRelativeTo(this);
        editDialog.setLayout(null);
        editDialog.getContentPane().setBackground(new Color(240, 240, 240)); //  light shade of gray color
        
        JLabel titleLabel = new JLabel("Edit Customer Information");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBounds(100, 20, 250, 25);
        editDialog.add(titleLabel);
        
        // Customer ID Field (read only)
        JLabel idLabel = new JLabel("Customer ID:");
        idLabel.setBounds(30, 60, 120, 25);
        editDialog.add(idLabel);
        
        JTextField idField = new JTextField(currentId);
        idField.setBounds(150, 60, 250, 25);
        idField.setEditable(false); // ID should not be editable
        idField.setBackground(new Color(230, 230, 230)); // gray background to show it's disabled
        idField.setForeground(Color.DARK_GRAY);
        editDialog.add(idField);
        
        // Customer Name Field kay editable
        JLabel nameLabel = new JLabel("Customer Name:");
        nameLabel.setBounds(30, 100, 120, 25);
        editDialog.add(nameLabel);
        
        JTextField nameField = new JTextField(currentName);
        nameField.setBounds(150, 100, 250, 25);
        editDialog.add(nameField);
        
        // Unit consumed Field kay editable
        JLabel unitsLabel = new JLabel("Units Consumed:");
        unitsLabel.setBounds(30, 140, 120, 25);
        editDialog.add(unitsLabel);
        
        JTextField unitsEditField = new JTextField(currentUnitsStr);
        unitsEditField.setBounds(150, 140, 250, 25);
        editDialog.add(unitsEditField);
        
        // Save Button
        JButton saveButton = new JButton("Save Changes");
        saveButton.setBounds(125, 190, 200, 40);
        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.addActionListener(e -> {
            String newName = nameField.getText().trim();
            String newUnitsStr = unitsEditField.getText().trim();
            
            if (newName.isEmpty() || newUnitsStr.isEmpty()) {
                JOptionPane.showMessageDialog(editDialog, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                int newUnits = Integer.parseInt(newUnitsStr);
                if (newUnits < 0) {
                    JOptionPane.showMessageDialog(editDialog, "Units cannot be negative!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Calculate new bill
                double newTotalBill = FIXED_CHARGE + (newUnits * RATE_PER_UNIT);
                
                Connection conn = DatabaseConnection.getConnection();
                
                // 
               String query = "UPDATE customers SET customer_name = ?, units_consumed = ?, total_bill = ? WHERE customer_id = ?";
               PreparedStatement pst = conn.prepareStatement(query);
               pst.setString(1, newName);
               pst.setInt(2, newUnits);
               pst.setDouble(3, newTotalBill);
               pst.setString(4, currentId); // Gamiton ang original ID para sa WHERE clause

               int rowsAffected = pst.executeUpdate();
               pst.close();
               conn.close();

               if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(editDialog, "Customer updated sucessfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    editDialog.dispose();
                    loadCustomerData();
               } else {
                    JOptionPane.showMessageDialog(editDialog, "Update failed! Customer not found.", "Error", JOptionPane.ERROR_MESSAGE);
               }
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(editDialog, "Please enter a valid number for units!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(editDialog, "Error updating: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        editDialog.add(saveButton);
        
        editDialog.setVisible(true);
    }
    
    private void calculateBill() {
        String customerName = customerNameField.getText().trim();
        String unitsText = unitsField.getText().trim();
        
        if (customerName.isEmpty() || unitsText.isEmpty()) {
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
        
        double unitCharge = units * RATE_PER_UNIT;
        double totalBill = FIXED_CHARGE + unitCharge;
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            // Generate new Customer ID
            String customerId = generateCustomerId(conn);
            
            String query = "INSERT INTO customers (customer_id, customer_name, units_consumed, total_bill) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, customerId);
            pst.setString(2, customerName);
            pst.setInt(3, units);
            pst.setDouble(4, totalBill);
            
            pst.executeUpdate();
            pst.close();
            conn.close();
            
            // Display the generated ID
            generatedIdLabel.setText(customerId);
            
            displayBill(customerId, customerName, units, unitCharge, totalBill);
            
            // Refresh table if visible
            if (tableScrollPane.isVisible()) {
                loadCustomerData();
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving to database: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private String generateCustomerId(Connection conn) {
        try {
            // Get the highest existing ID number
            String query = "SELECT customer_id FROM customers ORDER BY customer_id DESC LIMIT 1";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            int nextNumber = 1;
            
            if (rs.next()) {
                String lastId = rs.getString("customer_id");
                // Extract number from ID (e.g., "C0001" -> "0001" -> 1)
                String numberPart = lastId.substring(1); // Remove "C"
                int lastNumber = Integer.parseInt(numberPart);
                nextNumber = lastNumber + 1;
            }
            
            rs.close();
            pst.close();
            
            // Format: C0001, C0002, C0003, etc.
            return String.format("C%04d", nextNumber);
            
        } catch (Exception e) {
            e.printStackTrace();
            // If error, return default starting ID
            return "C0001";
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
        customerNameField.setText("");
        unitsField.setText("");
        displayArea.setText("");
        generatedIdLabel.setText("---");
    }
}