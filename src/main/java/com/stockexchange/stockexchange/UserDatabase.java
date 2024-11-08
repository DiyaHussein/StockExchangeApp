package com.stockexchange.stockexchange;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDatabase {
    private List<User> users;

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
}
