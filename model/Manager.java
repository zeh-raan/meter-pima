package model;

import dao.MenuSnackItemDAO;

public class Manager extends Staff implements CanManageInventory {
    public Manager(int id, String name, String password) {
        super(id, name, password, 1);
    }

    @Override
    public void addMenuItem(MenuSnackItem item) {
        new MenuSnackItemDAO().create(item);
    }
    
    @Override
    public void restock(MenuSnackItem item, int amount) {
        if (amount <= 0) return;

        // Restocking logic
        item.restock(amount); 
        new MenuSnackItemDAO().update(item); // Reflect change in database
    }

    @Override
    public void removeMenuItem(MenuSnackItem item) {
        new MenuSnackItemDAO().delete(item.getId());
    }
}