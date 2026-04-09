package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import model.MenuItem;
import db.DB;

public class OrderGUI extends JFrame{
	
	private JPanel pnlMenu, pnlCart;
	private JTextArea txtCart;
	private JLabel lblTotal;
	
	private double total = 0.0;
	
	// COLORS
	private final Color BG = new Color(245, 245, 245);
	private final Color CARD = Color.white;
	private final Color PRIMARY = new Color(37, 99, 235);
	private final Color SUCCESS = new Color(22, 163, 74);
	private final Color DANGER = new Color(220, 38, 38);
	
	private final DB db;
	
	public OrderGUI() {
		this.db = new DB();
		
		setTitle("Fast Food POS");
		setLayout(new BorderLayout());
		getContentPane().setBackground(BG);
		
		add(createTopTabs(), BorderLayout.NORTH);
		add(createMenuPanel(), BorderLayout.CENTER);
		add(createCartPanel(), BorderLayout.EAST);
		
		setSize(1000, 550);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);		
	}
	
	// TOP CATEGORY TABS
	private JPanel createTopTabs() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		panel.setBackground(CARD);
		
		String[] tabs = {"Burgers", "Chicken", "Sides", "Drink", "Desserts"};
		
		for (String t : tabs) {
			JButton btn = new JButton(t);
			styleTab(btn);
			btn.addActionListener(e -> loadItems(t));
			panel.add(btn);
		}
		
		return panel;
	}
	
	private void styleTab(JButton btn) {
		btn.setFocusPainted(false);
		btn.setBackground(new Color(230, 230, 230));
		btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
		btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
	}
	
	// MENU GRID
	private JPanel createMenuPanel() {
		pnlMenu = new JPanel(new GridLayout(2, 3, 20, 20));
		pnlMenu.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		pnlMenu.setBackground(BG);
		
		loadItems("Burgers");
		
		return pnlMenu;
	}
	
	// CART PANEL
	private JPanel createCartPanel() {
		pnlCart = new JPanel(new BorderLayout());
		pnlCart.setPreferredSize(new Dimension(300, 0));
		pnlCart.setBackground(CARD);
		pnlCart.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		
		JLabel title = new JLabel("Current Order");
		title.setFont(new Font("Segoe UI", Font.BOLD, 18));
		
		txtCart = new JTextArea();
		txtCart.setEditable(false);
		txtCart.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtCart.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
		
		JScrollPane scroll = new JScrollPane(txtCart);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		
		JPanel bottom = new JPanel(new GridLayout(4, 1, 10, 10));
		bottom.setBackground(CARD);
		
		lblTotal = new JLabel("Total: Rs 0.0");
		lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
		
		JButton btnPay = new JButton("Checkout");
		stylePrimary(btnPay, DANGER);
		
		JButton btnClear = new JButton("Clear Order");
		stylePrimary(btnClear, DANGER);
		
		btnPay.addActionListener(e -> processPayment());
		btnClear.addActionListener(e -> clearOrder());
		
		bottom.add(lblTotal);
		bottom.add(btnPay);
		bottom.add(btnClear);
		
		pnlCart.add(title, BorderLayout.NORTH);
		pnlCart.add(scroll, BorderLayout.CENTER);
		pnlCart.add(bottom, BorderLayout.SOUTH);
		
		return pnlCart;
	}
	
	private void stylePrimary(JButton btn, Color color) {
		btn.setFocusPainted(false);
		btn.setBackground(color);
		btn.setForeground(Color.WHITE);
		btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btn.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
	}
	
	// LOAD ITEMS FROM DATABASE
	private void loadItems(String category) {
		pnlMenu.removeAll();
		
		try {
			List<MenuItem> items = db.getMenuItemsByCategory(category);
			
			if (items.isEmpty()) {
				JLabel empty = new JLabel("No items found in this category", SwingConstants.CENTER);
				pnlMenu.add(empty);
			} else {
				for (MenuItem item : items) {
					pnlMenu.add(createItemCard(item));
				}
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this,
					"Error loading menu items from database: " + ex.getMessage(),
					"Database Error", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
		
		pnlMenu.revalidate();
		pnlMenu.repaint();
	}
	
	// MODERN CARD
	private JPanel createItemCard(MenuItem item) {
		JPanel card = new JPanel(new BorderLayout());
		card.setBackground(CARD);
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(220, 220, 220)),
				BorderFactory.createEmptyBorder(15,15,15,15)
		));
		
		JLabel lblName = new JLabel(item.getName(), SwingConstants.CENTER);
		lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
		
		JLabel lblPrice = new JLabel("Rs " + item.getPrice(), SwingConstants.CENTER);
		lblPrice.setForeground(PRIMARY);
		lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 14));
		
		JButton btnAdd = new JButton("Add");
		stylePrimary(btnAdd, PRIMARY);		
		
		btnAdd.addActionListener(e -> addToCart(item));
		
		card.add(lblName, BorderLayout.NORTH);
		card.add(lblPrice, BorderLayout.CENTER);
		card.add(btnAdd, BorderLayout.SOUTH);
		
		return card;
	}
	
	// CART LOGIC
	private void addToCart(MenuItem item) {
		total += item.getPrice();
		txtCart.append(item.getName() + " - Rs " + item.getPrice() + "\n");
		lblTotal.setText("Total: Rs " + total);
	}
	
	private void processPayment() {
		JOptionPane.showMessageDialog(this, "Payment Successful!");
		clearOrder();
	}
	
	private void clearOrder() {
		total = 0;
		txtCart.setText("");
		lblTotal.setText("Total: Rs 0.0");
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new OrderGUI());
	}
}
