package com.cty.springnettyim.client;

import com.cty.springnettyim.infrastructure.proto.MessageProto;
import com.google.protobuf.Timestamp;
import io.netty.channel.Channel;

import java.util.Date;

public class client1 {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 8090;
        Channel channel = new ImConnection().connect(host, port);

        sendMessage(channel);
    }

    public static void login(Channel channel) {
        MessageProto.ClientMsg.LoginBody loginBody = MessageProto.ClientMsg.LoginBody.newBuilder()
                .setUsername("ctydw")
                .setPassword("password")
                .build();

        MessageProto.ClientMsg msg = MessageProto.ClientMsg.newBuilder()
                .setMsgType(MessageProto.ClientMsg.MsgType.LOGIN)
                .setLoginBody(loginBody)
                .build();

        channel.writeAndFlush(msg);
    }

    public static void sendMessage(Channel channel) {
        MessageProto.NewMessageBody msg = MessageProto.NewMessageBody.newBuilder()
                .setSender("test user")
                .setReceiver("cty")
                .setContent("hello 11111")
                .setUuid("1")
                .setDate(Timestamp.newBuilder().setSeconds(new Date().getTime() / 1000)).build();

        MessageProto.ClientMsg message = MessageProto.ClientMsg.newBuilder()
                .setMsgType(MessageProto.ClientMsg.MsgType.NEW_MESSAGE)
                .setNewMessageBody(msg)
                .build();

        channel.writeAndFlush(message);
    }
}
