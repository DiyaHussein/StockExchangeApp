package com.stockexchange.stockexchange;

public class Order {
    private User user;
    private String stock;
    private int quantity;
    private StockAction intention;  // basically buy or sell

    public Order(User user, String stock, int quantity, StockAction intention) {
        this.user = user;
        this.stock = stock;
        this.quantity = quantity;
        this.intention = intention;
    }

    public User getUser() {
        return user;
    }

    public String getStock() {
        return stock;
    }

    public int getQuantity() {
        return quantity;
    }

    public StockAction getIntention() { return intention; }

}