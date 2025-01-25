package cz.cervenka.p2p_project.database.entities;

public class Account {

    private int id;
    private double balance;

    public Account(double balance, int id) {
        this.balance = balance;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
