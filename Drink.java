public class Drink extends MenuItem {
    public Drink(int itemId, String name, float price) {
        super(itemId, name, price);
    }

    // Updates the price through the parent setter (which validates price > 0)
    public void updatePrice(float newPrice) {
        setPrice(newPrice);
    }

    // Abstract from MenuItem
    @Override
    public String getDetails() {
        return String.format("Drink: %s - Rs %.2f", getName(), getPrice());
    }
}
