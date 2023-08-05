package app.nss.webchat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false)
    @Size(max = 20, min = 2, message = "Your username should be greater than 2 and less than 20 ")
    private String username;

    @Column(name = "email", nullable = false)
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE, message = "Email must contain valid tags.")
    private String email;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    @Size(max = 30, min = 8, message = "Your password should be greater than 8 and less than 20")
    private String password;

    @Column(name = "avatar")
    private String avatar;

    @JsonIgnore
    @ManyToMany(mappedBy = "participants")
    private List<ChatRoom> chatRoom;

    @JsonIgnore
    @OneToMany(mappedBy = "sender")
    private List<Message> messages;

    @JsonIgnore
    @OneToMany(mappedBy = "recipient")
    private List<Notification> notifications;

    public User() { // default constructor
        this.role = Role.USER;
        this.userStatus = UserStatus.ONLINE;
    }

    public User(Role role) {
        this.role = role;
        this.userStatus = UserStatus.ONLINE;
    }

    public void encodePassword(PasswordEncoder encoder) {
        this.password = encoder.encode(password);
    }

    public void erasePassword() {
        this.password = null;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<ChatRoom> getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(List<ChatRoom> chatRoom) {
        this.chatRoom = chatRoom;
    }

    public List<Message> getMessage() {
        return messages;
    }

    public void setMessage(List<Message> message) {
        this.messages = message;
    }

    public List<Notification> getNotification() {
        return notifications;
    }

    public void setNotification(List<Notification> notification) {
        this.notifications = notification;
    }

    @Override
    public String toString() {
        return "User{" +
                "role=" + role +
                ", id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", status=" + userStatus + '\'' +
                ", avatar=" + avatar + '\'' +
                ", chatRoom=" + chatRoom + '\'' +
                ", message=" + messages + '\'' +
                ", notification=" + notifications +
                '}';
    }
}
