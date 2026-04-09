package model;

public class CashPayment extends Payment {
    public CashPayment(float cashGiven) {
        super(cashGiven); // Inheritance
    }

    // Abstract from Payment
    @Override
    public float processPayment(float priceToBePaid) {
        if (getAmount() < priceToBePaid) return -1; // insufficient cash
        return getAmount() - priceToBePaid; // return change
    }
}