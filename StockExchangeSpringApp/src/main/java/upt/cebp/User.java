package upt.cebp;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String name;
    private double balance;
    private Map<String, Integer> stocks; // New variable to store stocks and their quantities

    public User(String name, double balance) {
        this.name = name;
        this.balance = balance;
        this.stocks = new HashMap<>(); // Initialize stocks as an empty map
    }

    public User(String name, double balance, Map<String, Integer> stocks) {
        this.name = name;
        this.balance = balance;
        this.stocks = stocks;
    }

    // Getter and setter for balance
    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }

    // Getter and setter for name
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    // Method to add or update stock quantity
    public void addOrUpdateStock(String ticker, int quantity) {
        stocks.put(ticker, stocks.getOrDefault(ticker, 0) + quantity);
    }

    // Method to remove stock
    public void removeStock(String ticker) {
        stocks.remove(ticker);
    }

    // Getter for stocks
    public Map<String, Integer> getStocks() {
        return stocks;
    }

    // Optional: Method to get the quantity of a specific stock
    public int getStockQuantity(String ticker) {
        return stocks.getOrDefault(ticker, 0);
    }

    public String getId() {
        return name;
    }
}

