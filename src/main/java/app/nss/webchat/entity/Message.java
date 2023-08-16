package app.nss.webchat.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "content", nullable = false)
    @Size(max = 255, min = 5, message = "Message content should be between 5 and 255 characters long.")
    private String content;

    @Column(name = "time_stamp", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm", iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime timeStamp;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "sender", nullable = false)
    private User sender;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "room", nullable = false)
    private ChatRoom room;
}
