package com.moozlee.easy_chat.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("connected from address: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("connection close from address: {}", ctx.channel().remoteAddress());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        if (!(o instanceof TextWebSocketFrame)) {
            log.error("receive error message type {}", o.getClass().getName());
            return;
        }

        TextWebSocketFrame request = (TextWebSocketFrame) o;
        log.info("receive text: {} from address: {}", request.text(), ctx.channel().remoteAddress());
        ctx.writeAndFlush(new TextWebSocketFrame(request.text()));
    }
}
