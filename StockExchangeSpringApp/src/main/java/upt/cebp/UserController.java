package upt.cebp;

import org.springframework.http.HttpStatus;
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
    public User createUser(@RequestBody User user) {
        User createdUser = userDatabase.addUser(user);
        saveDatabase();
        return createdUser;
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
}
