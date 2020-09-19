package com.facef.rabbitmqparkinglot.business;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageBusiness {
  public void processMessage(Message message) {
    log.info("Processing message: {}", message.toString());
    throw new AmqpRejectAndDontRequeueException("Business Rule Exception");
  }

  public void processMessageFromDlq(Message message) {
    log.info("Processing message from DLQ: {}", message.toString());
    throw new AmqpRejectAndDontRequeueException("Business Rule Exception");
  }
}
