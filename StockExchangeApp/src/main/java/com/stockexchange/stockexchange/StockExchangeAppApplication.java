package com.stockexchange.stockexchange;

import static spark.Spark.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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

        // Remove the /logTrade endpoint
        // Add a new endpoint to add a trade to the StockMarket orders list
        post("/addTrade", (req, res) -> {
            String userId = req.queryParams("userId");
            String stockSymbol = req.queryParams("stock");
            String quantityStr = req.queryParams("quantity");
            String priceStr = req.queryParams("price");
            String actionStr = req.queryParams("action");

            if (userId == null || stockSymbol == null || quantityStr == null || priceStr == null || actionStr == null) {
                res.status(400);
                return "Missing required parameters. Required: userId, stock, quantity, price, action.";
            }

            try {
                int quantity = Integer.parseInt(quantityStr);
                double price = Double.parseDouble(priceStr);
                StockAction action = StockAction.valueOf(actionStr.toUpperCase());

                if (quantity <= 0 || price <= 0) {
                    res.status(400);
                    return "Quantity and price must be positive numbers.";
                }

                User user = userDatabase.getUser(userId).orElse(null);
                if (user == null) {
                    res.status(404);
                    return "User not found.";
                }

                // Create a new order
                Order order = new Order(user, stockSymbol, quantity, price, action);

                // Add the order to the appropriate list in StockMarket
                stockMarket.addOrder(order);

                return "Order added successfully: " + order;
            } catch (NumberFormatException e) {
                res.status(400);
                return "Invalid quantity or price format.";
            } catch (IllegalArgumentException e) {
                res.status(400);
                return "Invalid action. Must be BUY or SELL.";
            }
        });

        // Add an endpoint to list all live orders
        get("/orders/live", (req, res) -> {
            String type = req.queryParams("type");

            if (type == null) {
                // If no type is specified, return all orders
                List<Order> allOrders = stockMarket.getAllOrders();
                if (allOrders.isEmpty()) {
                    return "No live orders found.";
                }
                return String.join("\n", allOrders.stream().map(Order::toString).collect(Collectors.toList()));
            }

            // If a type is specified, filter the orders by type
            try {
                StockAction action = StockAction.valueOf(type.toUpperCase());
                List<Order> filteredOrders = stockMarket.getOrdersByIntention(action);

                if (filteredOrders.isEmpty()) {
                    return "No live " + action + " orders found.";
                }
                return String.join("\n", filteredOrders.stream().map(Order::toString).collect(Collectors.toList()));
            } catch (IllegalArgumentException e) {
                res.status(400);
                return "Invalid type. Must be 'BUY' or 'SELL'.";
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
