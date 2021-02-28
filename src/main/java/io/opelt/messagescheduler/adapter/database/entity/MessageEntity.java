package io.opelt.messagescheduler.adapter.database.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import io.opelt.messagescheduler.domain.MessageChannel;
import io.opelt.messagescheduler.domain.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MESSAGE")
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
    @Enumerated(EnumType.STRING)
    private MessageStatus status;
}
