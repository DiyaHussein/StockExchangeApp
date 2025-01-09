package upt.cebp;

import java.util.Optional;
import java.util.List;

public interface UserService {
    Optional<User> findById(Long id); // Find user by ID
    User save(User user);            // Save or update user
    List<User> findAll();            // Get all users
}