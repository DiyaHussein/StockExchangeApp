package com.stockexchange.stockexchange;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
}
