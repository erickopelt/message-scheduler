package io.opelt.messagescheduler.adapter.database.entity;

import io.opelt.messagescheduler.domain.MessageChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MessageEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    private LocalDateTime schedule;
    private String recipient;
    private String body;
    @Enumerated(EnumType.STRING)
    private MessageChannel channel;
}
