// MenuItem is abstract because there won't ever be a plain MenuItem object,
// only Food and Drink objects
package model;

// Had to name it to this because apparently there's something called
// MenuItem in java.awt
public abstract class MenuSnackItem {
    private int id;
    private String name;
    private float price;
    private int amountInStock;

    public abstract String getDetails(); // Abstract

    public MenuSnackItem(int id, String name, float price, int amountInStock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.amountInStock = amountInStock;
    }

    public int getId()  {
        return this.id;
    }

    public String getName() { 
        return this.name;
    }

    public float getPrice() { 
        return this.price;
    }

    public int getAmountInStock() {
        return this.amountInStock;
    }

    // Used when storing in database
    public String getType() {
        return this.getClass().getSimpleName();
    }

    public void setName(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name; 
        }
    }

    public void setPrice(float price) { 
        if (price > 0) {
            this.price = price; 
        }
    }

    public Boolean isMakable() {
        return this.amountInStock > 0; // Will return false if not enough in stock
    }

    // This is for the restocking logic
    public void restock(int amount) {
        this.amountInStock = amount;
    }
}