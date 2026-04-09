package gui;

import model.CashPayment;
import model.CardPayment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

public class PaymentGUI extends JFrame {

    private final float totalDue;
    private final DecimalFormat df = new DecimalFormat("0.00");

    private JRadioButton cashRadio;
    private JRadioButton cardRadio;

    private JPanel inputPanel;
    private CardLayout cardLayout;

    // Cash components
    private JTextField amountPaidField;
    private JLabel changeLabel;

    // Card components
    private JTextField cardNumberField;

    private JTextArea receiptArea;

    public PaymentGUI(float totalDue) {
        this.totalDue = totalDue;

        setTitle("Snack Bar - Payment");
        setSize(520, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ===== TOP =====
        JLabel totalLabel = new JLabel("Total Due: $" + df.format(totalDue));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(totalLabel, BorderLayout.NORTH);

        // ===== CENTER =====
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Payment method
        JPanel methodPanel = new JPanel();
        cashRadio = new JRadioButton("Cash", true);
        cardRadio = new JRadioButton("Card");

        ButtonGroup group = new ButtonGroup();
        group.add(cashRadio);
        group.add(cardRadio);

        methodPanel.add(new JLabel("Payment Method:"));
        methodPanel.add(cashRadio);
        methodPanel.add(cardRadio);

        centerPanel.add(methodPanel, BorderLayout.NORTH);

        // CardLayout panel
        cardLayout = new CardLayout();
        inputPanel = new JPanel(cardLayout);

        inputPanel.add(createCashPanel(), "CASH");
        inputPanel.add(createCardPanel(), "CARD");

        centerPanel.add(inputPanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Switch panels
        cashRadio.addActionListener(e -> cardLayout.show(inputPanel, "CASH"));
        cardRadio.addActionListener(e -> cardLayout.show(inputPanel, "CARD"));

        // ===== RECEIPT =====
        receiptArea = new JTextArea(8, 40);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        receiptArea.setBorder(BorderFactory.createTitledBorder("Receipt"));

        // ===== BUTTONS =====
        JButton payButton = new JButton("Confirm & Pay");
        JButton cancelButton = new JButton("Cancel");

        payButton.setFont(new Font("Arial", Font.BOLD, 14));

        payButton.addActionListener(e -> processPayment());
        cancelButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(payButton);
        buttonPanel.add(cancelButton);

        // ===== BOTTOM COMBINED =====
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(new JScrollPane(receiptArea), BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // ================= CASH PANEL =================
    private JPanel createCashPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Amount Paid ($):"), gbc);

        amountPaidField = new JTextField(12);
        amountPaidField.setText(df.format(totalDue));
        gbc.gridx = 1;
        panel.add(amountPaidField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;

        changeLabel = new JLabel("Change: $0.00", SwingConstants.CENTER);
        changeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        changeLabel.setForeground(new Color(0, 128, 0));

        panel.add(changeLabel, gbc);

        // Real-time change calculation
        amountPaidField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                try {
                    float amount = Float.parseFloat(amountPaidField.getText());
                    float change = amount - totalDue;
                    changeLabel.setText("Change: $" + df.format(Math.max(change, 0)));
                } catch (Exception ignored) {}
            }
        });

        return panel;
    }

    // ================= CARD PANEL =================
    private JPanel createCardPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Card Number:"), gbc);

        cardNumberField = new JTextField(20);
        cardNumberField.setText("4111-1111-1111-1111");
        gbc.gridx = 1;
        panel.add(cardNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;

        JLabel note = new JLabel("(Demo – no real processing)");
        note.setFont(new Font("Arial", Font.ITALIC, 11));

        panel.add(note, gbc);

        return panel;
    }

    // ================= PROCESS PAYMENT =================
    private void processPayment() {
        receiptArea.setText("");

        if (cashRadio.isSelected()) {
            processCash();
        } else {
            processCard();
        }
    }

    // ================= CASH LOGIC =================
    private void processCash() {
        try {
            float amountPaid = Float.parseFloat(amountPaidField.getText());

            CashPayment cash = new CashPayment(amountPaid);
            float change = cash.processPayment(totalDue);

            if (change < 0) {
                JOptionPane.showMessageDialog(this,
                        "Amount paid is less than total due!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            changeLabel.setText("Change: $" + df.format(change));

            String receipt =
                    "=== SNACK BAR RECEIPT ===\n\n" +
                            "Total Due     : $" + df.format(totalDue) + "\n" +
                            "Cash Paid     : $" + df.format(amountPaid) + "\n" +
                            "Change        : $" + df.format(change) + "\n\n" +
                            "Payment Type  : CASH\n" +
                            "Status        : SUCCESSFUL\n" +
                            "=========================";

            receiptArea.setText(receipt);

            JOptionPane.showMessageDialog(this,
                    "Payment successful!\nChange: $" + df.format(change));

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Enter valid amount!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= CARD LOGIC =================
    private void processCard() {
        String cardNum = cardNumberField.getText().trim();

        if (!cardNum.matches("\\d{4}-\\d{4}-\\d{4}-\\d{4}")) {
            JOptionPane.showMessageDialog(this,
                    "Invalid card format!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        CardPayment card = new CardPayment(cardNum, totalDue);
        float result = card.processPayment(totalDue);

        if (result >= 0) {
            String receipt =
                    "=== SNACK BAR RECEIPT ===\n\n" +
                            "Total Due     : $" + df.format(totalDue) + "\n" +
                            "Payment Type  : CARD\n" +
                            "Card          : " + cardNum + "\n" +
                            "Status        : SUCCESSFUL\n" +
                            "=========================";

            receiptArea.setText(receipt);

            JOptionPane.showMessageDialog(this,
                    "Card payment successful!");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Card payment failed!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= MAIN =================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new PaymentGUI(24.50f).setVisible(true)
        );
    }
}