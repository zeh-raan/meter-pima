package gui;

import dao.MenuSnackItemDAO;
import dao.OrderDAO;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.*;

public class OrderPanel extends JPanel {

    private final Staff cashier;
    private final MenuSnackItemDAO menuDAO = new MenuSnackItemDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    private JTable menuTable;
    private DefaultTableModel menuTableModel;

    private JTable cartTable;
    private DefaultTableModel cartTableModel;

    private List<OrderItem> cart = new ArrayList<>();

    public OrderPanel(Staff cashier) {
        this.cashier = cashier;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initMenuTable();
        initCartTable();
        initButtons();

        refreshMenuTable();
        refreshCartTable();
    }

    //  Menu table 
    private void initMenuTable() {
        menuTableModel = new DefaultTableModel(new String[]{"Name", "Price"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        menuTable = new JTable(menuTableModel);
        JScrollPane scroll = new JScrollPane(menuTable);
        scroll.setPreferredSize(new Dimension(300, 0));
        add(scroll, BorderLayout.WEST);
    }

    //  Cart table 
    private void initCartTable() {
        cartTableModel = new DefaultTableModel(new String[]{"Name", "Qty", "Price", "Total"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        cartTable = new JTable(cartTableModel);
        JScrollPane scroll = new JScrollPane(cartTable);
        add(scroll, BorderLayout.CENTER);
    }

    //  Bottom buttons 
    private void initButtons() {
        JPanel btnPanel = new JPanel();

        JButton addBtn = new JButton("Add to Order");
        JButton removeBtn = new JButton("Remove from Order");
        JButton checkoutBtn = new JButton("Checkout");

        btnPanel.add(addBtn);
        btnPanel.add(removeBtn);
        btnPanel.add(checkoutBtn);

        add(btnPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> handleAddToCart());
        removeBtn.addActionListener(e -> handleRemoveFromCart());
        checkoutBtn.addActionListener(e -> handleCheckout());
    }

    //  Add to cart 
    private void handleAddToCart() {
        int row = menuTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a menu item first!"); return; }

        String name = menuTableModel.getValueAt(row, 0).toString();
        MenuSnackItem item = menuDAO.getByName(name);
        if (item == null) return;

        String qtyStr = JOptionPane.showInputDialog(this, "Quantity:");
        if (qtyStr == null || qtyStr.isEmpty()) return;

        int qty;
        try { qty = Integer.parseInt(qtyStr); }
        catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Invalid quantity!"); return; }

        if (qty > item.getAmountInStock()) {
            JOptionPane.showMessageDialog(this, "Not enough stock!");
            return;
        }

        cart.add(new OrderItem(item, qty));
        refreshCartTable();
    }

    //  Remove from cart 
    private void handleRemoveFromCart() {
        int row = cartTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a cart item first!"); return; }
        cart.remove(row);
        refreshCartTable();
    }

    //  Checkout 
    private void handleCheckout() {
        if (cart.isEmpty()) { JOptionPane.showMessageDialog(this, "Cart is empty!"); return; }

        float total = 0;
        for (OrderItem o : cart) total += o.calculatePrice();

        // Payment method
        String[] options = {"Cash", "Card"};
        int method = JOptionPane.showOptionDialog(
                this,
                "Total: Rs " + total + "\nSelect payment method",
                "Payment",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );
        if (method == -1) return;

        Payment payment;

        if (options[method].equals("Cash")) {
            String amtStr = JOptionPane.showInputDialog(this, "Enter cash given:");
            if (amtStr == null || amtStr.isEmpty()) return;

            float cashGiven;
            try { cashGiven = Float.parseFloat(amtStr); }
            catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Invalid amount!"); return; }

            CashPayment cash = new CashPayment(cashGiven);
            float change = cash.processPayment(total);
            if (change < 0) { JOptionPane.showMessageDialog(this, "Insufficient cash!"); return; }

            payment = cash;
            JOptionPane.showMessageDialog(this, "Payment successful! Change: Rs " + change);

        } else { // Card
            CardPayment card = new CardPayment(total); // exact amount, no card ID needed
            card.processPayment(total);
            payment = card;
            JOptionPane.showMessageDialog(this, "Payment successful via card!");
        }

        // Save order and update stock
        Order order = new Order(cashier.getId());
        order.setPayment(payment);

        for (OrderItem o : cart) {
            order.addItem(o.getItem(), o.getQuantity());

            // Update stock in DB
            MenuSnackItem item = o.getItem();
            int newStock = item.getAmountInStock() - o.getQuantity();
            item.restock(newStock);
            menuDAO.update(item);
        }

        orderDAO.create(order);

        cart.clear();
        refreshCartTable();
        refreshMenuTable();
    }

    //  Refresh menu 
    private void refreshMenuTable() {
        menuTableModel.setRowCount(0);
        List<MenuSnackItem> menuItems = menuDAO.getAll();
        for (MenuSnackItem item : menuItems) {
            if (item.getAmountInStock() > 0) {
                menuTableModel.addRow(new Object[]{item.getName(), item.getPrice()});
            }
        }
    }

    //  Refresh cart 
    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        for (OrderItem o : cart) {
            MenuSnackItem item = o.getItem();
            int qty = o.getQuantity();
            float price = item.getPrice();
            cartTableModel.addRow(new Object[]{
                    item.getName(),
                    qty,
                    price,
                    price * qty
            });
        }
    }
}