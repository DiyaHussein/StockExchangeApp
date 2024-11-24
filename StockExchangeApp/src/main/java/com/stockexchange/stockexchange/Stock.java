package com.stockexchange.stockexchange;

public class Stock {
    private final String ticker;
    private double price; // might be removed in the future
    // otherwise we can add another thread that updates the prices that the stock are trading at
    // every now and then (once per second for example)
    // make it configurable

    public Stock(String ticker, double price) {
        this.ticker = ticker;
        this.price = price;
    }

    public String getTicker() {
        return ticker;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
