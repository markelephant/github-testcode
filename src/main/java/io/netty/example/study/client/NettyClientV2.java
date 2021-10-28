package io.netty.example.study.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.example.study.client.codec.*;
import io.netty.example.study.client.handler.OperationResultFutuee;
import io.netty.example.study.client.handler.RequestPendingCenter;
import io.netty.example.study.client.handler.ResponseDispatherHandler;
import io.netty.example.study.common.Operation;
import io.netty.example.study.common.OperationResult;
import io.netty.example.study.common.RequestMessage;
import io.netty.example.study.common.order.OrderOperation;
import io.netty.example.study.util.IdUtil;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.ExecutionException;

public class NettyClientV2 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        NioEventLoopGroup group = new NioEventLoopGroup();
        try{
            RequestPendingCenter requestPendingCenter = new RequestPendingCenter();
            bootstrap.group(group)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new OrderFrameDecoder());
                            pipeline.addLast(new OrderFrameEncoder());
                            pipeline.addLast(new OrderProtocolEncoder());
                            pipeline.addLast(new OrderProtocolDecoder());
                            pipeline.addLast(new ResponseDispatherHandler(requestPendingCenter));
                            pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8090);
            channelFuture.sync();
            long streamId = IdUtil.nextId();
            RequestMessage requestMessage = new RequestMessage(streamId,
                    new OrderOperation(1001, "tudou"));


            channelFuture.channel().writeAndFlush(requestMessage);
            OperationResultFutuee operationResultFutuee = new OperationResultFutuee();

            requestPendingCenter.add(streamId,operationResultFutuee);
            Operation operationResult = operationResultFutuee.get();

            System.out.println(operationResult);

            channelFuture.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }
    }

}
