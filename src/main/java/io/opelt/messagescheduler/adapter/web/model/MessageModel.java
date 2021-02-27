package io.opelt.messagescheduler.adapter.web.model;

import io.opelt.messagescheduler.domain.MessageChannel;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@Getter
@Builder
@Relation(collectionRelation = "messages", itemRelation = "message")
public class MessageModel extends RepresentationModel<MessageModel> {

    private String id;
    private LocalDateTime schedule;
    private String recipient;
    private String body;
    private MessageChannel channel;
}
