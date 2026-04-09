package model;

public class OrderItem {
    private final MenuSnackItem item; // Composition
    private final int quantity;

    public OrderItem(MenuSnackItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public MenuSnackItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public float calculatePrice() {
        return item.getPrice() * quantity;
    }
}