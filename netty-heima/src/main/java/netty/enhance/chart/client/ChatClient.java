package netty.enhance.chart.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import netty.enhance.chart.message.LoginRequestMessage;
import netty.enhance.chart.protocol.MessageCodecSharable;
import netty.enhance.chart.protocol.ProcotolFrameDecoder;

import java.io.IOException;
import java.util.Scanner;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG); // 日志工具
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable(); // 消息编码解码。无状态，可共享的。
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                // 加入与 业务无关的 handler
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder()); // 记录后封装成 frame
                    ch.pipeline().addLast(LOGGING_HANDLER);// 编码后记录日志
                    ch.pipeline().addLast(MESSAGE_CODEC);// 拿到消息后，编码
                    ch.pipeline().addLast("client handler", new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            System.out.println("=============");
                            log.debug("接收到的消息为 {}", msg);
                            super.channelRead(ctx, msg);
                        }

                        // 这是一个入栈处理器，执行了写入操作就会触发出栈操作
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            // 正确建立后出发 active 事件
                            // 用户输入是一个阻塞IO，需要单独开启一个线程
                            new Thread(() -> {
                                Scanner scanner = new Scanner(System.in);
                                System.out.println("请输入用户名");
                                String name = scanner.nextLine();
                                System.out.println("请输入密码");
                                String password = scanner.nextLine();
                                // 构造消息对象
                                LoginRequestMessage message = new LoginRequestMessage(name, password);
                                // 发送消息
                                ctx.writeAndFlush(message);
                                System.out.println("等待后续操作");
                                try {
                                    System.in.read();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }, "System in").start();
                            super.channelActive(ctx);
                        }
                    });
                }
                // 连接建立之后就发送登录请求。
            });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
