package model;

public class Drink extends MenuSnackItem {
    public Drink(int id, String name, float price, int amountInStock) {
        super(id, name, price, amountInStock); // Inheritance
    }

    // Abstract from MenuItem
    @Override
    public String getDetails() {
        return String.format("Drink: %s - Rs %.2f", getName(), getPrice());
    }
}