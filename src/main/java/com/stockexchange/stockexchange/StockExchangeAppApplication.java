package com.stockexchange.stockexchange;

import static spark.Spark.*;

import java.io.FileWriter;
import java.io.IOException;

public class StockExchangeAppApplication {

    public static void main(String[] args) {
        // Clear the trade_log.json file when the application starts
        try (FileWriter fileWriter = new FileWriter("trade_log.json", false)) {
            // Opening in "false" mode clears the file
            fileWriter.write(""); // Clears the file content
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize the User Database and Stock Market
        UserDatabase userDatabase = new UserDatabase();
        StockMarket stockMarket = new StockMarket(userDatabase);

        // Populate random users and stock orders
        int nOfRandomUsers = 100;
        int nOfRandomOrders = 1000;
        userDatabase.populateRandomUsers(nOfRandomUsers);
        stockMarket.populateRandomOrders(nOfRandomOrders);

        // Set up Spark routes
        port(8080);  // Set the port for the web server

        // Endpoint to get all users (for testing)
        get("/users", (req, res) -> {
            return userDatabase.getAllUsers().toString();
        });

        // Endpoint to add a new user
        post("/addUser", (req, res) -> {
            // Assuming you're sending a JSON with 'name' and 'balance'
            String name = req.queryParams("name");
            double balance = Double.parseDouble(req.queryParams("balance"));
            User user = new User(name, balance);
            userDatabase.addUser(user);
            return "User added successfully!";
        });

        // Endpoint to get stock orders (for testing)
        get("/orders", (req, res) -> {
            return stockMarket.getAllOrders().toString();
        });

        // Example route for clearing trade_log.json
        get("/clearLog", (req, res) -> {
            try (FileWriter fileWriter = new FileWriter("trade_log.json", false)) {
                fileWriter.write(""); // Clears the file content
                return "Trade log cleared!";
            } catch (IOException e) {
                e.printStackTrace();
                return "Error clearing trade log.";
            }
        });

        // Thread to handle order matching (if needed in the future)
        OrderMatcher orderMatcher = new OrderMatcher(stockMarket);
        Thread orderMatcherThread = new Thread(orderMatcher);
        orderMatcherThread.start();

        // Add a shutdown hook to stop the OrderMatcher gracefully
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            orderMatcherThread.interrupt();
            System.out.println("OrderMatcher stopped.");
        }));
    }
}
