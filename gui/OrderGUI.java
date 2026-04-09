import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import db.DB;

public class OrderGUI extends JFrame {

    private JPanel pnlMenu;
    private JTextArea txtCart;
    private JLabel lblTotal;

    private double total = 0.0;

    public OrderGUI() {
        // Initialize Database
        try {
            DB.Connect();  // Creates tables and inserts sample data if needed
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Failed to connect to database:\n" + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        setTitle("Fast Food POS");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(createTopPanel(), BorderLayout.NORTH);
        add(createMenuPanel(), BorderLayout.CENTER);
        add(createCartPanel(), BorderLayout.EAST);

        setSize(900, 550);
        setLocationRelativeTo(null);
        setVisible(true);

        // Load all menu items on startup
        loadAllMenuItems();
    }

    // ==================== TOP PANEL (Simple Refresh Button) ====================
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnRefresh = new JButton("Refresh Menu");
        btnRefresh.addActionListener(e -> loadAllMenuItems());

        panel.add(btnRefresh);
        return panel;
    }

    // ==================== MENU PANEL ====================
    private JPanel createMenuPanel() {
        pnlMenu = new JPanel(new GridLayout(0, 3, 10, 10)); // 3 columns, auto rows
        return pnlMenu;
    }

    // ==================== CART PANEL ====================
    private JPanel createCartPanel() {
        JPanel pnlCart = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Current Order", JLabel.CENTER);
        title.setFont(new Font("Dialog", Font.BOLD, 16));

        txtCart = new JTextArea(15, 25);
        txtCart.setEditable(false);

        JScrollPane scroll = new JScrollPane(txtCart);

        lblTotal = new JLabel("Total: Rs 0.0", JLabel.CENTER);
        lblTotal.setFont(new Font("Dialog", Font.BOLD, 16));

        JButton btnPay = new JButton("Checkout");
        JButton btnClear = new JButton("Clear Order");

        btnPay.addActionListener(e -> processPayment());
        btnClear.addActionListener(e -> clearOrder());

        JPanel bottom = new JPanel(new GridLayout(3, 1, 5, 5));
        bottom.add(lblTotal);
        bottom.add(btnPay);
        bottom.add(btnClear);

        pnlCart.add(title, BorderLayout.NORTH);
        pnlCart.add(scroll, BorderLayout.CENTER);
        pnlCart.add(bottom, BorderLayout.SOUTH);

        return pnlCart;
    }

    // ==================== LOAD ALL ITEMS FROM MENU TABLE ====================
    private void loadAllMenuItems() {
        pnlMenu.removeAll();

        String sql = "SELECT id, name, price FROM Menu ORDER BY name";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                double price = rs.getDouble("price");

                pnlMenu.add(createItemCard(name, price));
            }

            if (!rs.isBeforeFirst()) {  // No items found
                JLabel emptyLabel = new JLabel("No menu items found in database.", JLabel.CENTER);
                pnlMenu.add(emptyLabel);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading menu items:\n" + ex.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        pnlMenu.revalidate();
        pnlMenu.repaint();
    }

    // ==================== CREATE SIMPLE ITEM CARD ====================
    private JPanel createItemCard(String name, double price) {
        JPanel card = new JPanel(new BorderLayout(5, 5));

        JLabel lblName = new JLabel(name, JLabel.CENTER);
        JLabel lblPrice = new JLabel("Rs " + price, JLabel.CENTER);

        JButton btnAdd = new JButton("Add to Cart");
        btnAdd.addActionListener(e -> addToCart(name, price));

        card.add(lblName, BorderLayout.NORTH);
        card.add(lblPrice, BorderLayout.CENTER);
        card.add(btnAdd, BorderLayout.SOUTH);

        return card;
    }

    // ==================== CART LOGIC ====================
    private void addToCart(String name, double price) {
        total += price;
        txtCart.append(name + " - Rs " + price + "\n");
        lblTotal.setText("Total: Rs " + String.format("%.2f", total));
    }

    private void processPayment() {
        if (total <= 0) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, 
            "Payment Successful!\n\nTotal Amount: Rs " + String.format("%.2f", total), 
            "Success", JOptionPane.INFORMATION_MESSAGE);

        clearOrder();
    }

    private void clearOrder() {
        total = 0.0;
        txtCart.setText("");
        lblTotal.setText("Total: Rs 0.0");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OrderGUI());
    }
}
