package netty.quick.channel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
@Slf4j
public class BadCloseFutureClient {
    public static void main(String[] args) throws InterruptedException {
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // 需要在 logback 里进行配置
                        /**
                         *    <logger name="io.netty.handler.logging.LoggingHandler" level="debug" additivity="false">
                         *         <appender-ref ref="STDOUT"/>
                         *     </logger>
                         * */
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new StringEncoder());
                    }
                }).connect("localhost", 8080);
        Channel channel = channelFuture.sync().channel();

        new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            while (true) {
                String s = sc.nextLine();
                if ("q".equals(s)) {
                    channel.close();
                    log.debug("处理关闭之后的操作");
                    return;
                }
                channel.writeAndFlush(s);
            }
        }, "client-send-msg").start();
    }
}
