package com.stockexchange.stockexchange;

import com.stockexchange.stockexchange.Order;
import com.stockexchange.stockexchange.StockMarket;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;

import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OrderMatcher implements Runnable {
    private final StockMarket stockMarket;
    private final ReentrantLock lock = new ReentrantLock();
    private volatile boolean running = true; // Control flag for thread termination
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public OrderMatcher(StockMarket stockMarket) {
        this.stockMarket = stockMarket;
    }

    @Override
    public void run() {
        while (running) {
            try {
                matchOrders();
                // Sleep to reduce CPU usage and allow graceful thread interruption
                Thread.sleep(1);  // Sleep for 100 milliseconds, adjust if needed
            } catch (InterruptedException e) {
                // Thread interrupted, exit gracefully
                Thread.currentThread().interrupt();  // Preserve interrupt status
                System.out.println("OrderMatcher thread was interrupted.");
            }
        }
    }

    private void matchOrders() {
        lock.lock();
        try {
            Iterator<Order> buyIterator = stockMarket.getBuyOrders().iterator();
            while (buyIterator.hasNext()) {
                Order buyOrder = buyIterator.next();
                Iterator<Order> sellIterator = stockMarket.getSellOrders().iterator();

                while (sellIterator.hasNext()) {
                    Order sellOrder = sellIterator.next();

                    // Check if the orders match by stock and price
                    if (buyOrder.matches(sellOrder)) {
                        int tradeQuantity = Math.min(buyOrder.getQuantity(), sellOrder.getQuantity());

                        // Execute the trade
                        executeTrade(buyOrder, sellOrder, tradeQuantity);

                        // Remove fully matched orders from the queues
                        if (buyOrder.getQuantity() == 0) {
                            buyIterator.remove();
                        }
                        if (sellOrder.getQuantity() == 0) {
                            sellIterator.remove();
                        }

                        // If buyOrder is fully matched, move to the next buy order
                        if (buyOrder.getQuantity() == 0) {
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error in matching orders: " + e.getMessage());
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private void executeTrade(Order buyOrder, Order sellOrder, int tradeQuantity) {
        // Adjust quantities of orders after matching
        buyOrder.reduceQuantity(tradeQuantity);
        sellOrder.reduceQuantity(tradeQuantity);

        // Update user balances locally
        double totalTradeCost = tradeQuantity * sellOrder.getPrice();
        buyOrder.getUser().setBalance(buyOrder.getUser().getBalance() - totalTradeCost);
        sellOrder.getUser().setBalance(sellOrder.getUser().getBalance() + totalTradeCost);

        // Adjust stock holdings locally
        buyOrder.getUser().addOrUpdateStock(buyOrder.getStock(), tradeQuantity);
        sellOrder.getUser().addOrUpdateStock(sellOrder.getStock(), -tradeQuantity);

        // Synchronize balances with the Spring app
        syncUserBalanceWithSpringApp(buyOrder.getUser());
        syncUserBalanceWithSpringApp(sellOrder.getUser());

        // Log the trade details to the terminal
        System.out.printf(
                "Trade Executed: Stock=%s, Quantity=%d, BuyPrice=%.2f, SellPrice=%.2f, Buyer=%s, Seller=%s%n",
                buyOrder.getStock(),
                tradeQuantity,
                buyOrder.getPrice(),
                sellOrder.getPrice(),
                buyOrder.getUser().getName(),
                sellOrder.getUser().getName()
        );

        // Record the trade in the system
        stockMarket.recordTrade(buyOrder, sellOrder, tradeQuantity);
    }

    private void syncUserBalanceWithSpringApp(User user) {
        try {
            String apiUrl = "http://localhost:8080/api/users/" + user.getId(); // Spring app's endpoint
            String updatedUserJson = objectMapper.writeValueAsString(user);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(updatedUserJson))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.printf("Failed to update user %s on Spring app. Status: %d%n", user.getName(), response.statusCode());
            }
        } catch (Exception e) {
            System.err.printf("Error updating user %s: %s%n", user.getName(), e.getMessage());
        }
    }
    // Method to stop the matcher thread if needed
    public void stop() {
        running = false;
    }
}
