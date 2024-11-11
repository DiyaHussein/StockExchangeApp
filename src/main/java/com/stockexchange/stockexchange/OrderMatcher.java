package com.stockexchange.stockexchange;

import com.stockexchange.stockexchange.Order;
import com.stockexchange.stockexchange.StockMarket;

import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

public class OrderMatcher implements Runnable {
    private StockMarket stockMarket;
    private ReentrantLock lock = new ReentrantLock();

    public OrderMatcher(StockMarket stockMarket) {
        this.stockMarket = stockMarket;
    }

    @Override
    public void run() {
        while (true) {
            matchOrders();
            // Sleep or wait briefly to prevent excessive CPU usage
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
        } finally {
            lock.unlock();
        }
    }

    private void executeTrade(Order buyOrder, Order sellOrder, int tradeQuantity) {
        // Adjust quantities of orders after matching
        buyOrder.reduceQuantity(tradeQuantity);
        sellOrder.reduceQuantity(tradeQuantity);

        // Adjust quantities of user balances
        buyOrder.getUser().setBalance(buyOrder.getUser().getBalance() + tradeQuantity);
        sellOrder.getUser().setBalance(sellOrder.getUser().getBalance() - tradeQuantity);

        // Adjust quantities of users' stock holdings
        buyOrder.getUser().addOrUpdateStock(buyOrder.getStock(), tradeQuantity);
        sellOrder.getUser().addOrUpdateStock(sellOrder.getStock(), tradeQuantity);

        // Record trade and update user balances
        stockMarket.recordTrade(buyOrder, sellOrder, tradeQuantity);
    }
}
