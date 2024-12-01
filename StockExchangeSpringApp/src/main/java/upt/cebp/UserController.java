package upt.cebp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserDatabase userDatabase;
    // Add a new user
    @PostMapping("/add")
    public String addUser(@RequestBody User user) {
        userDatabase.addUser(user);
        return "User added successfully!";
    }

    // Get all users
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userDatabase.getAllUsers();
    }

    // Get user by name
    @GetMapping("/{name}")
    public Optional<User> getUser(@PathVariable String name) {
        return userDatabase.getUser(name);
    }

    // Update user balance
    @PutMapping("/{name}/balance")
    public String updateBalance(@PathVariable String name, @RequestParam double balance) {
        userDatabase.updateBalance(name, balance);
        return "Balance updated successfully!";
    }

    // Remove a user by name
    @DeleteMapping("/{name}")
    public String removeUser(@PathVariable String name) {
        boolean removed = userDatabase.removeUser(name);
        return removed ? "User removed successfully!" : "User not found!";
    }

    // Add stock to a user
    @PostMapping("/{name}/stocks")
    public String addStock(@PathVariable String name, @RequestParam String ticker, @RequestParam int quantity) {
        Optional<User> user = userDatabase.getUser(name);
        if (user.isPresent()) {
            user.get().addOrUpdateStock(ticker, quantity);
            return "Stock added successfully!";
        }
        return "User not found!";
    }

    // Get user stocks
    @GetMapping("/{name}/stocks")
    public Map<String, Integer> getUserStocks(@PathVariable String name) {
        Optional<User> user = userDatabase.getUser(name);
        return user.map(User::getStocks).orElse(Collections.singletonMap("error", -1));
    }


}
