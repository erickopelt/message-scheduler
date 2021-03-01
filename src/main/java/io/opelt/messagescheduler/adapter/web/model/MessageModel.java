package io.opelt.messagescheduler.adapter.web.model;

import java.time.LocalDateTime;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.opelt.messagescheduler.domain.MessageChannel;
import io.opelt.messagescheduler.domain.MessageStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Relation(collectionRelation = "messages", itemRelation = "message")
public class MessageModel extends RepresentationModel<MessageModel> {

    private final String id;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SS")
    private final LocalDateTime schedule;
    private final String recipient;
    private final String body;
    private final MessageChannel channel;
    private final MessageStatus status;
}
