package com.facef.rabbitmqparkinglot.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DirectExchangeConfiguration {

  public static final String DIRECT_EXCHANGE_NAME = "order-exchange";
  public static final String ORDER_MESSAGES_QUEUE_NAME = "order-messages-queue";
  public static final String ORDER_MESSAGES_QUEUE_DLQ_NAME = ORDER_MESSAGES_QUEUE_NAME + ".dlq";
  public static final String ORDER_MESSAGES_QUEUE_PARKING_LOT_NAME = ORDER_MESSAGES_QUEUE_NAME + ".parkingLot";

  @Bean
  Queue orderMessagesQueue() {
    return QueueBuilder.durable(ORDER_MESSAGES_QUEUE_NAME)
        .withArgument("x-dead-letter-exchange", "")
        .withArgument("x-dead-letter-routing-key", ORDER_MESSAGES_QUEUE_DLQ_NAME)
        .build();
  }

  @Bean
  Queue orderMessagesDeadLetterQueue() {
    return QueueBuilder.durable(ORDER_MESSAGES_QUEUE_DLQ_NAME).build();
  }

  @Bean
  Queue orderMessagesParkingLotQueue() {
    return QueueBuilder.durable(ORDER_MESSAGES_QUEUE_PARKING_LOT_NAME).build();
  }

  @Bean
  DirectExchange exchange() {
    return ExchangeBuilder.directExchange(DIRECT_EXCHANGE_NAME).durable(true).build();
  }

  @Bean
  Binding bindingOrderMessagesQueue(
      @Qualifier("orderMessagesQueue") Queue queue, DirectExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(ORDER_MESSAGES_QUEUE_NAME);
  }

  @Bean
  Binding bindingOrderMessagesParkingLotQueue(
      @Qualifier("orderMessagesParkingLotQueue") Queue queue, DirectExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(ORDER_MESSAGES_QUEUE_PARKING_LOT_NAME);
  }
}
