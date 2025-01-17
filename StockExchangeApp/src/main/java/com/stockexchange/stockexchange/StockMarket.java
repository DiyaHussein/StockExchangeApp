package com.stockexchange.stockexchange;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.json.JSONObject;

public class StockMarket {
    private final ConcurrentLinkedQueue<Order> buyOrders = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Order> sellOrders = new ConcurrentLinkedQueue<>();
    private final List<Stock> availableStocks;
    private static final String[] STOCK_TICKERS = {"AAPL", "MSFT", "AMZN", "GOOGL", "TSLA", "FB", "NFLX"};
    private static final Random random = new Random();
    private final UserDatabase userDatabase;

    public StockMarket(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
        this.availableStocks = new ArrayList<>();
        initializeStocks();
        startPriceUpdater();
    }

    private void initializeStocks() {
        for (String ticker : STOCK_TICKERS) {
            double price = 50 + random.nextDouble() * 100; // Random price between $50 and $150
            availableStocks.add(new Stock(ticker, price));
        }
        System.out.println("Initialized stocks: " + availableStocks);
    }

    public List<Stock> getAllStocks() {
        System.out.println("Fetching all stocks: " + availableStocks);
        return availableStocks;
    }

    public void addOrder(Order order) {
        if (order.getIntention() == StockAction.BUY) {
            buyOrders.add(order);
        } else {
            sellOrders.add(order);
        }
        System.out.println("Order added: " + order);
    }

    public List<Order> getAllOrders() {
        List<Order> allOrders = new ArrayList<>(buyOrders);
        allOrders.addAll(sellOrders);
        System.out.println("Active orders: " + allOrders);
        return allOrders;
    }

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

    public List<Order> getOrdersByIntention(StockAction intention) {
        List<Order> result = new ArrayList<>();

        if (intention == StockAction.BUY) {
            result.addAll(buyOrders);
        } else {
            result.addAll(sellOrders);
        }

        return result;
    }

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

    private void updateStockPrices() {
        for (Stock stock : availableStocks) {
            double change = (random.nextDouble() - 0.5) * 5; // Random change between -2.5 and +2.5
            stock.setPrice(Math.max(0, stock.getPrice() + change)); // Ensure price doesn't go negative
        }
        System.out.println("Updated stock prices: " + availableStocks);
    }

    private void startPriceUpdater() {
        new Thread(() -> {
            while (true) {
                updateStockPrices();
                try {
                    Thread.sleep(5000); // Update every 5 seconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    public ConcurrentLinkedQueue<Order> getBuyOrders() {
        return buyOrders;
    }

    public ConcurrentLinkedQueue<Order> getSellOrders() {
        return sellOrders;
    }

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

    public void recordTrade(Order buyOrder, Order sellOrder, int tradeQuantity) {
        double tradePrice = (buyOrder.getPrice() + sellOrder.getPrice()) / 2; // Average price
        double totalValue = tradeQuantity * tradePrice;

        JSONObject tradeRecord = new JSONObject();
        tradeRecord.put("timestamp", new Date().toString());
        tradeRecord.put("stock", buyOrder.getStock());
        tradeRecord.put("quantity", tradeQuantity);
        tradeRecord.put("price", tradePrice);
        tradeRecord.put("totalValue", totalValue);
        tradeRecord.put("buyerId", buyOrder.getUser().getId());
        tradeRecord.put("sellerId", sellOrder.getUser().getId());
        tradeRecord.put("buyOrderId", buyOrder.getId());
        tradeRecord.put("sellOrderId", sellOrder.getId());

        try (FileWriter file = new FileWriter("trade_log.json", true)) {
            file.write(tradeRecord.toString() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Trade recorded: " + tradeRecord);
    }
}
