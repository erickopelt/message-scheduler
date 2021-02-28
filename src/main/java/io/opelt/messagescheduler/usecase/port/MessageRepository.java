package io.opelt.messagescheduler.usecase.port;

import java.util.Optional;

import org.springframework.data.domain.Page;

import io.opelt.messagescheduler.domain.Message;
import io.opelt.messagescheduler.domain.MessageFilter;

public interface MessageRepository {

    Message save(Message message);

    Optional<Message> findById(String id);

    Page<Message> findByFilter(MessageFilter filter);

    void delete(Message message);
}
