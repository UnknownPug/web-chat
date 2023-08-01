package com.example.webchat.service;

import com.example.webchat.entity.Role;
import com.example.webchat.entity.User;
import com.example.webchat.entity.UserStatus;
import com.example.webchat.exception.ApplicationException;
import com.example.webchat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "User with id " + id + " not found.")
        );
    }

    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User with username " + username + " not found.");
        }
        return user;
    }

    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User with email " + email + " not found.");
        }
        return user;
    }

    public User getUserByKeyword(String keyword) {
        User user = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
        if (user == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User with keyword " + keyword + " not found.");
        }
        return user;
    }

    public List<User> getChatRoomsByUserId(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "User with id " + id + " not found.")
        );
        return userRepository.findAllChatRoomsById(user.getId());
    }

    public User createUser(String username, String password, String email) {
        User user = new User();
        return setProfileInfo(username, password, email, user);
    }

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

    public void uploadAvatar(Long id, String avatar) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "User with id " + id + " not found.")
        );
        user.setAvatar(avatar);
        userRepository.save(user);
    }
    // ------------------------ User Status (Offline/Online) --------------------------------
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

    public void updateUserById(Long id, String username, String password, String email) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "User with id " + id + " not found.")
        );
        if (username != null && !username.isEmpty() && !Objects.equals(user.getUsername(), username)) {
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
        if (email != null && !email.isEmpty() && !Objects.equals(user.getEmail(), email)) {
            user.setEmail(email);
        } else {
            throw new ApplicationException(
                    HttpStatus.BAD_REQUEST, "Email must be filled completely or this email is already set.");
        }
        userRepository.save(user);
    }

    public void updateUserStatusById(Long id, UserStatus status) {
        User user  = userRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "User with id " + id + " not found.")
        );
        if (status == null) {
            throw new ApplicationException(
                    HttpStatus.BAD_REQUEST, "Email must be filled completely or this email is already set.");
        }
        user.setUserStatus(status);
        userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "User with id " + id + " not found.")
        );
        userRepository.delete(user);
    }
}