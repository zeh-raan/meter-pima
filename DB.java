import java.sql.*;

public class DB {
    public static void Connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            DriverManager.getConnection("jdbc:sqlite:test.db");

        } catch ( ClassNotFoundException | SQLException e ) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        System.out.println("Opened database successfully");
    }
}