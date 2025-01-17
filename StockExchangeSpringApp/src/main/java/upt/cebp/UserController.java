package upt.cebp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserDatabase userDatabase = new UserDatabase();

    public UserController() {
        // Load existing data from JSON on startup
        try {
            userDatabase.loadFromJson();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userDatabase.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userDatabase.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        // Validate required fields
        if (user.getName() == null || user.getName().isEmpty()) {
            return ResponseEntity.badRequest().body("User name is required");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required");
        }

        // Set default values for optional fields
        if (user.getBalance() == 0) {
            user.setBalance(0);
        }
        if (user.getStocks() == null) {
            user.setStocks(new HashMap<>());
        }

        User createdUser = userDatabase.addUser(user);
        saveDatabase();
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        User user = userDatabase.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(updatedUser.getName());
        user.setBalance(updatedUser.getBalance());
        user.setStocks(updatedUser.getStocks());
        saveDatabase();
        return user;
    }

    @PatchMapping("/{id}/balance")
    public ResponseEntity<User> updateUserBalance(@PathVariable Long id, @RequestBody double newBalance) {
        Optional<User> userOptional = userDatabase.getUserById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        User user = userOptional.get();
        user.setBalance(newBalance);

        // Save changes to the database
        saveDatabase();

        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}/stocks")
    public ResponseEntity<User> updateUserStocks(@PathVariable Long id, @RequestBody Map<String, Integer> updatedStocks) {
        Optional<User> userOptional = userDatabase.getUserById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        User user = userOptional.get();
        user.setStocks(updatedStocks);

        // Save changes to the database
        saveDatabase();

        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        if (!userDatabase.removeUser(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    private void saveDatabase() {
        try {
            userDatabase.saveToJson();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save user data");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        return userDatabase.getAllUsers().stream()
                .filter(user -> user.getName().equals(username) && user.getPassword().equals(password))
                .findFirst()
                .map(ResponseEntity::ok) // Returns the full User object, including the ID
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null));
    }

    @GetMapping("/{id}/stocks")
    public ResponseEntity<Map<String, Integer>> getUserStocks(@PathVariable Long id) {
        Optional<User> userOptional = userDatabase.getUserById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(userOptional.get().getStocks());
    }

    @GetMapping("/{id}/stocks/{ticker}")
    public ResponseEntity<Integer> getUserStock(@PathVariable Long id, @PathVariable String ticker) {
        Optional<User> userOptional = userDatabase.getUserById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Integer quantity = userOptional.get().getStockQuantity(ticker);
        return ResponseEntity.ok(quantity != null ? quantity : 0);
    }

    /* Example API Call:
    GET http://localhost:8080/api/users/1/stocks/AAPL
     */

    @PatchMapping("/{id}/stocks/{ticker}")
    public ResponseEntity<User> addOrUpdateStock(@PathVariable Long id, @PathVariable String ticker, @RequestBody int quantity) {
        Optional<User> userOptional = userDatabase.getUserById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User user = userOptional.get();
        user.addOrUpdateStock(ticker, quantity);
        saveDatabase();
        return ResponseEntity.ok(user);
    }

    /* Example API call:
    PATCH http://localhost:8080/api/users/1/stocks/AAPL
    Content-Type: application/json
    Body: 15
     */

    @DeleteMapping("/{id}/stocks/{ticker}")
    public ResponseEntity<User> removeStock(@PathVariable Long id, @PathVariable String ticker) {
        Optional<User> userOptional = userDatabase.getUserById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User user = userOptional.get();
        user.removeStock(ticker);
        saveDatabase();
        return ResponseEntity.ok(user);
    }
    /* Example API call: DELETE http://localhost:8080/api/users/1/stocks/AAPL */
}
