package com.example.webchat.entity;

import jakarta.persistence.*;

import javax.validation.constraints.Size;
import java.util.List;

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

    @ManyToMany
    @JoinTable(
        name = "participants",
        joinColumns = @JoinColumn(name = "chat_room_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants;

    @OneToMany(mappedBy = "room")
    private List<Message> message;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<User> getUsers() {
        return participants;
    }

    public void setUsers(List<User> users) {
        this.participants = users;
    }

    public List<Message> getMessage() {
        return message;
    }

    public void setMessage(List<Message> message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", participants=" + participants +
                ", message=" + message +
                '}';
    }
}
