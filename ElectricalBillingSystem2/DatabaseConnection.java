package ElectricalBillingSystem2;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection { // We use encapsulation para ma hide ang connection details
    private static final String URL = "jdbc:mysql://localhost:3306/billing_system"; // mao ang mag ingon na asa na locate ang database
    private static final String USER = "root"; // default username sa mysql
    private static final String PASSWORD = "124700"; // mao ni ang password sa mysql user
    
    public static Connection getConnection() { // We use method  / abstraction
        Connection conn = null; // nag declare ta og connection variable na naka set sa null
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // The driver translates Java code into MySQL commands
            conn = DriverManager.getConnection(URL, USER, PASSWORD); // creates a connection to the database
            System.out.println("Database connected successfully!");
        } catch (ClassNotFoundException e) { // we apply exception handling para ma catch ang possible errors 
            System.out.println("MySQL Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection failed!");
            e.printStackTrace(); // Print error details to console
        }
        return conn;
    }
}

/* concepts na gigamit namo:
  1. CLASS - DatabaseConnection is a class
  2. ENCAPSULATION - Private fields hide implementation details
  3. STATIC METHOD - getConnection() can be called without object
  4. ABSTRACTION - Hides complex connection logic behind simple method
*/