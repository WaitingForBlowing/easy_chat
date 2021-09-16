package com.moozlee.easy_chat.server.websocket;

import com.moozlee.easy_chat.server.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebsocketServer {

    @Setter
    private String contextPath;
    private ServerBootstrap bootstrap;
    private EventLoopGroup boss;
    private EventLoopGroup worker;

    public WebsocketServer(final String contextPath) {
        this.contextPath = contextPath;
    }

    public void start(final short port) {
        this.init();
        ChannelFuture future = null;
        try {
            future = bootstrap.bind(port).sync();
            log.info("server started listen on: {}!", port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        bootstrap = new ServerBootstrap();
        // TCP参数设置
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);

        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup(5);

        bootstrap.group(boss, worker)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel sc) throws Exception {
                    ChannelPipeline pipeline = sc.pipeline();
                    pipeline.addLast(new HttpServerCodec());
                    pipeline.addLast(new ChunkedWriteHandler());
                    pipeline.addLast(new HttpObjectAggregator(64*1024));
                    pipeline.addLast(new WebSocketServerProtocolHandler("/chat"));
                    pipeline.addLast(new WebSocketHandler());
                }
            });
    }
}
