package db;

import java.sql.*;

public class DB {
    private static Connection conn; // Encapsulation - only db can access conn

    // SQL Commands that are executed to initialise the database
    private static final String CREATE_INGREDIENT_TABLE = """
        CREATE TABLE IF NOT EXISTS Ingredient (
            id       INTEGER PRIMARY KEY AUTOINCREMENT,
            name     TEXT NOT NULL,
            quantity INTEGER NOT NULL DEFAULT 0
        );
    """;

    private static final String CREATE_STAFF_TABLE = """
        CREATE TABLE IF NOT EXISTS Staff (
            id       INTEGER PRIMARY KEY AUTOINCREMENT,
            name     TEXT NOT NULL UNIQUE,
            password TEXT NOT NULL,
            is_admin INTEGER NOT NULL DEFAULT 0
        );
    """;

    private static final String CREATE_MENU_TABLE = """
        CREATE TABLE IF NOT EXISTS Menu (
            id    INTEGER PRIMARY KEY AUTOINCREMENT,
            type  TEXT NOT NULL CHECK (type IN ('food', 'drink')),
            name  TEXT NOT NULL,
            price REAL NOT NULL CHECK (price >= 0)
        );
    """;

    // Inserting sample data
    private static final String[] INSERT_INGREDIENTS = {
        """
            INSERT INTO Ingredient (name, quantity)
            SELECT 'Bread', 50
            WHERE NOT EXISTS (SELECT 1 FROM Ingredient WHERE name = 'Bread');
        """,

        """
            INSERT INTO Ingredient (name, quantity)
            SELECT 'Cheese', 30
            WHERE NOT EXISTS (SELECT 1 FROM Ingredient WHERE name = 'Cheese');
        """,

        """
            INSERT INTO Ingredient (name, quantity)
            SELECT 'Ham', 20
            WHERE NOT EXISTS (SELECT 1 FROM Ingredient WHERE name = 'Ham');
        """,

        """
            INSERT INTO Ingredient (name, quantity)
            SELECT 'Cola Syrup', 40
            WHERE NOT EXISTS (SELECT 1 FROM Ingredient WHERE name = 'Cola Syrup');
        """
    };

    private static final String[] INSERT_STAFF = {
        """
            INSERT INTO Staff (name, password, is_admin)
            SELECT 'admin', 'admin', 1
            WHERE NOT EXISTS (SELECT 1 FROM Staff WHERE name = 'admin');
        """,

        """
            INSERT INTO Staff (name, password, is_admin)
            SELECT 'cashier', 'password', 0
            WHERE NOT EXISTS (SELECT 1 FROM Staff WHERE name = 'cashier');
        """
    };

    private static final String[] INSERT_MENU = {
        """
            INSERT INTO Menu (type, name, price)
            SELECT 'food', 'Burger', 50.0
            WHERE NOT EXISTS (SELECT 1 FROM Menu WHERE name = 'Burger');
        """,

        """
            INSERT INTO Menu (type, name, price)
            SELECT 'food', 'Sandwich', 35.0
            WHERE NOT EXISTS (SELECT 1 FROM Menu WHERE name = 'Sandwich');
        """,

        """
            INSERT INTO Menu (type, name, price)
            SELECT 'drink', 'Cola', 20.0
            WHERE NOT EXISTS (SELECT 1 FROM Menu WHERE name = 'Cola');
        """,

        """
            INSERT INTO Menu (type, name, price)
            SELECT 'drink', 'Orange Juice', 25.0
            WHERE NOT EXISTS (SELECT 1 FROM Menu WHERE name = 'Orange Juice');
        """
    };

    // Initialize database connection
    public static void Connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:db/test.db");

            System.out.println("Success: Opened database."); // Output test
            
            // Creates tables if they do not exist
            // Inserts sample test data on first init
            Statement stmt = conn.createStatement();

            // Creating tables
            stmt.execute(CREATE_INGREDIENT_TABLE);
            stmt.execute(CREATE_STAFF_TABLE);
            stmt.execute(CREATE_MENU_TABLE);

            for (String sql : INSERT_INGREDIENTS) { stmt.execute(sql); }
            for (String sql : INSERT_STAFF) { stmt.execute(sql); }
            for (String sql : INSERT_MENU) { stmt.execute(sql); }

            System.out.println("Success: Sample data inserted into database.");
            stmt.close();

        // Handle error
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error: SQLite JBDC driver not found", e);
        }
    }

    // Returns DB connection instance for DAOs
    public static Connection getConnection() throws SQLException {
        if (conn.isClosed()) {
            throw new SQLException("Error: JDBC connection is closed!");
        }

        return conn; 
    }

    // NOTE: Temporary method for testing
    public static void testSelect() throws SQLException {
        String query = "SELECT * FROM Menu";

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        System.out.println("=== Ingredients ===");

        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");

            System.out.println(id + " | " + name);
        }

        rs.close();
        stmt.close();
    }
}