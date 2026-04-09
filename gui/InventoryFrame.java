
import dao.IngredientDAO;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Ingredient;

public class InventoryFrame extends JFrame {
    private static final IngredientDAO dao = new IngredientDAO();

    private JTable table;
    private DefaultTableModel tableModel;

    // Utility method to refresh table with lastest snapshot of table
    private void refreshTable(List<Ingredient> ingredients) {

        // Feeds table with given ingredients
        tableModel.setRowCount(0);
        for (Ingredient i : ingredients) {
            tableModel.addRow(new Object[]{
                i.getIngredientId(),
                i.getName(),
                i.getAmountInStock()
            });
        }
    }

    private void refreshTableWithAll() {
        List<Ingredient> ingredients = dao.getAll();
        refreshTable(ingredients);
    }

    public InventoryFrame() {
        setTitle("Inventory Management");
        setSize(600, 400);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialising the table
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Quantity"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER); // Adds the table to the GUI

        // List of operations/buttons
        JPanel buttonPanel = new JPanel();

        JButton createBtn = new JButton("Create");
        JButton findBtn = new JButton("Find");
        JButton refreshBtn = new JButton("Refresh");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");

        buttonPanel.add(createBtn);
        buttonPanel.add(findBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // ----------------------
        //   Attaching handlers
        // ----------------------

        // Handler for "CREATE" button
        class CreateButtonHandler implements ActionListener {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField nameField = new JTextField();
                JTextField qtyField = new JTextField();

                Object[] message = {
                    "Name:", nameField,
                    "Quantity:", qtyField
                };

                int option = JOptionPane.showConfirmDialog(
                    InventoryFrame.this, // References the JFrame
                    message,
                    "Create Ingredient",
                    JOptionPane.OK_CANCEL_OPTION
                );

                if (option == JOptionPane.OK_OPTION) {
                    try {
                        String name = nameField.getText().trim();
                        int qty = Integer.parseInt(qtyField.getText().trim());

                        if (name.isEmpty()) {
                            throw new IllegalArgumentException("Name cannot be empty");
                        }

                        dao.create(new Ingredient(0, name, qty)); // Id will be autoincremented
                        refreshTableWithAll();

                    } catch ( IllegalArgumentException ex ) {
                        JOptionPane.showMessageDialog(
                            InventoryFrame.this, "Invalid input: " + ex.getMessage()
                        );
                    }
                }
            }

        }

        CreateButtonHandler createBtnHandler = new CreateButtonHandler(); 
        createBtn.addActionListener(createBtnHandler);

        // Handler for "FIND" button
        class FindButtonHandler implements ActionListener {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField nameField = new JTextField();
                Object[] message = {
                    "Name:", nameField,
                };

                int option = JOptionPane.showConfirmDialog(
                    InventoryFrame.this, // References the JFrame
                    message,
                    "Find Ingredient",
                    JOptionPane.OK_CANCEL_OPTION
                );

                if (option == JOptionPane.OK_OPTION) {
                    try {
                        String name = nameField.getText().trim();
                        if (name.isEmpty()) {
                            throw new IllegalArgumentException("Name cannot be empty");
                        }

                        // Finds ingredient by name
                        Ingredient foundByName = dao.getByName(name);
                        if (foundByName == null) return;

                        List<Ingredient> ingredients = new ArrayList<>();
                        ingredients.add(foundByName);

                        refreshTable(ingredients);

                    } catch ( IllegalArgumentException ex ) {
                        JOptionPane.showMessageDialog(
                            InventoryFrame.this, "Invalid input: " + ex.getMessage()
                        );
                    }
                }
            }

        }

        FindButtonHandler findBtnHandler = new FindButtonHandler();
        findBtn.addActionListener(findBtnHandler);

        // Handler for "REFRESH" button
        class RefreshButtonHandler implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTableWithAll();
            }
        }

        RefreshButtonHandler refreshBtnHandler = new RefreshButtonHandler();
        refreshBtn.addActionListener(refreshBtnHandler);

        // Handler for "UPDATE" button
        class UpdateButtonHandler implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {

                // User must first click on a row to select it
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(InventoryFrame.this, "Select a row first");
                    return;
                }

                int id = (int) tableModel.getValueAt(row, 0);
                String name = tableModel.getValueAt(row, 1).toString();
                int qty = (int) tableModel.getValueAt(row, 2);

                JTextField nameField = new JTextField(name);
                JTextField qtyField = new JTextField(String.valueOf(qty));

                Object[] message = {
                    "Name:", nameField,
                    "Quantity:", qtyField
                };

                int option = JOptionPane.showConfirmDialog(
                    InventoryFrame.this,
                    message,
                    "Update Ingredient",
                    JOptionPane.OK_CANCEL_OPTION
                );

                if (option == JOptionPane.OK_OPTION) {
                    try {
                        String newName = nameField.getText().trim();
                        int newQty = Integer.parseInt(qtyField.getText().trim());

                        if (newName.isEmpty()) {
                            throw new IllegalArgumentException("Name cannot be empty");
                        }

                        dao.update(new Ingredient(id, newName, newQty));
                        refreshTableWithAll();

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(InventoryFrame.this, "Invalid input: " + ex.getMessage());
                    }
                }
            }

        }

        UpdateButtonHandler updateBtnHandler = new UpdateButtonHandler();
        updateBtn.addActionListener(updateBtnHandler);

        // Handler for "DELETE" button
        class DeleteButtonHandler implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(InventoryFrame.this, "Select a row first");
                    return;
                }

                int id = (int) tableModel.getValueAt(row, 0);
                String name = tableModel.getValueAt(row, 1).toString();

                int confirm = JOptionPane.showConfirmDialog(
                    InventoryFrame.this,
                    "Delete '" + name + "'?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    dao.delete(id);
                    refreshTableWithAll();
                }
            }
        }

        DeleteButtonHandler deleteBtnHanlder = new DeleteButtonHandler();
        deleteBtn.addActionListener(deleteBtnHanlder);

        // First time the JFrame is loaded, show all ingredients
        refreshTableWithAll();
    }
}