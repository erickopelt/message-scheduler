package io.opelt.messagescheduler.adapter.web.controller;

import static org.springframework.hateoas.IanaLinkRelations.SELF;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.opelt.messagescheduler.adapter.web.mapper.MessageModelAssembler;
import io.opelt.messagescheduler.adapter.web.model.CreateMessageModel;
import io.opelt.messagescheduler.adapter.web.model.MessageModel;
import io.opelt.messagescheduler.domain.Message;
import io.opelt.messagescheduler.domain.MessageChannel;
import io.opelt.messagescheduler.domain.MessageFilter;
import io.opelt.messagescheduler.domain.MessageStatus;
import io.opelt.messagescheduler.usecase.CancelScheduleMessage;
import io.opelt.messagescheduler.usecase.FindMessage;
import io.opelt.messagescheduler.usecase.ScheduleMessage;
import io.opelt.messagescheduler.usecase.exception.MessageScheduleMustHaveAFutureValueException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final PagedResourcesAssembler<Message> pagedResourcesAssembler;
    private final MessageModelAssembler modelAssembler;
    private final ScheduleMessage scheduleMessage;
    private final FindMessage findMessage;
    private final CancelScheduleMessage cancelScheduleMessage;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaTypes.HAL_JSON_VALUE)
    ResponseEntity<MessageModel> create(@RequestBody @Valid CreateMessageModel createMessageModel) throws MessageScheduleMustHaveAFutureValueException {
        var createMessage = modelAssembler.toDomain(createMessageModel);
        var savedMessage = scheduleMessage.schedule(createMessage);
        var mappedMessage = modelAssembler.toModel(savedMessage);
        return ResponseEntity
                .created(mappedMessage.getRequiredLink(SELF).toUri())
                .body(mappedMessage);
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    ResponseEntity<PagedModel<MessageModel>> getPage(
            @RequestParam(value = "status", required = false) MessageStatus status,
            @RequestParam(value = "channel", required = false) MessageChannel channel,
            Pageable pageable) throws Exception {
        var filter = MessageFilter.builder()
                .status(status)
                .channel(channel)
                .pageable(pageable).build();
        var page = findMessage.findByFilter(filter);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, modelAssembler));
    }

    @GetMapping(path = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
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
