package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.*;

public class OrderDAO extends BaseDAO implements DAO<Order> {

    @Override
    public void create(Order order) {
        try {
            // Insert order without payment yet
            PreparedStatement stmt = getConnection().prepareStatement(
                    "INSERT INTO Orders(cashier_id, total_price, is_paid) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            stmt.setInt(1, order.getCashierId());
            stmt.setFloat(2, order.calculateTotal());
            stmt.setBoolean(3, order.isPaid());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int orderId = rs.getInt(1);
                order.setId(orderId);

                // Insert order items
                for (OrderItem item : order.getItems()) {
                    PreparedStatement itemStmt = getConnection().prepareStatement(
                        "INSERT INTO OrderItems(order_id, menu_item_id, quantity, price) VALUES (?, ?, ?, ?)"
                    );
                    itemStmt.setInt(1, orderId);
                    itemStmt.setInt(2, item.getItem().getId());
                    itemStmt.setInt(3, item.getQuantity());
                    itemStmt.setFloat(4, item.calculatePrice());
                    itemStmt.executeUpdate();
                    itemStmt.close();
                }
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update order after payment
    public void markAsPaid(Order order, Payment payment) {
        try {
            // Update payment info
            PreparedStatement stmt = getConnection().prepareStatement(
                "UPDATE Orders SET is_paid = ?, payment_type = ?, payment_amount = ? WHERE id = ?"
            );
            stmt.setBoolean(1, true);
            stmt.setString(2, payment instanceof CashPayment ? "Cash" : "Card");
            stmt.setFloat(3, payment.getAmount());
            stmt.setInt(4, order.getId());
            stmt.executeUpdate();
            stmt.close();

            // Update inventory
            for (OrderItem item : order.getItems()) {
                PreparedStatement stockStmt = getConnection().prepareStatement(
                    "UPDATE Menu SET quantity = quantity - ? WHERE id = ?"
                );
                stockStmt.setInt(1, item.getQuantity());
                stockStmt.setInt(2, item.getItem().getId());
                stockStmt.executeUpdate();
                stockStmt.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override public Order getById(int id) { return null; }
    @Override public List<Order> getAll() { return new ArrayList<>(); }
    @Override public void update(Order entity) {}
    @Override public void delete(int id) {}
}