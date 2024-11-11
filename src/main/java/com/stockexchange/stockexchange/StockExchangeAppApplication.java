package com.stockexchange.stockexchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileWriter;
import java.io.IOException;

@SpringBootApplication
public class StockExchangeAppApplication {

    public static void main(String[] args) {
        //SpringApplication.run(StockExchangeAppApplication.class, args);

        // Clear the trade_log.json file when the application starts
        try (FileWriter fileWriter = new FileWriter("trade_log.json", false)) {
            // Opening in "false" mode clears the file
            fileWriter.write(""); // Clears the file content
        } catch (IOException e) {
            e.printStackTrace();
        }

        int nOfRandomOrders = 1000;
        int nOfRandomUsers = 100;

        UserDatabase userDatabase = new UserDatabase();
        StockMarket stockMarket = new StockMarket(userDatabase);

        // StockExchangeService class might be useless, will need to redesign/reconsider in the future
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
