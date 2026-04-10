package gui;

import dao.MenuSnackItemDAO;
import dao.OrderDAO;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class CheckoutGUI extends JDialog {

    private final OrderPanel orderPanel;
    private final List<OrderItem> cart;
    private final Staff cashier;
    private final MenuSnackItemDAO menuDAO;
    private final OrderDAO orderDAO;

    public CheckoutGUI(OrderPanel orderPanel, List<OrderItem> cart, Staff cashier,
                       MenuSnackItemDAO menuDAO, OrderDAO orderDAO) {
        super((Frame) SwingUtilities.getWindowAncestor(orderPanel), "Checkout", true);

        this.orderPanel = orderPanel;
        this.cart = cart;
        this.cashier = cashier;
        this.menuDAO = menuDAO;
        this.orderDAO = orderDAO;

        setSize(440, 340);
        setLocationRelativeTo(orderPanel);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        initCheckoutUI();
    }

    private void initCheckoutUI() {
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(250, 250, 252));

        float total = 0;
        for (OrderItem o : cart) total += o.calculatePrice();

        DecimalFormat df = new DecimalFormat("#,##0.00");

        JLabel titleLabel = new JLabel("Checkout", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(30, 60, 120));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 15, 0));

        JLabel totalLabel = new JLabel("Total Amount: Rs " + df.format(total), JLabel.CENTER);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(new Color(0, 100, 200));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.add(totalLabel);

        add(titleLabel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);

        // Payment logic (original, unchanged)
        String[] options = {"Cash", "Card"};
        int method = JOptionPane.showOptionDialog(this,
                "Total: Rs " + df.format(total) + "\n\nSelect payment method:",
                "Payment Method",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        if (method == -1) {
            dispose();
            return;
        }

        Payment payment = null;

        if (options[method].equals("Cash")) {
            String amtStr = JOptionPane.showInputDialog(this,
                    "Enter cash given (Rs):", "Cash Payment", JOptionPane.PLAIN_MESSAGE);

            if (amtStr == null || amtStr.trim().isEmpty()) {
                dispose();
                return;
            }

            float cashGiven;
            try {
                cashGiven = Float.parseFloat(amtStr.trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount!", "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }

            CashPayment cash = new CashPayment(cashGiven);
            float change = cash.processPayment(total);

            if (change < 0) {
                JOptionPane.showMessageDialog(this, "Insufficient cash!", "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }

            payment = cash;
            JOptionPane.showMessageDialog(this,
                    "Payment successful!\nChange: Rs " + df.format(change),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

        } else {
            CardPayment card = new CardPayment(total);
            card.processPayment(total);
            payment = card;
            JOptionPane.showMessageDialog(this, "Payment successful via Card!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }

        // Original checkout logic - unchanged
        Order order = new Order(cashier.getId());
        order.setPayment(payment);

        for (OrderItem o : cart) {
            order.addItem(o.getItem(), o.getQuantity());

            MenuSnackItem item = o.getItem();
            int newStock = item.getAmountInStock() - o.getQuantity();
            item.restock(newStock);
            menuDAO.update(item);
        }

        orderDAO.create(order);

        JOptionPane.showMessageDialog(this,
                "Order completed successfully!\nOrder ID: " + order.getId(),
                "Transaction Complete", JOptionPane.INFORMATION_MESSAGE);

        orderPanel.clearCartAfterCheckout();
        dispose();
    }
}