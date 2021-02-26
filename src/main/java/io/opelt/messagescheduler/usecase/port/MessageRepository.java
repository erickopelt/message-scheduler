package io.opelt.messagescheduler.usecase.port;

import io.opelt.messagescheduler.domain.CreateMessage;
import io.opelt.messagescheduler.domain.Message;

public interface MessageRepository {

    Message save(CreateMessage message);
}
