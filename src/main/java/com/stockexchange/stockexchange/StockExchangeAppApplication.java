package com.stockexchange.stockexchange;

import static spark.Spark.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class StockExchangeAppApplication {

    public static void main(String[] args) {
        // Clear the trade_log.json file when the application starts
        try (FileWriter fileWriter = new FileWriter("trade_log.json", false)) {
            fileWriter.write(""); // Clears the file content
        } catch (IOException e) {
            System.err.println("Error clearing the trade log: " + e.getMessage());
        }

        // Initialize the User Database and Stock Market
        UserDatabase userDatabase = new UserDatabase();
        StockMarket stockMarket = new StockMarket(userDatabase);

        // Populate random users and stock orders
        int nOfRandomUsers = 10;
        int nOfRandomOrders = 10;
        userDatabase.populateRandomUsers(nOfRandomUsers);
        stockMarket.populateRandomOrders(nOfRandomOrders);

        // Set up Spark routes
        port(9090);  // Set the port for the web server

        // Test route to check server status
        get("/test", (req, res) -> "Server is running!");

        // Endpoint to get all users (for testing)
        get("/users", (req, res) -> {
            List<User> users = userDatabase.getAllUsers();
            return users.isEmpty() ? "No users found." : users.toString();
        });

        // Endpoint to add a new user
        post("/addUser", (req, res) -> {
            String name = req.queryParams("name");
            String balanceStr = req.queryParams("balance");
            if (name == null || balanceStr == null) {
                res.status(400);
                return "Name and balance are required.";
            }

            try {
                double balance = Double.parseDouble(balanceStr);
                if (balance < 0) {
                    res.status(400);
                    return "Balance must be a positive number.";
                }

                User user = new User(name, balance);
                userDatabase.addUser(user);
                return "User added successfully!";
            } catch (NumberFormatException e) {
                res.status(400);
                return "Invalid balance format.";
            }
        });

        // Endpoint to get stock orders (for testing)
        get("/orders", (req, res) -> {
            List<Order> orders = stockMarket.getAllOrders();
            return orders.isEmpty() ? "No orders found." : orders.toString();
        });

        // Endpoint to log a trade into the trade_log.json file
        post("/logTrade", (req, res) -> {
            String stockSymbol = req.queryParams("symbol");
            double price = Double.parseDouble(req.queryParams("price"));
            int quantity = Integer.parseInt(req.queryParams("quantity"));
            String tradeDetails = "Trade: " + stockSymbol + " at $" + price + " for " + quantity + " units.";

            try (FileWriter fileWriter = new FileWriter("trade_log.json", true)) {
                fileWriter.append(tradeDetails + "\n");
                return "Trade logged: " + tradeDetails;
            } catch (IOException e) {
                e.printStackTrace();
                res.status(500);
                return "Error logging trade.";
            }
        });

        // Example route for clearing trade_log.json
        get("/clearLog", (req, res) -> {
            try (FileWriter fileWriter = new FileWriter("trade_log.json", false)) {
                fileWriter.write(""); // Clears the file content
                return "Trade log cleared!";
            } catch (IOException e) {
                e.printStackTrace();
                res.status(500);
                return "Error clearing trade log.";
            }
        });

        // Thread to handle order matching
        OrderMatcher orderMatcher = new OrderMatcher(stockMarket);
        Thread orderMatcherThread = new Thread(orderMatcher);
        orderMatcherThread.setDaemon(true); // Make sure this thread doesn't block JVM shutdown
        orderMatcherThread.start();

        // Keep the application running indefinitely
        awaitInitialization();  // Keeps the Spark server running until a shutdown signal
    }
}
