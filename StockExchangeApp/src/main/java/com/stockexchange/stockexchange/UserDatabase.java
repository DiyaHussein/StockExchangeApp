package com.stockexchange.stockexchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UserDatabase {

    private final RestClient restClient;
    private static final String BASE_URL = "http://localhost:8080/api/users"; // Spring App's base URL
    private final ObjectMapper objectMapper;

    public UserDatabase() {
        this.restClient = new RestClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<User> getAllUsers() {
        try {
            String response = restClient.get(BASE_URL);
            return objectMapper.readValue(response, objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch users", e);
        }
    }

    public Optional<User> getUser(String userId) {
        try {
            String response = restClient.get(BASE_URL + "/" + userId);
            User user = objectMapper.readValue(response, User.class);
            return Optional.of(user);
        } catch (IOException | InterruptedException e) {
            return Optional.empty();
        }
    }

    // kept just in case, should not be used
    public void addUser(User user) {
        try {
            String userJson = objectMapper.writeValueAsString(user);
            restClient.post(BASE_URL, userJson);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to add user", e);
        }
    }

    // kept just in case, should not be used
    public boolean removeUser(String userId) {
        try {
            restClient.delete(BASE_URL + "/" + userId);
            return true;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public void updateUserBalance(String userId, double newBalance) {
        try {
            String balanceUpdateUrl = BASE_URL + "/" + userId + "/balance";
            restClient.patch(balanceUpdateUrl, String.valueOf(newBalance));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to update user balance", e);
        }
    }

    public void updateUserStock(String userId, String stockTicker, int quantity) {
        try {
            String stockUpdateUrl = BASE_URL + "/" + userId + "/stocks/" + stockTicker;
            restClient.patch(stockUpdateUrl, String.valueOf(quantity));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to update user stock", e);
        }
    }

    public void removeUserStock(String userId, String stockTicker) {
        try {
            String stockDeleteUrl = BASE_URL + "/" + userId + "/stocks/" + stockTicker;
            restClient.delete(stockDeleteUrl);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to remove user stock", e);
        }
    }

}

