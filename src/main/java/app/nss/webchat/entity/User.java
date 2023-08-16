package app.nss.webchat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@ToString
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
    @ToString.Exclude
    private List<ChatRoom> chatRoom;

    @JsonIgnore
    @OneToMany(mappedBy = "sender")
    @ToString.Exclude
    private List<Message> messages;

    @JsonIgnore
    @OneToMany(mappedBy = "recipient")
    @ToString.Exclude
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
}
