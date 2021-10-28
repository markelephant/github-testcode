package io.netty.example.study.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.example.study.client.codec.OrderFrameDecoder;
import io.netty.example.study.client.codec.OrderFrameEncoder;
import io.netty.example.study.client.codec.OrderProtocolDecoder;
import io.netty.example.study.client.codec.OrderProtocolEncoder;
import io.netty.example.study.client.handler.ClientIdleCheckHandler;
import io.netty.example.study.client.handler.KeepAliveSendHandler;
import io.netty.example.study.common.RequestMessage;
import io.netty.example.study.common.order.OrderOperation;
import io.netty.example.study.util.IdUtil;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;

public class NettyClient {

    public static void main(String[] args) throws InterruptedException, SSLException {

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        NioEventLoopGroup group = new NioEventLoopGroup();

        try{
            KeepAliveSendHandler keepAliveSendHandler = new KeepAliveSendHandler();
            SslContext sslContext = SslContextBuilder.forClient().build();
            bootstrap.group(group)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("sslHadnler",sslContext.newHandler(ch.alloc()));
                            pipeline.addLast(new ClientIdleCheckHandler());
                            pipeline.addLast(new LoggingHandler(LogLevel.INFO))
                                    .addLast(new OrderFrameDecoder())
                                    .addLast(new OrderFrameEncoder())
                                    .addLast(new OrderProtocolEncoder())
                                    .addLast(new OrderProtocolDecoder())
                                    .addLast(keepAliveSendHandler);
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8090);
            channelFuture.sync();
            RequestMessage requestMessage = new RequestMessage(IdUtil.nextId(), new OrderOperation(1001, "tudou"));
            channelFuture.channel().writeAndFlush(requestMessage);
            channelFuture.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }
    }

}
