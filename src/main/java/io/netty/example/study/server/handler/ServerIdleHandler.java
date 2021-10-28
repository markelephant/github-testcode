package io.netty.example.study.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class ServerIdleHandler extends IdleStateHandler {

    public ServerIdleHandler() {
        super(10, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        if(evt == IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT){
            System.out.println("connection close");
            ctx.close();
            return;
        }
        super.channelIdle(ctx, evt);
    }
}
