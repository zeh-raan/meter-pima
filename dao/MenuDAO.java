package dao;

import model.MenuItem;
import db.DB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {

    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT id, name, price FROM Menu ORDER BY name";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Since MenuItem is abstract, we create a simple concrete wrapper or use Food/Drink.
                // For simplicity here, we'll treat them as generic MenuItem.
                // If you have Food/Drink constructors, adjust accordingly.
                items.add(new model.Food(   // or Drink - choose based on type if needed
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getFloat("price"),
                    ""   // ingredient placeholder if required by subclass
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return items;
    }
}