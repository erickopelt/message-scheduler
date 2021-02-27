package io.opelt.messagescheduler.usecase.port;

import io.opelt.messagescheduler.domain.Message;

public interface MessageRepository {

    Message save(Message message);
}
