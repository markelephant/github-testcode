package io.netty.example.study.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.study.common.RequestMessage;

public class ResponseDispatherHandler extends SimpleChannelInboundHandler<RequestMessage> {
    private RequestPendingCenter requestPendingCenter;

    public ResponseDispatherHandler(RequestPendingCenter requestPendingCenter){
        this.requestPendingCenter = requestPendingCenter;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RequestMessage requestMessage) throws Exception {
        System.out.println(1111);
        requestPendingCenter.set(requestMessage.getMessageHeader().getStreamId(),requestMessage.getMessageBody());
    }
}
