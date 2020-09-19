package com.facef.rabbitmqparkinglot.consumer;

import com.facef.rabbitmqparkinglot.business.MessageBusiness;
import com.facef.rabbitmqparkinglot.configuration.DirectExchangeConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import static com.facef.rabbitmqparkinglot.configuration.DirectExchangeConfiguration.DIRECT_EXCHANGE_NAME;
import static com.facef.rabbitmqparkinglot.configuration.DirectExchangeConfiguration.ORDER_MESSAGES_QUEUE_PARKING_LOT_NAME;

@Configuration
@Slf4j
public class MessageConsumer {

  private static final Integer NUM_MAX_ATTEMPTS_TO_RETRY_PROCESS_DLQ_QUEUE = 2;

  @Autowired
  MessageBusiness messageBusiness;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @RabbitListener(queues = DirectExchangeConfiguration.ORDER_MESSAGES_QUEUE_NAME)
  public void processOrderMessage(Message message) {
    messageBusiness.processMessage(message);
  }

  @RabbitListener(queues = DirectExchangeConfiguration.ORDER_MESSAGES_QUEUE_DLQ_NAME)
  public void processOrderMessageDlq(Message message) {
    var numAttempts = NUM_MAX_ATTEMPTS_TO_RETRY_PROCESS_DLQ_QUEUE;
    var attempts = 0;

    for (var i = 1; i <= numAttempts; i++) {
      try {
        log.info("Trying to process message {} times. Message: {}", i, message.toString());
        messageBusiness.processMessageFromDlq(message);
      } catch (AmqpException ex) {
        log.info("Error to process message from DLQ. Message: {}", message.toString());
        attempts += 1;
      }
    }

    if (attempts == numAttempts) {
      log.info("Sending message to ParkingLot. Message: {}", message.toString());
      try {
        this.rabbitTemplate.send(
            DIRECT_EXCHANGE_NAME,
            ORDER_MESSAGES_QUEUE_PARKING_LOT_NAME,
            message);
      } catch (AmqpException ex) {
        log.info("Error on send message to ParkingLot. Message: {}", message.toString());
      }
    }
  }
}
