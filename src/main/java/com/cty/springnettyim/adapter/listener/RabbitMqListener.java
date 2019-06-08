package com.cty.springnettyim.adapter.listener;

import com.cty.springnettyim.domain.netty.handler.ImServerHandler;
import com.cty.springnettyim.infrastructure.proto.MessageProto;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RabbitMqListener {

    public static ConcurrentHashMap<String, MessageProto.NewMessageBody> ackWaitList = new ConcurrentHashMap<>();

    @RabbitListener(queues = "server1")
    public void listen(Message msg) {
        // Get Message Body
        byte[] bytes = msg.getBody();

        try {
            // Get Parse Message from bytes
            MessageProto.NewMessageBody message = MessageProto.NewMessageBody.parseFrom(bytes);

            // 1. Get the target ChannelHandlerContext
            String receiver = message.getReceiver();
            ChannelHandlerContext target_ctx = ImServerHandler.ctx_map.get(receiver);

            // 2. Write the message and wait for ack
            // 2.(1) Add Message to a wait list
            String uuid = message.getUuid();
            ackWaitList.put(uuid, message);

            // 2.(2) Write the message to the client
            if (target_ctx != null) {
                MessageProto.ServerMsg m = MessageProto.ServerMsg.newBuilder()
                        .setNewMessageBody(message)
                        .setMsgType(MessageProto.ServerMsg.MsgType.NEW_MESSAGE_NOTIFICATION)
                        .build();
                target_ctx.writeAndFlush(m);
            }

            // 3. Write ACK for ack


            // 4. Return success to the source


        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }
}
