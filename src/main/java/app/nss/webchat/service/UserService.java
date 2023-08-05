package app.nss.webchat.service;

import app.nss.webchat.entity.Role;
import app.nss.webchat.entity.User;
import app.nss.webchat.entity.UserStatus;
import app.nss.webchat.exception.ApplicationException;
import app.nss.webchat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Cacheable(value = "usersCache", key = "{#root.methodName, #root.args}")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Cacheable(value = "usersCache", key = "{#root.methodName, #root.args}")
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "User with id " + id + " not found.")
        );
    }

    @Cacheable(value = "usersCache", key = "{#root.methodName, #root.args}")
    public User getUserByUserIdentifier(String keyword) {
        User user = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
        if (user == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User with keyword " + keyword + " not found.");
        }
        return user;
    }

    @CachePut(value = "usersCache", key = "#result.id")
    public User createUser(String username, String password, String email) {
        User user = new User();
        return setProfileInfo(username, password, email, user);
    }

    @CachePut(value = "usersCache", key = "#result.id")
    public User createAdmin(String username, String password, String email) {
        User admin = new User(Role.ADMIN);
        return setProfileInfo(username, password, email, admin);
    }

    public User setProfileInfo(String username, String password, String email, User admin) {
        if (userRepository.existsByUsername(username)) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "The name has already been taken.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "The email has already been taken.");
        }
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPassword(password);
        admin.encodePassword(passwordEncoder);
        admin.setAvatar(null); // default
        return userRepository.save(admin);
    }

    @CacheEvict(value = "usersCache", key = "#id")
    @CachePut(value = "usersCache", key = "#id")
    public void uploadAvatar(Long id, String avatar) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "User with id " + id + " not found.")
        );
        user.setAvatar(avatar);
        userRepository.save(user);
    }

    // ------------------------ User Status (Offline/Online) --------------------------------
    @CacheEvict(value = "usersCache", key = "#username")
    public void loginUser(String username) {
        // Perform login logic
        // ...
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User with username " + username + " not found.");
        }
//        if (!user.getPassword().equals(password)) {
//            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Password is incorrect.");
//        }
        // Set the user status to ONLINE
        user.setUserStatus(UserStatus.ONLINE);
        userRepository.save(user);
    }

    @CacheEvict(value = "usersCache", key = "#username")
    public void logoutUser(String username) {
        // Perform logout logic
        // ...
        User user = userRepository.findByUsername(username);
        if (user == null) { // TODO: Maybe must delete (in future)
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User with username " + username + " not found.");
        }
        // Set the user status to OFFLINE
        user.setUserStatus(UserStatus.OFFLINE);
        userRepository.save(user);
    }

    // ----------------------------------------------------------------------------------------
    @CacheEvict(value = "usersCache", key = "#id")
    @CachePut(value = "usersCache", key = "#id")
    public void updateUserById(Long id, String username, String password, String email) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "User with id " + id + " not found.")
        );
        if (username != null && !username.isEmpty() &&
                !Objects.equals(user.getUsername(), username) && !userRepository.existsByUsername(username)) {
            user.setUsername(username);
        } else {
            throw new ApplicationException(
                    HttpStatus.BAD_REQUEST, "Username must be filled completely or this username is already set.");
        }
        if (password != null && !password.isEmpty() && !Objects.equals(user.getPassword(), password)) {
            user.setPassword(password);
            user.encodePassword(passwordEncoder);
        } else {
            throw new ApplicationException(
                    HttpStatus.BAD_REQUEST, "Password must be filled completely or this password is already set.");
        }
        if (email != null && !email.isEmpty() &&
                !Objects.equals(user.getEmail(), email) && !userRepository.existsByEmail(email)) {
            user.setEmail(email);
        } else {
            throw new ApplicationException(
                    HttpStatus.BAD_REQUEST, "Email must be filled completely or this email is already set.");
        }
        userRepository.save(user);
    }

    @CacheEvict(value = "usersCache", key = "#id")
    public void updateUserStatusById(Long id, UserStatus status) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "User with id " + id + " not found.")
        );
        if (status == null) {
            throw new ApplicationException(
                    HttpStatus.BAD_REQUEST, "Email must be filled completely or this email is already set.");
        }
        user.setUserStatus(status);
        userRepository.save(user);
    }

    @CacheEvict(value = "usersCache", key = "#id")
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "User with id " + id + " not found.")
        );
        userRepository.delete(user);
    }
}