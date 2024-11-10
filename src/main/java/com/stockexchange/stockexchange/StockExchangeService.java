package com.stockexchange.stockexchange;

import com.stockexchange.stockexchange.Stock;
import com.stockexchange.stockexchange.Order;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

@Service
public class StockExchangeService {
    public UserDatabase userDatabase;
    public StockMarket stockMarket;

    public StockExchangeService() {
        userDatabase = new UserDatabase();
        stockMarket = new StockMarket(userDatabase);
    }

    public StockExchangeService(UserDatabase userDatabase, StockMarket stockMarket) {
        this.userDatabase = userDatabase;
        this.stockMarket = stockMarket;
    }

    // dummy function used for debug
    public void setUpRandomStockExchange() {
        if (this.userDatabase == null || this.stockMarket == null) {
            throw new NullPointerException("userDatabase or stockMarket is null");
        }

        int nOfUsers = 5;
        int nOfStocks = 20;

        userDatabase.populateRandomUsers(nOfUsers);
        stockMarket.populateRandomOrders(nOfStocks);
    }
}
