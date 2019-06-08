package com.cty.springnettyim.domain.netty.handler;

import com.cty.springnettyim.infrastructure.proto.MessageProto;
import com.cty.springnettyim.adapter.listener.RabbitMqListener;
import com.cty.springnettyim.domain.rabbitmq.util.RabbitMQUtil;
import com.google.protobuf.Timestamp;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Qualifier("serverHandler")
@ChannelHandler.Sharable
@Slf4j
public class ImServerHandler extends ChannelInboundHandlerAdapter {

    public static ConcurrentHashMap<String, ChannelHandlerContext> ctx_map = new ConcurrentHashMap<>();

    private void handlePushNotification(MessageProto.NewMessageBody msg_body) {
        // 1. Find the target server through cache server
        String key = "server1";

        // 2. Ask the server to do the notification and wait for the answer
        RabbitMQUtil.rabbitMQUtil.getRabbitService().sendMessage(key, msg_body);
        log.info("Forward the message to rabbitmq");
    }

    private void handlePushNotificationCallback() {
        // 1. Send Notification Ack
    }

    private void deliverNotificaiton() {
        // 1. Get the target ChannelHandlerContext

        // 2. Write the message and wait for ack Timeout

        // 3. Write ACK for ack

        // 4. Return success to the source
    }

    private MessageProto.ServerMsg makeSendAck(MessageProto.NewMessageBody msg_body) {

        MessageProto.ServerMsg.AckSendBody ackSendBody = MessageProto.ServerMsg.AckSendBody.newBuilder()
                .setTimestamp(Timestamp.newBuilder().setSeconds(new Date().getTime() / 1000))
                .setUuid(msg_body.getUuid())
                .build();

        return MessageProto.ServerMsg.newBuilder()
                .setMsgType(MessageProto.ServerMsg.MsgType.ACK_SEND)
                .setAckSendBody(ackSendBody)
                .build();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        MessageProto.ClientMsg message = (MessageProto.ClientMsg) msg;
        MessageProto.ServerMsg reply;

        switch (message.getMsgType()) {
            case LOGIN:
                MessageProto.ClientMsg.LoginBody loginBody = message.getLoginBody();
                String username = loginBody.getUsername();
                ctx_map.put(username, ctx);

                // Construct Login Message Body
                MessageProto.ServerMsg.LoginStatusBody reply_body = MessageProto.ServerMsg.LoginStatusBody.newBuilder()
                        .setStatus(true)
                        .setMsg("Login success!").build();

                // Construct Reply
                reply = MessageProto.ServerMsg.newBuilder()
                        .setMsgType(MessageProto.ServerMsg.MsgType.LOGIN_STATUS)
                        .setLoginStatusBody(reply_body)
                        .build();

                // Write reply
                ctx.writeAndFlush(reply);
                break;
            case NEW_MESSAGE:
                MessageProto.NewMessageBody msg_body = message.getNewMessageBody();

                // Testing
                username = msg_body.getSender();
                ctx_map.put(username, ctx);

//                String receiver = msg_body.getReceiver();
//
//                Handle Offline later
//                if (!ctx_map.containsKey(receiver)) {
//                    break;
//                }

                // 1. Write ack
                reply = makeSendAck(msg_body);
                ctx.writeAndFlush(reply);
                log.info("Received Send_Message request from " + username);

                // 2. Push Notification to the target
                handlePushNotification(msg_body);

                break;
            case ACK:
                MessageProto.ClientMsg.AckBody ackBody = message.getAckBody();

                // Get Message UUID
                String uuid = ackBody.getRefUuid();

                // If we are waiting for such an ack
                if (RabbitMqListener.ackWaitList.containsKey(uuid)) {
                    // Remove the message from wait list
                    RabbitMqListener.ackWaitList.remove(uuid);

                    // Construct ACKACK body
                    MessageProto.ServerMsg.AckAckBody ackAckBody = MessageProto.ServerMsg.AckAckBody.newBuilder()
                            .setTimestamp(Timestamp.newBuilder().setSeconds(new Date().getTime() / 1000))
                            .setUuid(uuid)
                            .build();

                    // Construct ACKACK and send it back
                    reply = MessageProto.ServerMsg.newBuilder().setMsgType(MessageProto.ServerMsg.MsgType.ACK_ACK)
                            .setAckAckBody(ackAckBody)
                            .build();

                    // Write message
                    ctx.writeAndFlush(reply);
                }
                break;
            default:
                // Construct error message
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
