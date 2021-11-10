package netty.quick.channel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

public class ChannelFutureClient {
    public static void main(String[] args) throws InterruptedException {
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                // 1. 连接到服务器
                // 异步非阻塞， main 发起了调用，真正执行 connect 的是 nio 线程。
                .connect("localhost", 8080);
//        System.out.println(channelFuture.channel()); // 1
//        ChannelFuture sync = channelFuture.sync();// 2
//        System.out.println(channelFuture.channel()); // 3
//        sync.channel().writeAndFlush("hello");


        System.out.println(channelFuture.channel()); // 1 [id: 0x077235fb]
        channelFuture.addListener(new ChannelFutureListener() {
            // 在 NIO 线程连接建立好之后，会调用 operationComplete
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Channel channel = future.channel();
                System.out.println(channelFuture.channel()); // 2 [id: 0x077235fb, L:/127.0.0.1:7468 - R:localhost/127.0.0.1:8080]
                System.out.println(channel); // 2 [id: 0x077235fb, L:/127.0.0.1:7468 - R:localhost/127.0.0.1:8080]

                channel.writeAndFlush("Hello, I am addListener");
            }
        });
    }
}
