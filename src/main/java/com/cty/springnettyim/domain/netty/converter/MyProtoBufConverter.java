package com.cty.springnettyim.domain.netty.converter;

import com.cty.springnettyim.infrastructure.proto.MessageProto;

import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;

public class MyProtoBufConverter extends AbstractMessageConverter {

    @Override
    public Message createMessage(Object object, MessageProperties messageProperties) {
        MessageProto.NewMessageBody msg = (MessageProto.NewMessageBody) object;
        byte[] byteArray = msg.toByteArray();
        return new Message(byteArray, messageProperties);
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        byte[] byteArray = message.getBody();
        MessageProto.NewMessageBody msg = null;
        try {
            msg = MessageProto.NewMessageBody.parseFrom(byteArray);
        } catch (InvalidProtocolBufferException e) {
            throw new org.springframework.amqp.support.converter.MessageConversionException("Conversion Failed");
        }
        return msg;
    }
}
