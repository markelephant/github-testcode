package io.netty.example.study.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.example.study.server.codec.OrderFrameDecoder;
import io.netty.example.study.server.codec.OrderFrameEncoder;
import io.netty.example.study.server.codec.OrderProtocolDecoder;
import io.netty.example.study.server.codec.OrderProtocolEncoder;
import io.netty.example.study.server.handler.MetricsHandler;
import io.netty.example.study.server.handler.OrderServerProcessHandler;
import io.netty.example.study.server.handler.ServerIdleHandler;
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.timeout.IdleStateHandler;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

public class NettyServer {

    public static void main(String[] args) throws InterruptedException, CertificateException, SSLException {

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        try{
            MetricsHandler metricsHandler = new MetricsHandler();
            //创建一个自签证书
            SelfSignedCertificate selfSigned = new SelfSignedCertificate();
            System.out.println("证书位置"+selfSigned.certificate());
            System.out.println("证书密钥"+selfSigned.privateKey());
            SslContext sslContext = SslContextBuilder.forServer(selfSigned.certificate(), selfSigned.privateKey()).build();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast( "sslHandler",sslContext.newHandler(ch.alloc()));
                            pipeline.addLast(metricsHandler);
                            pipeline.addLast(new ServerIdleHandler());
                            pipeline.addLast(new OrderFrameDecoder())
                                    .addLast(new OrderFrameEncoder());
                            pipeline.addLast(new OrderProtocolEncoder())
                                    .addLast(new OrderProtocolDecoder())
                                    //开启消息接受优化，10消息之后，才flush，如果是异步开启schdule任务
                                    .addLast("flushEnhancer",new FlushConsolidationHandler(10,true))
                                    .addLast(new LoggingHandler(LogLevel.INFO))
                                    .addLast(new IdleStateHandler(5,0,0, TimeUnit.SECONDS))
                                    .addLast(new OrderServerProcessHandler());
                        }
                    });

            ChannelFuture closeFutrue = serverBootstrap.bind(8090).sync();
            closeFutrue.channel().closeFuture().sync();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

}
