import java.util.ArrayList;
import java.util.List;

public class Manager extends Staff implements CanRestock {
    private final List<MenuItem> menuItems;
    
    public Manager(int staffId, String name) {
        super(staffId, name);
        menuItems = new ArrayList<>();
    }

    @Override
    public void restock(Ingredient ingredient, int amount) {
        if (amount <= 0) return;
        ingredient.restock(amount); // Restocking logic
    }

    // ***** Menu item CRUD *****

    // Add
    public boolean addMenuItem(MenuItem item) {
        if (item == null) return false; // Null

        // Account for duplicate item ID
        for (MenuItem i : menuItems) {
            if (i.getItemId() == item.getItemId()) {
                return false;
            }
        }

        menuItems.add(item);
        return true;
    }
    // Update
    public boolean updateMenuItem(int id, String newName, float newPrice) {
        for (MenuItem item : menuItems) {
            if (item.getItemId() == id) {

                // Update valid name
                if (newName != null && !newName.isEmpty()) {
                    item.setName(newName);
                }
                
                // Update valid price
                if (newPrice > 0) {
                    item.setPrice(newPrice);
                }

                return true;
            }
        }
        return false;
    }
    // Delete
    public boolean deleteMenuItem(int id) {
        return menuItems.removeIf(item -> item.getItemId() == id);
    }
}