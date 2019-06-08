package com.cty.springnettyim.client;

import com.cty.springnettyim.infrastructure.proto.MessageProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.util.AttributeKey;

public class ImConnection {
    private Channel channel;

    public Channel connect(String host, int port) {
        doConnect(host, port);
        return this.channel;
    }

    private void doConnect(String host, int port) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast("decoder",
                            new ProtobufDecoder(MessageProto.ServerMsg.getDefaultInstance()));
                    ch.pipeline().addLast("encoder",
                            new ProtobufEncoder());
                    ch.pipeline().addLast(new ClientHandler());

                }
            });

            ChannelFuture f = b.connect(host, port).sync();
            f.channel().attr(AttributeKey.valueOf("token")).set("test");
            channel = f.channel();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
