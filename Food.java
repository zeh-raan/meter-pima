public class Food extends MenuItem {
    public Food(int itemId, String name, float price) {
        super(itemId, name, price);
    }

    public void displayItem() {
        System.out.println(getDetails());
    }

    public void updatePrice(float newPrice) {
        setPrice(newPrice);
    }
    // Abstract from MenuItem
    @Override
    public String getDetails() {
        return String.format("Food: %s - Rs %.2f", getName(), getPrice());
    }
}
