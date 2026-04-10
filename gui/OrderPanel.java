package gui;

import dao.MenuSnackItemDAO;
import dao.OrderDAO;
import model.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderPanel extends JPanel {

    private final Staff cashier;
    private final MenuSnackItemDAO menuDAO = new MenuSnackItemDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    private JTable menuTable;
    private DefaultTableModel menuTableModel;

    private JTable cartTable;
    private DefaultTableModel cartTableModel;

    private List<OrderItem> cart = new ArrayList<>();

    private JLabel totalLabel;
    private JLabel statusLabel;

    private JButton addBtn, removeBtn, clearCartBtn, checkoutBtn;

    public OrderPanel(Staff cashier) {
        this.cashier = cashier;

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));  // Main panel padding
        setBackground(new Color(250, 250, 252));

        initComponents();
        refreshMenuTable();
        refreshCartTable();
    }

    private void initComponents() {
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 245, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JLabel titleLabel = new JLabel("New Order", JLabel.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 60, 120));

        statusLabel = new JLabel("Cashier: " + cashier.getName() + "   |   Items in cart: 0");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(Color.DARK_GRAY);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);

        // Menu Panel (Left)
        JPanel menuPanel = createTitledPanel("Available Menu Items");
        menuPanel.setPreferredSize(new Dimension(380, 0));

        menuTableModel = new DefaultTableModel(new String[]{"Item Name", "Price (Rs)"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        menuTable = new JTable(menuTableModel);
        styleTable(menuTable);

        JScrollPane menuScroll = new JScrollPane(menuTable);
        menuScroll.setBorder(BorderFactory.createEmptyBorder());

        menuPanel.add(menuScroll, BorderLayout.CENTER);

        // Cart Panel (Center)
        JPanel cartPanel = createTitledPanel("Shopping Cart");

        cartTableModel = new DefaultTableModel(new String[]{"Item", "Qty", "Unit Price", "Total"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        cartTable = new JTable(cartTableModel);
        styleTable(cartTable);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        cartTable.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        cartTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        JScrollPane cartScroll = new JScrollPane(cartTable);

        totalLabel = new JLabel("Total: Rs 0.00", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalLabel.setForeground(new Color(0, 100, 200));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 20));

        cartPanel.add(cartScroll, BorderLayout.CENTER);
        cartPanel.add(totalLabel, BorderLayout.SOUTH);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(getBackground());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        addBtn = createStyledButton("Add to Cart", new Color(40, 167, 69));
        removeBtn = createStyledButton("Remove Selected", new Color(220, 53, 69));
        clearCartBtn = createStyledButton("Clear Cart", new Color(108, 117, 125));
        checkoutBtn = createStyledButton("Checkout", new Color(0, 123, 255));

        checkoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        checkoutBtn.setPreferredSize(new Dimension(160, 48));

        buttonPanel.add(addBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(clearCartBtn);
        buttonPanel.add(checkoutBtn);

        // Action Listeners
        addBtn.addActionListener(e -> handleAddToCart());
        removeBtn.addActionListener(e -> handleRemoveFromCart());
        clearCartBtn.addActionListener(e -> handleClearCart());
        checkoutBtn.addActionListener(e -> openCheckoutDialog());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, menuPanel, cartPanel);
        splitPane.setResizeWeight(0.42);
        splitPane.setOneTouchExpandable(true);
        splitPane.setBorder(null);

        add(headerPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);

        // Clean compound border: outer line + inner padding
        Border line = BorderFactory.createLineBorder(new Color(215, 220, 230), 1);
        Border padding = BorderFactory.createEmptyBorder(15, 15, 15, 15);
        panel.setBorder(BorderFactory.createCompoundBorder(line, padding));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(40, 60, 100));

        panel.add(titleLabel, BorderLayout.NORTH);
        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(11, 22, 11, 22));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
        });
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(200, 220, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(235, 235, 240));
        table.setShowGrid(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(245, 247, 252));
        header.setForeground(new Color(50, 60, 90));
        header.setReorderingAllowed(false);
    }

    // ==================== Original Logic (Unchanged) ====================

    private void handleAddToCart() {
        int row = menuTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a menu item first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = menuTableModel.getValueAt(row, 0).toString();
        MenuSnackItem item = menuDAO.getByName(name);
        if (item == null) return;

        String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity for " + name + ":", "Quantity", JOptionPane.PLAIN_MESSAGE);
        if (qtyStr == null || qtyStr.trim().isEmpty()) return;

        int qty;
        try {
            qty = Integer.parseInt(qtyStr.trim());
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive number!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (qty > item.getAmountInStock()) {
            JOptionPane.showMessageDialog(this, "Not enough stock! Available: " + item.getAmountInStock(),
                    "Stock Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        cart.add(new OrderItem(item, qty));
        refreshCartTable();
    }

    private void handleRemoveFromCart() {
        int row = cartTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item from the cart first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cart.remove(row);
        refreshCartTable();
    }

    private void handleClearCart() {
        if (cart.isEmpty()) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Clear all items from cart?", "Confirm Clear", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            cart.clear();
            refreshCartTable();
        }
    }

    private void openCheckoutDialog() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        new CheckoutGUI(this, new ArrayList<>(cart), cashier, menuDAO, orderDAO);
    }

    public void clearCartAfterCheckout() {
        cart.clear();
        refreshCartTable();
        refreshMenuTable();
    }

    private void refreshMenuTable() {
        menuTableModel.setRowCount(0);
        List<MenuSnackItem> menuItems = menuDAO.getAll();
        for (MenuSnackItem item : menuItems) {
            if (item.getAmountInStock() > 0) {
                menuTableModel.addRow(new Object[]{
                        item.getName(),
                        String.format("%.2f", item.getPrice())
                });
            }
        }
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        float total = 0;
        DecimalFormat df = new DecimalFormat("#,##0.00");

        for (OrderItem o : cart) {
            MenuSnackItem item = o.getItem();
            float price = item.getPrice();
            float itemTotal = price * o.getQuantity();

            cartTableModel.addRow(new Object[]{
                    item.getName(),
                    o.getQuantity(),
                    df.format(price),
                    df.format(itemTotal)
            });
            total += itemTotal;
        }

        totalLabel.setText("Total: Rs " + df.format(total));
        statusLabel.setText("Cashier: " + cashier.getName() + "   |   Items in cart: " + cart.size());
    }
}