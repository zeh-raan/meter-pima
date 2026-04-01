public class Drink extends MenuItem {
    public Drink(int itemId, String name, float price) {
        super(itemId, name, price);
    }

    // Abstract from MenuItem
    @Override
    public String getDetails() {
        return String.format("Drink: %s - Rs %.2f", getName(), getPrice());
    }
}