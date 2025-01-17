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
    private final UserDatabase userDatabase;

    private final ReentrantLock lock = new ReentrantLock();
    private volatile boolean running = true; // Control flag for thread termination
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public OrderMatcher(UserDatabase userDatabase, StockMarket stockMarket) {
        this.userDatabase = userDatabase;
        this.stockMarket = stockMarket;
    }

    public OrderMatcher(UserDatabase userDatabase) {
        this(userDatabase, new StockMarket(userDatabase)); // Default stockMarket initialization
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
        double tradePrice = sellOrder.getPrice();
        double totalTradeCost = tradeQuantity * tradePrice;

        // Use userDatabase for API calls
        String buyerId = buyOrder.getUser().getId().toString();
        userDatabase.updateUserBalance(buyerId, buyOrder.getUser().getBalance() - totalTradeCost);
        userDatabase.updateUserStock(buyerId, buyOrder.getStock(), tradeQuantity);

        String sellerId = sellOrder.getUser().getId().toString();
        userDatabase.updateUserBalance(sellerId, sellOrder.getUser().getBalance() + totalTradeCost);
        userDatabase.updateUserStock(sellerId, sellOrder.getStock(), -tradeQuantity);

        System.out.printf(
                "Trade Executed: Stock=%s, Quantity=%d, BuyPrice=%.2f, SellPrice=%.2f, Buyer=%s, Seller=%s%n",
                buyOrder.getStock(),
                tradeQuantity,
                buyOrder.getPrice(),
                sellOrder.getPrice(),
                buyOrder.getUser().getName(),
                sellOrder.getUser().getName()
        );

        stockMarket.recordTrade(buyOrder, sellOrder, tradeQuantity);
    }

    // Method to stop the matcher thread if needed
    public void stop() {
        running = false;
    }
}
