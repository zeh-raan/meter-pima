package gui;

import dao.MenuDAO;
import model.MenuItem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class OrderGUI extends JFrame {

    private JPanel pnlMenu;
    private JTextArea txtCart;
    private JLabel lblTotal;

    private double total = 0.0;
    private final MenuDAO menuDAO;

    public OrderGUI() {
        this.menuDAO = new MenuDAO();

        setTitle("Fast Food Order System");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(createTopPanel(), BorderLayout.NORTH);
        add(createMenuPanel(), BorderLayout.CENTER);
        add(createCartPanel(), BorderLayout.EAST);

        setSize(950, 580);
        setLocationRelativeTo(null);
        setVisible(true);

        loadMenuItems();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Refresh Menu");
        btnRefresh.addActionListener(e -> loadMenuItems());
        panel.add(btnRefresh);
        return panel;
    }

    private JPanel createMenuPanel() {
        pnlMenu = new JPanel(new GridLayout(0, 3, 12, 12));
        return pnlMenu;
    }

    private JPanel createCartPanel() {
        JPanel pnlCart = new JPanel(new BorderLayout(5, 5));

        JLabel title = new JLabel("Current Cart", JLabel.CENTER);
        title.setFont(new Font("Dialog", Font.BOLD, 16));

        txtCart = new JTextArea(18, 28);
        txtCart.setEditable(false);
        JScrollPane scroll = new JScrollPane(txtCart);

        lblTotal = new JLabel("Total: Rs 0.00", JLabel.CENTER);
        lblTotal.setFont(new Font("Dialog", Font.BOLD, 16));

        JButton btnClear = new JButton("Clear Cart");

        btnClear.addActionListener(e -> clearCart());

        JPanel bottom = new JPanel(new GridLayout(2, 1, 8, 8));
        bottom.add(lblTotal);
        bottom.add(btnClear);

        pnlCart.add(title, BorderLayout.NORTH);
        pnlCart.add(scroll, BorderLayout.CENTER);
        pnlCart.add(bottom, BorderLayout.SOUTH);

        return pnlCart;
    }

    private void loadMenuItems() {
        pnlMenu.removeAll();

        List<MenuItem> items = menuDAO.getAllMenuItems();

        if (items.isEmpty()) {
            pnlMenu.add(new JLabel("No items available in the menu.", JLabel.CENTER));
        } else {
            for (MenuItem item : items) {
                // Directly create panel without separate createMenuItemCard method
                JPanel itemPanel = new JPanel(new BorderLayout(5, 5));

                JLabel nameLabel = new JLabel(item.getName(), JLabel.CENTER);
                JLabel priceLabel = new JLabel("Rs " + item.getPrice(), JLabel.CENTER);

                JButton btnAdd = new JButton("Add to Cart");
                btnAdd.addActionListener(e -> addToCart(item));

                itemPanel.add(nameLabel, BorderLayout.NORTH);
                itemPanel.add(priceLabel, BorderLayout.CENTER);
                itemPanel.add(btnAdd, BorderLayout.SOUTH);

                pnlMenu.add(itemPanel);
            }
        }

        pnlMenu.revalidate();
        pnlMenu.repaint();
    }

    private void addToCart(MenuItem item) {
        total += item.getPrice();
        txtCart.append(item.getName() + "   Rs " + item.getPrice() + "\n");
        lblTotal.setText("Total: Rs " + String.format("%.2f", total));
    }

    private void clearCart() {
        total = 0.0;
        txtCart.setText("");
        lblTotal.setText("Total: Rs 0.00");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OrderGUI());
    }
}