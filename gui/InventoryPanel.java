package gui;

import dao.MenuSnackItemDAO;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.CanManageInventory;
import model.Drink;
import model.Food;
import model.MenuSnackItem;
import model.Staff;

public class InventoryPanel extends JPanel {

    private static final MenuSnackItemDAO dao = new MenuSnackItemDAO();
    private final Staff currentUser;

    private JTable table;
    private DefaultTableModel tableModel;

    public InventoryPanel(Staff currentUser) {
        this.currentUser = currentUser;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initTable();
        initButtons();

        refreshTableWithAll();
    }

    // Utility method to initialise table
    private void initTable() {
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Type", "Name", "Price", "Quantity"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    // Initialise buttons and attach event listeners
    private void initButtons() {
        JPanel panel = new JPanel();

        JButton createBtn = new JButton("Create");
        JButton findBtn = new JButton("Find");
        JButton refreshBtn = new JButton("Refresh");
        JButton updateBtn = new JButton("Restock");
        JButton deleteBtn = new JButton("Delete");

        panel.add(createBtn);
        panel.add(findBtn);
        panel.add(refreshBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);

        add(panel, BorderLayout.SOUTH);

        // Disables buttons for non-admin users
        if (currentUser.isAdmin() != 1) {
            createBtn.setEnabled(false);
            updateBtn.setEnabled(false);
            deleteBtn.setEnabled(false);
        }

        // Attaching event listeners
        createBtn.addActionListener(e -> handleCreate());
        findBtn.addActionListener(e -> handleFind());
        refreshBtn.addActionListener(e -> refreshTableWithAll());
        updateBtn.addActionListener(e -> handleUpdate());
        deleteBtn.addActionListener(e -> handleDelete());
    }

    // Handlers...

    private void handleCreate() {
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField qtyField = new JTextField();
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Food", "Drink"});

        Object[] msg = {
                "Type:", typeBox,
                "Name:", nameField,
                "Price:", priceField,
                "Quantity:", qtyField
        };

        if (JOptionPane.showConfirmDialog(this, msg, "Create Item",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

            try {
                String type = typeBox.getSelectedItem().toString();
                String name = nameField.getText().trim();
                float price = Float.parseFloat(priceField.getText().trim());
                int qty = Integer.parseInt(qtyField.getText().trim());

                if (name.isEmpty()) throw new IllegalArgumentException("Name empty");

                MenuSnackItem item = type.equals("Food")
                        ? new Food(0, name, price, qty)
                        : new Drink(0, name, price, qty);

                // Only if user implements the interface CanRestock
                if (currentUser instanceof CanManageInventory restocker) {
                    restocker.addMenuItem(item);
                }

                refreshTableWithAll();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void handleFind() {
        String name = JOptionPane.showInputDialog(this, "Enter name:");
        if (name == null || name.trim().isEmpty()) return;

        MenuSnackItem item = dao.getByName(name.trim());

        if (item == null) {
            JOptionPane.showMessageDialog(this, "Not found");
            return;
        }

        List<MenuSnackItem> list = new ArrayList<>();
        list.add(item);

        refreshTable(list);
    }

    private void handleUpdate() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String type = tableModel.getValueAt(row, 1).toString();
        String name = tableModel.getValueAt(row, 2).toString();
        float price = Float.parseFloat(tableModel.getValueAt(row, 3).toString());
        int qty = (int) tableModel.getValueAt(row, 4);

        // Restocking just updates the quantity

        // JTextField nameField = new JTextField(name);
        // JTextField priceField = new JTextField(String.valueOf(price));

        JTextField qtyField = new JTextField(String.valueOf(qty));
        Object[] msg = {
                // "Name:", nameField,
                // "Price:", priceField,
                "Quantity:", qtyField
        };

        if (JOptionPane.showConfirmDialog(this, msg, "Update Item",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

            try {
                // String newName = nameField.getText().trim();
                // float newPrice = Float.parseFloat(priceField.getText().trim());
                int newQty = Integer.parseInt(qtyField.getText().trim());

                MenuSnackItem item = type.equals("Food")
                        ? new Food(id, name, price, newQty)
                        : new Drink(id, name, price, newQty);

                // Only if user implements the interface CanRestock
                if (currentUser instanceof CanManageInventory restocker) {
                    System.out.println("Restocking menu item...");
                    restocker.restock(item, newQty);
                }

                refreshTableWithAll();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void handleDelete() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String type = tableModel.getValueAt(row, 1).toString();
        String name = tableModel.getValueAt(row, 2).toString();
        float price = Float.parseFloat(tableModel.getValueAt(row, 3).toString());
        int qty = (int) tableModel.getValueAt(row, 4);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete '" + name + "'?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        // Only if user implements the interface CanRestock
        if (confirm == JOptionPane.YES_OPTION) {
            if (currentUser instanceof CanManageInventory restocker) {
                MenuSnackItem item;
                if (type.equals("Food")) {
                    item = new Food(id, name, price, qty);
                } else {
                    item = new Drink(id, name, price, qty);
                }

                restocker.removeMenuItem(item);
            }

            refreshTableWithAll();
        }
    }

    // Utility method to feed data into table
    private void refreshTable(List<MenuSnackItem> items) {
        tableModel.setRowCount(0);

        for (MenuSnackItem i : items) {
            tableModel.addRow(new Object[]{
                    i.getId(),
                    i.getType(),
                    i.getName(),
                    i.getPrice(),
                    i.getAmountInStock()
            });
        }
    }

    private void refreshTableWithAll() {
        refreshTable(dao.getAll());
    }
}