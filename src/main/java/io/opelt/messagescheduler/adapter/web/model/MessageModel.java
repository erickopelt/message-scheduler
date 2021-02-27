package io.opelt.messagescheduler.adapter.web.model;

import io.opelt.messagescheduler.domain.MessageChannel;
import io.opelt.messagescheduler.domain.MessageStatus;
import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@Getter
@Builder
@Relation(collectionRelation = "messages", itemRelation = "message")
public class MessageModel extends RepresentationModel<MessageModel> {

    private final String id;
    private final LocalDateTime schedule;
    private final String recipient;
    private final String body;
    private final MessageChannel channel;
    private final MessageStatus status;
}
