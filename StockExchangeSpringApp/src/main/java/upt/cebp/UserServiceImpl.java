package upt.cebp;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserDatabase userDatabase;

    public UserServiceImpl() {
        this.userDatabase = new UserDatabase();

        // Load existing users from the JSON file
        try {
            userDatabase.loadFromJson();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load users from JSON", e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        return userDatabase.getUserById(id);
    }

    @Override
    public User save(User user) {
        User savedUser = userDatabase.addUser(user);
        saveToDatabase();
        return savedUser;
    }

    @Override
    public List<User> findAll() {
        return userDatabase.getAllUsers();
    }

    public boolean delete(Long id) {
        boolean result = userDatabase.removeUser(id);
        if (result) {
            saveToDatabase();
        }
        return result;
    }

    private void saveToDatabase() {
        try {
            userDatabase.saveToJson();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save users to JSON", e);
        }
    }
}
