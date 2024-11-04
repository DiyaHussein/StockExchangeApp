package com.stockexchange.stockexchange;

public class Order {
    private String user;
    private String stock;
    private int quantity;
    private String type;  // basically buy or sell

    public Order(String user, String stock, int quantity, String type) {
        this.user = user;
        this.stock = stock;
        this.quantity = quantity;
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public String getStock() {
        return stock;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getType() {
        return type;
    }
}
