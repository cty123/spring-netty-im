package com.cty.springnettyim.domain.netty.handler;

import com.cty.springnettyim.infrastructure.proto.MessageProto;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("springProtocolInitializer")
public class StringProtocolInitalizer extends ChannelInitializer<SocketChannel> {

    @Autowired
    ImServerHandler serverHandler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("decoder",
                new ProtobufDecoder(MessageProto.ClientMsg.getDefaultInstance()));
        ch.pipeline().addLast("encoder",
                new ProtobufEncoder());
        ch.pipeline().addLast(new ImServerHandler());
    }


}
