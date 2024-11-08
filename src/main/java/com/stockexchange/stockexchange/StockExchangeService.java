package com.stockexchange.stockexchange;

import com.stockexchange.stockexchange.Stock;
import com.stockexchange.stockexchange.Order;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

@Service
public class StockExchangeService {
    // TODO: create user database
    // TODO: create stockMarket (also kind of a database)
    //public StockMarket stockMarket = new StockMarket();

    public StockExchangeService() {
        // start with some data
        stocks.put("AAPL", new Stock("AAPL", 150.0));
        stocks.put("GOOG", new Stock("GOOG", 2800.0));
    }

    public List<Stock> getStocks() {
        return new ArrayList<>(stocks.values());
    }

    public double getBalance(String userId) {
        return 0;
    }

    public String placeOrder(Order order) {
        Stock stock = stocks.get(order.getStock());
        if (stock == null) {
            return "Stock not found";
        }

        double totalCost = stock.getPrice() * order.getQuantity();
        double userBalance = 0; //TODO:

        if (order.getIntention() == StockAction.BUY) {
            if (userBalance >= totalCost) {
//                balances.put(order.getUser(), userBalance - totalCost);
                return "Order placed: Bought " + order.getQuantity() + " shares of " + order.getStock();
            } else {
                return "Insufficient balance";
            }
        } else if (order.getIntention() == StockAction.SELL) {
//            balances.put(order.getUser(), userBalance + totalCost);
            return "Order placed: Sold " + order.getQuantity() + " shares of " + order.getStock();
        }
        return "Invalid order type";
    }
}
