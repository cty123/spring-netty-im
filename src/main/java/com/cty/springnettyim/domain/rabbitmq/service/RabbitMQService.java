package com.cty.springnettyim.domain.rabbitmq.service;

import com.cty.springnettyim.infrastructure.proto.MessageProto;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("rabbitService")
@AllArgsConstructor
public class RabbitMQService {

    private final AmqpTemplate amqpTemplate;

    public void sendMessage(String key, MessageProto.NewMessageBody msg) {
        amqpTemplate.convertAndSend(key, msg);
    }


}
