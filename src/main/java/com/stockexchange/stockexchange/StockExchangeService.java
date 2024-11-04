package com.stockexchange.stockexchange;

import com.stockexchange.stockexchange.Stock;
import com.stockexchange.stockexchange.Order;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

@Service
public class StockExchangeService {
    private ConcurrentHashMap<String, Stock> stocks = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Double> balances = new ConcurrentHashMap<>();

    public StockExchangeService() {
        // start with some data
        stocks.put("AAPL", new Stock("AAPL", 150.0));
        stocks.put("GOOG", new Stock("GOOG", 2800.0));
        balances.put("user1", 1000.0);
        balances.put("user2", 1500.0);
    }

    public List<Stock> getStocks() {
        return new ArrayList<>(stocks.values());
    }

    public double getBalance(String userId) {
        return balances.getOrDefault(userId, 0.0);
    }

    public String placeOrder(Order order) {
        Stock stock = stocks.get(order.getStock());
        if (stock == null) {
            return "Stock not found";
        }

        double totalCost = stock.getPrice() * order.getQuantity();
        double userBalance = balances.getOrDefault(order.getUser(), 0.0);

        if (order.getType().equalsIgnoreCase("buy")) {
            if (userBalance >= totalCost) {
                balances.put(order.getUser(), userBalance - totalCost);
                return "Order placed: Bought " + order.getQuantity() + " shares of " + order.getStock();
            } else {
                return "Insufficient balance";
            }
        } else if (order.getType().equalsIgnoreCase("sell")) {
            balances.put(order.getUser(), userBalance + totalCost);
            return "Order placed: Sold " + order.getQuantity() + " shares of " + order.getStock();
        }
        return "Invalid order type";
    }
}
