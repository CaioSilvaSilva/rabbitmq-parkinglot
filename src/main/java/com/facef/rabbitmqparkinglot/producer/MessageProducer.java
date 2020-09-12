package com.facef.rabbitmqparkinglot.producer;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.facef.rabbitmqparkinglot.configuration.DirectExchangeConfiguration.DIRECT_EXCHANGE_NAME;
import static com.facef.rabbitmqparkinglot.configuration.DirectExchangeConfiguration.ORDER_MESSAGES_QUEUE_NAME;

@Service
@Slf4j
public class MessageProducer {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  public void sendFakeMessage() {
    log.info("Sending a fake message...");
    this.rabbitTemplate.convertAndSend(
        DIRECT_EXCHANGE_NAME,
        ORDER_MESSAGES_QUEUE_NAME,
        "FAKE-MESSAGE-"
            .concat(LocalDateTime.now().toString())
            .concat(UUID.randomUUID().toString()));
  }
}