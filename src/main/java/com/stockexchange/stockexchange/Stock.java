package com.stockexchange.stockexchange;

public class Stock {
    private final String ticker;
    private double price;

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
