package com.stockexchange.stockexchange;

import static spark.Spark.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StockExchangeAppApplication {

    private static final String LOG_DIRECTORY = "logs";

    public static void main(String[] args) {
        // Enable CORS for all endpoints
        enableCORS();

        // Clear the trade_log.json file when the application starts
        try (FileWriter fileWriter = new FileWriter("trade_log.json", false)) {
            fileWriter.write(""); // Clears the file content
        } catch (IOException e) {
            System.err.println("Error clearing the trade log: " + e.getMessage());
        }

        // Initialize the User Database and Stock Market
        UserDatabase userDatabase = new UserDatabase();
        StockMarket stockMarket = new StockMarket(userDatabase);
        System.out.println("StockMarket initialized with stocks: " + stockMarket.getAllStocks());

        // Populate random users and stock orders
        int nOfRandomUsers = 10;
        int nOfRandomOrders = 10;
        stockMarket.populateRandomOrders(nOfRandomOrders);

        // Add a shutdown hook to save the trade log with a timestamp
        Runtime.getRuntime().addShutdownHook(new Thread(StockExchangeAppApplication::saveTradeLog));

        // Set up Spark routes
        port(9090); // Set the port for the web server

        // Test route to check server status
        get("/test", (req, res) -> "Server is running!");

        // Endpoint to get all stocks
        get("/stocks", (req, res) -> {
            System.out.println("here");
            res.type("application/json");
            List<Stock> stocks = stockMarket.getAllStocks();
            return stocks.stream()
                    .map(stock -> String.format("{\"ticker\": \"%s\", \"price\": %.2f}", stock.getTicker(), stock.getPrice()))
                    .collect(Collectors.joining(",", "[", "]"));
        });

        // Endpoint to add a trade
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

        // Endpoint to list all live orders
        get("/orders/live", (req, res) -> {
            res.type("application/json");
            String type = req.queryParams("type");

            if (type == null) {
                // If no type is specified, return all orders
                List<Order> allOrders = stockMarket.getAllOrders();
                if (allOrders.isEmpty()) {
                    res.status(404);
                    return "No live orders found.";
                }
                return allOrders.stream()
                        .map(Order::toString)
                        .collect(Collectors.joining("\n"));
            }

            // If a type is specified, filter the orders by type
            try {
                StockAction action = StockAction.valueOf(type.toUpperCase());
                List<Order> filteredOrders = stockMarket.getOrdersByIntention(action);

                if (filteredOrders.isEmpty()) {
                    return "No live " + action + " orders found.";
                }
                return filteredOrders.stream()
                        .map(Order::toString)
                        .collect(Collectors.joining("\n"));
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
        OrderMatcher orderMatcher = new OrderMatcher(userDatabase, stockMarket);
        Thread orderMatcherThread = new Thread(orderMatcher);
        orderMatcherThread.setDaemon(true); // Make sure this thread doesn't block JVM shutdown
        orderMatcherThread.start();

        // Keep the application running indefinitely
        awaitInitialization(); // Keeps the Spark server running until a shutdown signal

        System.out.println("Server is running. Press CTRL+C to stop.");
    }

    private static void saveTradeLog() {
        try {
            // Create logs directory if it doesn't exist
            File logDir = new File(LOG_DIRECTORY);
            if (!logDir.exists()) {
                logDir.mkdir();
            }

            // Generate a timestamped filename
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String targetFileName = LOG_DIRECTORY + File.separator + "trade_log_" + timestamp + ".json";

            // Move the current trade_log.json file to the new file
            Files.move(Paths.get("trade_log.json"), Paths.get(targetFileName));

            System.out.println("Trade log saved to: " + targetFileName);
        } catch (IOException e) {
            System.err.println("Error saving trade log: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void enableCORS() {
        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            res.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });
    }
}
