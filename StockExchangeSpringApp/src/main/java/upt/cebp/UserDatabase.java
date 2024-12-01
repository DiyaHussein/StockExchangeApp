package upt.cebp;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Service // Mark this class as a Spring-managed service
public class UserDatabase {
    private final List<User> users;
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

    // Populate users with random data
    public void populateRandomUsers(int n) {
        for (int i = 0; i < n; i++) {
            String name = "User" + (i + 1);
            double balance = random.nextDouble() * 100000;
            User user = new User(name, balance);
            addUser(user);
        }
    }

    // Log users to JSON file
    public void logUsers() {
        JSONArray usersArray = new JSONArray();

        for (User user : users) {
            JSONObject userJson = new JSONObject();
            userJson.put("name", user.getName());
            userJson.put("balance", user.getBalance());

            JSONArray holdingsArray = new JSONArray();
            for (Map.Entry<String, Integer> entry : user.getStocks().entrySet()) {
                JSONObject stockJson = new JSONObject();
                stockJson.put("ticker", entry.getKey());
                stockJson.put("quantity", entry.getValue());
                holdingsArray.put(stockJson);
            }

            userJson.put("holdings", holdingsArray);
            usersArray.put(userJson);
        }

        try (FileWriter fileWriter = new FileWriter("users_log.json")) {
            fileWriter.write(usersArray.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Users log created at users_log.json");
    }
}
