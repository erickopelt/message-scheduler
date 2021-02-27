package io.opelt.messagescheduler.adapter.web.controller;

import io.opelt.messagescheduler.adapter.web.mapper.MessageModelAssembler;
import io.opelt.messagescheduler.adapter.web.model.CreateMessageModel;
import io.opelt.messagescheduler.adapter.web.model.MessageModel;
import io.opelt.messagescheduler.usecase.ScheduleMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.hateoas.IanaLinkRelations.SELF;

@RestController
@RequestMapping("/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageModelAssembler modelAssembler;
    private final ScheduleMessage scheduleMessage;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<MessageModel> create(@RequestBody @Valid CreateMessageModel createMessageModel) {
        var createMessage = modelAssembler.toDomain(createMessageModel);
        var savedMessage = scheduleMessage.schedule(createMessage);
        var mappedMessage = modelAssembler.toModel(savedMessage);
        return ResponseEntity
                .created(mappedMessage.getRequiredLink(SELF).toUri())
                .body(mappedMessage);
    }
}
