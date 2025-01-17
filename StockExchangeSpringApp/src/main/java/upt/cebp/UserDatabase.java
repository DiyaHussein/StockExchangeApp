package upt.cebp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class UserDatabase {

    private final List<User> users = new ArrayList<>();
    private long nextId = 1;
    private static final String FILE_PATH = "users.json";

    private static final AtomicLong idCounter = new AtomicLong(1); // Import java.util.concurrent.atomic.AtomicLong

    public synchronized User addUser(User user) {
        // Ensure the ID is unique
        if (user.getId() == null) {
            long maxId = users.stream()
                    .mapToLong(User::getId)
                    .max()
                    .orElse(0); // Default to 0 if the list is empty
            user.setId(Math.max(maxId + 1, idCounter.getAndIncrement()));
        } else if (users.stream().anyMatch(existingUser -> existingUser.getId().equals(user.getId()))) {
            throw new IllegalArgumentException("User ID already exists: " + user.getId());
        }

        users.add(user);

        try {
            saveToJson(); // Save changes to JSON
        } catch (IOException e) {
            throw new RuntimeException("Failed to save users to JSON", e);
        }

        return user;
    }


    public Optional<User> getUserById(Long id) {
        return users.stream().filter(user -> Objects.equals(user.getId(), id)).findFirst();
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public boolean removeUser(Long id) {
        return users.removeIf(user -> Objects.equals(user.getId(), id));
    }

    public void saveToJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), users);
    }

    public void loadFromJson() throws IOException {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            ObjectMapper mapper = new ObjectMapper();
            List<User> loadedUsers = mapper.readValue(file, new TypeReference<List<User>>() {});
            users.clear();
            users.addAll(loadedUsers);

            // Update the idCounter to ensure IDs are unique
            long maxId = users.stream().mapToLong(User::getId).max().orElse(0);
            idCounter.set(maxId + 1); // Set idCounter to one greater than the current max ID
        }
    }

    public void updateUserBalance(Long id, double newBalance) {
        getUserById(id).ifPresent(user -> {
            user.setBalance(newBalance);
            try {
                saveToJson(); // Persist changes
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void updateUserStocks(Long id, Map<String, Integer> newStocks) {
        getUserById(id).ifPresent(user -> {
            user.setStocks(newStocks);
            try {
                saveToJson(); // Persist changes
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
