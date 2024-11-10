package com.stockexchange.stockexchange;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
//import org.json.JSONObject;

public class StockMarket {
    private ConcurrentLinkedQueue<Order> buyOrders = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Order> sellOrders = new ConcurrentLinkedQueue<>();
    private static final String[] STOCK_TICKERS = {"AAPL", "MSFT", "AMZN", "GOOGL", "TSLA", "FB", "NFLX"};
    private static final Random random = new Random();
    private final UserDatabase userDatabase;

    public StockMarket(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
    }

    // Method to add a new order to the stock market
    public void addOrder(Order order) {
        if (order.getIntention() == StockAction.BUY) {
            buyOrders.add(order);
        } else {
            sellOrders.add(order);
        }
        System.out.println("Order added: " + order.getIntention() + " " + order.getQuantity() +
                " shares of " + order.getStock() + " by " + order.getUser().getName());
    }

    // Method to remove an order from the stock market (e.g., if fulfilled or canceled)
    public boolean removeOrder(Order order) {
        boolean removed;
        if (order.getIntention() == StockAction.BUY) {
            removed = buyOrders.remove(order);
        } else {
            removed = sellOrders.remove(order);
        }

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

        for (Order order : buyOrders) {
            if (order.getStock().equalsIgnoreCase(stock)) {
                result.add(order);
            }
        }

        for (Order order : sellOrders) {
            if (order.getStock().equalsIgnoreCase(stock)) {
                result.add(order);
            }
        }

        return result;
    }

    // Get all orders of a specific type (BUY or SELL)
    public List<Order> getOrdersByIntention(StockAction intention) {
        List<Order> result = new ArrayList<>();

        if (intention == StockAction.BUY) {
            result.addAll(buyOrders);
        } else {
            result.addAll(sellOrders);
        }

        return result;
    }

    // Get all active orders
    public List<Order> getAllOrders() {
        List<Order> allOrders = new ArrayList<>(buyOrders);
        allOrders.addAll(sellOrders);
        return allOrders;
    }

    // Display all active orders (for monitoring)
    public void displayAllOrders() {
        if (buyOrders.isEmpty() && sellOrders.isEmpty()) {
            System.out.println("No active orders.");
        } else {
            System.out.println("Active buy orders:");
            for (Order order : buyOrders) {
                System.out.println("BUY " + order.getQuantity() + " shares of " +
                        order.getStock() + " by " + order.getUser().getName());
            }

            System.out.println("Active sell orders:");
            for (Order order : sellOrders) {
                System.out.println("SELL " + order.getQuantity() + " shares of " +
                        order.getStock() + " by " + order.getUser().getName());
            }
        }
    }

    public ConcurrentLinkedQueue<Order> getBuyOrders() {
        return buyOrders;
    }

    public ConcurrentLinkedQueue<Order> getSellOrders() {
        return sellOrders;
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
            double price = random.nextDouble(100) + 1;

            // Random intention (BUY or SELL)
            StockAction intention = random.nextBoolean() ? StockAction.BUY : StockAction.SELL;

            // Create new order
            Order order = new Order(user, stockTicker, quantity, price, intention);

            // Add the order to the appropriate queue
            addOrder(order);
        }
    }

    // Method to record a trade in JSON format
    public void recordTrade(Order buyOrder, Order sellOrder, int tradeQuantity) {
//        // Define the trade details
//        double tradePrice = (buyOrder.getPrice() + sellOrder.getPrice()) / 2; // Average price
//        double totalValue = tradeQuantity * tradePrice;
//
//        JSONObject tradeRecord = new JSONObject();
//        tradeRecord.put("timestamp", new Date().toString());
//        tradeRecord.put("stock", buyOrder.getStock());
//        tradeRecord.put("quantity", tradeQuantity);
//        tradeRecord.put("price", tradePrice);
//        tradeRecord.put("totalValue", totalValue);
//        tradeRecord.put("buyerId", buyOrder.getUser().getId());
//        tradeRecord.put("sellerId", sellOrder.getUser().getId());
//        tradeRecord.put("buyOrderId", buyOrder.getId());
//        tradeRecord.put("sellOrderId", sellOrder.getId());
//
//        // Write trade record to a JSON log file
//        try (FileWriter file = new FileWriter("trade_log.json", true)) {
//            file.write(tradeRecord.toString() + "\n");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("Trade recorded: " + tradeRecord);
    }
}
