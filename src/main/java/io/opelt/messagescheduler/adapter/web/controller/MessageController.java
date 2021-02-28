package io.opelt.messagescheduler.adapter.web.controller;

import io.opelt.messagescheduler.adapter.web.mapper.MessageModelAssembler;
import io.opelt.messagescheduler.adapter.web.model.CreateMessageModel;
import io.opelt.messagescheduler.adapter.web.model.MessageModel;
import io.opelt.messagescheduler.usecase.CancelScheduleMessage;
import io.opelt.messagescheduler.usecase.FindMessage;
import io.opelt.messagescheduler.usecase.ScheduleMessage;
import io.opelt.messagescheduler.usecase.exception.MessageScheduleMustHaveAFutureValueException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.IanaLinkRelations.SELF;

@RestController
@RequestMapping("/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageModelAssembler modelAssembler;
    private final ScheduleMessage scheduleMessage;
    private final FindMessage findMessage;
    private final CancelScheduleMessage cancelScheduleMessage;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<MessageModel> create(@RequestBody @Valid CreateMessageModel createMessageModel) throws MessageScheduleMustHaveAFutureValueException {
        var createMessage = modelAssembler.toDomain(createMessageModel);
        var savedMessage = scheduleMessage.schedule(createMessage);
        var mappedMessage = modelAssembler.toModel(savedMessage);
        return ResponseEntity
                .created(mappedMessage.getRequiredLink(SELF).toUri())
                .body(mappedMessage);
    }

    @GetMapping(path = "/{id}")
    ResponseEntity<MessageModel> getById(@PathVariable("id") String id) throws Exception {
        var message = findMessage.findById(id);
        var messageModel = modelAssembler.toModel(message);
        return ResponseEntity.ok(messageModel);
    }

    @DeleteMapping(path = "/{id}")
    ResponseEntity<Void> cancel(@PathVariable("id") String id) throws Exception {
       cancelScheduleMessage.cancel(id);
       return ResponseEntity.noContent().build();
    }
}
