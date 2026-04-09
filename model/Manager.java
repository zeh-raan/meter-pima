package model;

import dao.MenuSnackItemDAO;

public class Manager extends Staff implements CanRestock {
    public Manager(int id, String name, String password) {
        super(id, name, password, 1); // Inheritance
    }

    // TODO: Make this better fit logic-wise
    // Interface
    @Override
    public void restock(MenuSnackItem item, int amount) {
        if (amount <= 0) return;

        // Restocking logic
        item.restock(amount); 
        new MenuSnackItemDAO().update(item); // Reflect change in database
    }
}