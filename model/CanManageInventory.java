// This is an interface so that then the ability to restock an item will not
// be hardcoded to certain classes. For example, other roles can be added
// later on (i.e. Warehouse worker) that can just implement the restocking
// behaviour
package model;

public interface CanManageInventory {
    public void addMenuItem(MenuSnackItem item);
    public void restock(MenuSnackItem item, int amount);
    public void removeMenuItem(MenuSnackItem item);
}