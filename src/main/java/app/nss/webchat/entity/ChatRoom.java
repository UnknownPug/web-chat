package app.nss.webchat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "chat_room")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    @Size(max = 15, min = 2, message = "Your room name should be greater than 2 and less than 15")
    private String name;

    @Column(name = "description", nullable = false)
    @Size(max = 255, min = 2, message = "Your room description should be greater than 2 and less than 255")
    private String description;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "participants",
            joinColumns = @JoinColumn(name = "chat_room_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @ToString.Exclude
    private List<User> participants;

    @JsonIgnore
    @OneToMany(mappedBy = "room")
    @ToString.Exclude
    private List<Message> messages;
}
