package upt.cebp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class UserDatabase {

    private final List<User> users = new ArrayList<>();
    private long nextId = 1;
    private static final String FILE_PATH = "users.json";

    public User addUser(User user) {
        if (user.getId() == null) {
            user.setId(nextId++);
        }
        users.add(user);
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
            nextId = users.stream().mapToLong(User::getId).max().orElse(0) + 1;
        }
    }
}
