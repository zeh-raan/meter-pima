public class Food extends MenuItem {
    public Food(int itemId, String name, float price) {
        super(itemId, name, price);
    }

    // Abstract from MenuItem
    @Override
    public String getDetails() {
        return String.format("Food: %s - Rs %.2f", getName(), getPrice());
    }
}