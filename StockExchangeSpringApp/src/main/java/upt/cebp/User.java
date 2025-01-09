package upt.cebp;

import java.util.HashMap;
import java.util.Map;

public class User {

    private Long id; // You can manage this manually if needed
    private String name;
    private double balance;
    private Map<String, Integer> stocks;
    public String password;

    public User() {} // Default constructor is required for JSON deserialization

    public User(String name, double balance) {
        this.name = name;
        this.balance = balance;
        this.stocks = new HashMap<>();
    }

    public User(String name, double balance, Map<String, Integer> stocks) {
        this.name = name;
        this.balance = balance;
        this.stocks = stocks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Map<String, Integer> getStocks() {
        return stocks;
    }

    public void setStocks(Map<String, Integer> stocks) {
        this.stocks = stocks;
    }

    public void addOrUpdateStock(String ticker, int quantity) {
        stocks.put(ticker, stocks.getOrDefault(ticker, 0) + quantity);
    }

    public void removeStock(String ticker) {
        stocks.remove(ticker);
    }

    public int getStockQuantity(String ticker) {
        return stocks.getOrDefault(ticker, 0);
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){this.password = password;}
}