package com.cty.springnettyim.rabbitmq.service;

import com.cty.springnettyim.netty.proto.MessageProto;
import com.google.protobuf.Timestamp;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("rabbitService")
public class RabbitMQService {

    @Autowired
    AmqpTemplate amqpTemplate;

    public void sendMessage(String key, MessageProto.NewMessageBody msg) {
        amqpTemplate.convertAndSend("test", msg);
    }
}
