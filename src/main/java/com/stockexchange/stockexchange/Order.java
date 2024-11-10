package com.stockexchange.stockexchange;

import java.util.concurrent.atomic.AtomicLong;

public class Order {
    private static final AtomicLong idCounter = new AtomicLong(1);
    private final long id;
    private User user;
    private String stock;
    private int quantity;
    private double price;
    private StockAction intention;  // basically buy or sell

    public Order(User user, String stock, int quantity, double price, StockAction intention) {
        this.id = idCounter.getAndIncrement();
        this.user = user;
        this.stock = stock;
        this.quantity = quantity;
        this.price = price;
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

    public double getPrice() {
        return price;
    }

    public long getId() {
        return id;
    }

    public StockAction getIntention() { return intention; }

    public void reduceQuantity(int tradeQuantity) {
        if (tradeQuantity < 0) {
            throw new IllegalArgumentException("Trade quantity must be non-negative.");
        }

        if (tradeQuantity > this.quantity) {
            throw new IllegalArgumentException("Trade quantity exceeds available order quantity.");
        }

        this.quantity -= tradeQuantity;
    }


    public boolean matches(Order otherOrder) {
        // Check if both orders are for the same stock
        if (!this.stock.equalsIgnoreCase(otherOrder.stock)) {
            return false;
        }

        // Check if buy order's price is >= sell order's price
        if (this.intention == StockAction.BUY && otherOrder.intention == StockAction.SELL) {
            return this.price >= otherOrder.price;
        } else if (this.intention == StockAction.SELL && otherOrder.intention == StockAction.BUY) {
            return otherOrder.price >= this.price;
        }

        // If both are the same intention, they don't match (e.g., both are BUY or both are SELL)
        return false;
    }

}