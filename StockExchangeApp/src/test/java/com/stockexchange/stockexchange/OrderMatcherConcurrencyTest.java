package com.stockexchange.stockexchange;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class OrderMatcherConcurrencyTest {

    //@Test
    @Disabled // TODO: check test and fix possible concurrency issues if it still fails
    public void testMatchOrdersConcurrency() throws InterruptedException {
        // Set up the stock market and order matcher
        UserDatabase userDatabase = new UserDatabase();
        StockMarket stockMarket = new StockMarket(userDatabase);
        OrderMatcher orderMatcher = new OrderMatcher(stockMarket);

        // Populate the user database
        userDatabase.populateRandomUsers(10);

        // Start the OrderMatcher in a separate thread
        Thread matcherThread = new Thread(orderMatcher);
        matcherThread.setDaemon(true);
        matcherThread.start();

        // Use an ExecutorService to simulate concurrent order submissions
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Submit buy and sell orders concurrently
        for (int i = 0; i < 100; i++) {
            final int index = i;
            executor.submit(() -> {
                User user = userDatabase.getAllUsers().get(index % userDatabase.getAllUsers().size());
                String stock = "AAPL";  // Example stock ticker
                int quantity = 10 + index;  // Incremental quantity
                double price = 100 + index * 0.5;  // Incremental price

                // Alternate between BUY and SELL
                StockAction action = (index % 2 == 0) ? StockAction.BUY : StockAction.SELL;

                // Create and add the order
                Order order = new Order(user, stock, quantity, price, action);
                stockMarket.addOrder(order);
            });
        }

        // Shut down the executor and wait for tasks to finish
        executor.shutdown();
        assertTrue(executor.awaitTermination(30, TimeUnit.SECONDS), "Executor did not terminate in time");

        // Allow some time for order matching to occur
        Thread.sleep(2000);

        // Validate that all matched orders are processed without concurrency issues
        assertEquals(0, stockMarket.getBuyOrders().size(), "All buy orders should be matched or processed");
        assertEquals(0, stockMarket.getSellOrders().size(), "All sell orders should be matched or processed");

        // Stop the matcher thread
        orderMatcher.stop();
    }
}
