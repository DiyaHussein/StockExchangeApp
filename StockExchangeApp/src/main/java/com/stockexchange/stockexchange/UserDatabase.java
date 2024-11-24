package com.stockexchange.stockexchange;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class UserDatabase {
    private List<User> users;
    private static final Random random = new Random();

    public UserDatabase() {
        this.users = new ArrayList<>();
    }

    // Add a new user
    public void addUser(User user) {
        users.add(user);
    }

    // Get user by name
    public Optional<User> getUser(String name) {
        return users.stream()
                .filter(user -> user.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    // Remove a user by name
    public boolean removeUser(String name) {
        return users.removeIf(user -> user.getName().equalsIgnoreCase(name));
    }

    // Update balance for a specific user
    public void updateBalance(String name, double newBalance) {
        getUser(name).ifPresent(user -> user.setBalance(newBalance));
    }

    // Get all users
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    // Get user by name
    public User getUserByName(String name) {
        for (User user : users) {
            if (user.getName().equalsIgnoreCase(name)) {
                return user;
            }
        }
        return null; // User not found
    }

    // Method to populate the users list with random users
    public void populateRandomUsers(int n) {
        for (int i = 0; i < n; i++) {
            // Generate a random name (e.g., "User1", "User2", etc.)
            String name = "User" + (i + 1);

            // Generate a random balance between 0 and 100,000
            double balance = random.nextDouble() * 100000;

            // Create a new user with the generated name and balance
            User user = new User(name, balance);

            // Add the user to the list
            addUser(user);
        }
    }

    // Method to log all users with balances and holdings to a JSON file
    public void logUsers() {
        JSONArray usersArray = new JSONArray();

        for (User user : users) {
            JSONObject userJson = new JSONObject();
            userJson.put("name", user.getName());
            userJson.put("balance", user.getBalance());

            // Collect stock holdings from the Map<String, Integer>
            JSONArray holdingsArray = new JSONArray();
            for (Map.Entry<String, Integer> entry : user.getStocks().entrySet()) {
                JSONObject stockJson = new JSONObject();
                stockJson.put("ticker", entry.getKey());       // Stock ticker
                stockJson.put("quantity", entry.getValue());    // Quantity owned
                holdingsArray.put(stockJson);
            }

            userJson.put("holdings", holdingsArray);
            usersArray.put(userJson);
        }

        // Write JSON array to file
        try (FileWriter fileWriter = new FileWriter("users_log.json")) {
            fileWriter.write(usersArray.toString(4)); // Pretty-print with 4 spaces indentation
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Users log created at users_log.json");
    }
}
