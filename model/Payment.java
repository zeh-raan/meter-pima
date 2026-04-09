package model;

public abstract class Payment {
    private final float amount;  // Amount paid or to be paid

    public Payment(float amount) {
        this.amount = amount;
    }

    public float getAmount() { return amount; }

    // returns change or 0
    public abstract float processPayment(float priceToBePaid); // Abstract
}