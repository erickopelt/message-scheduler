package io.opelt.messagescheduler.usecase.port;

import io.opelt.messagescheduler.domain.Message;

import java.util.Optional;

public interface MessageRepository {

    Message save(Message message);

    Optional<Message> findById(String id);

    void delete(Message message);
}
