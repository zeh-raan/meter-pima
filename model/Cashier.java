package model;

public class Cashier extends Staff {
    public Cashier(int id, String name, String password) {
        super(id, name, password, 0); // Inheritance
    }
}