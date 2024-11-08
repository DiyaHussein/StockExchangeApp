package com.stockexchange.stockexchange;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StockMarket {
    private List<Order> orders; // List to store all active orders
    private static final String[] STOCK_TICKERS = {"AAPL", "MSFT", "AMZN", "GOOGL", "TSLA", "FB", "NFLX"};
    private static final Random random = new Random();
    private final UserDatabase userDatabase;

    public StockMarket(UserDatabase userDatabase) {
        this.orders = new ArrayList<>();
        this.userDatabase = userDatabase;
    }

    // Method to add a new order to the stock market
    public void addOrder(Order order) {
        orders.add(order);
        System.out.println("Order added: " + order.getIntention() + " " + order.getQuantity() +
                " shares of " + order.getStock() + " by " + order.getUser().getName());
    }

    // Method to remove an order from the stock market (e.g., if fulfilled or canceled)
    public boolean removeOrder(Order order) {
        boolean removed = orders.remove(order);
        if (removed) {
            System.out.println("Order removed: " + order.getIntention() + " " + order.getQuantity() +
                    " shares of " + order.getStock() + " by " + order.getUser().getName());
        } else {
            System.out.println("Order not found.");
        }
        return removed;
    }

    // Get all orders for a specific stock
    public List<Order> getOrdersByStock(String stock) {
        List<Order> result = new ArrayList<>();
        for (Order order : orders) {
            if (order.getStock().equalsIgnoreCase(stock)) {
                result.add(order);
            }
        }
        return result;
    }

    // Get all orders of a specific type (BUY or SELL)
    public List<Order> getOrdersByIntention(StockAction intention) {
        List<Order> result = new ArrayList<>();
        for (Order order : orders) {
            if (order.getIntention() == intention) {
                result.add(order);
            }
        }
        return result;
    }

    // Get all active orders
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    // Display all active orders (for monitoring)
    public void displayAllOrders() {
        if (orders.isEmpty()) {
            System.out.println("No active orders.");
        } else {
            System.out.println("Active orders:");
            for (Order order : orders) {
                System.out.println(order.getIntention() + " " + order.getQuantity() + " shares of " +
                        order.getStock() + " by " + order.getUser().getName());
            }
        }
    }

    // Method to populate the orders list with random orders
    public void populateRandomOrders(int n) {
        List<User> users = userDatabase.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users available to create orders.");
            return;
        }

        for (int i = 0; i < n; i++) {
            // Select a random user from the user database
            User user = users.get(random.nextInt(users.size()));

            // Random stock ticker
            String stockTicker = STOCK_TICKERS[random.nextInt(STOCK_TICKERS.length)];

            // Random quantity between 1 and 100
            int quantity = random.nextInt(100) + 1;

            // Random intention (BUY or SELL)
            StockAction intention = random.nextBoolean() ? StockAction.BUY : StockAction.SELL;

            // Create new order
            Order order = new Order(user, stockTicker, quantity, intention);

            // Add the order to the list
            addOrder(order);
        }
    }
}
