package com.stockexchange.stockexchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StockExchangeAppApplication {

    public static void main(String[] args) {
        //SpringApplication.run(StockExchangeAppApplication.class, args);

        int nOfRandomOrders = 10;
        int nOfRandomUsers = 5;

        UserDatabase userDatabase = new UserDatabase();
        StockMarket stockMarket = new StockMarket(userDatabase);

        StockExchangeService stockExchangeService = new StockExchangeService(userDatabase, stockMarket);

        OrderMatcher orderMatcher = new OrderMatcher(stockMarket);

        Thread orderMatcherThread = new Thread(orderMatcher);
        orderMatcherThread.start();

        userDatabase.populateRandomUsers(nOfRandomUsers);
        stockMarket.populateRandomOrders(nOfRandomOrders);

        // add a shutdown hook to stop the OrderMatcher gracefully
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            orderMatcherThread.interrupt();
            System.out.println("OrderMatcher stopped.");
        }));

        //stockExchangeService.setUpRandomStockExchange();
    }

}
