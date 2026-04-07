// TODO: Make proper packages (folders) for better seperation of concerns
// F u NO
// Ton faire li seulment 🤣

import db.DB;
import java.sql.*;

public class Main {
    public static void main(String[] args) {
        try {

            // 1. Connect to database
            // 2. Feed application with data from DB
            DB.Connect();
            DB.testSelect();

            // 3. Launch GUI

        // Handle error(s)
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}