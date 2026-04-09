package model;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private final int cashierId;
    private final List<OrderItem> items = new ArrayList<>(); // Aggregation
    private Payment payment; // null until paid
    private boolean isPaid = false;

    public Order(int cashierId) {
        this.cashierId = cashierId;
    }

    // Getters
    public int getId() { return id; }
    public int getCashierId() { return cashierId; }
    public List<OrderItem> getItems() { return items; }
    public Payment getPayment() { return payment; }
    public boolean isPaid() { return isPaid; }

    // Setters
    public void setId(int id) { this.id = id; }

    // Add item to order
    public void addItem(MenuSnackItem item, int quantity) {
        items.add(new OrderItem(item, quantity));
    }

    // Set payment and mark as paid
    public void setPayment(Payment payment) {
        this.payment = payment;
        this.isPaid = true;
    }

    // Calculate total
    public float calculateTotal() {
        float total = 0;
        for (OrderItem oi : items) {
            total += oi.calculatePrice();
        }
        return total;
    }
}