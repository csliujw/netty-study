package netty.quick.pipeline;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class TestPipelineServer {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // 1. 通过 channel 拿到 pipeline
                        ChannelPipeline pipeline = ch.pipeline();
                        // ch 从尾巴开始找，ctx 从当前开始向前找出站处理器。
                        // ctx 是每个 addLast 内部类方法的
                        // 2. 默认会添加处理器 head -> tail。加入一个自己的后变成了 head -> h1 -> tail
                        pipeline.addLast("h1", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                String name = buf.toString(StandardCharsets.UTF_8);
                                log.debug("1");
                                super.channelRead(ctx, name); // channelRead 内部调用了 ctx.fireChannelRead(msg); 将消息传递给下一个 hanlder
                            }
                        });

                        // head -> h1 ->h2 -> tail
                        pipeline.addLast("h2", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                Student student = new Student();
                                student.name = msg.toString();
                                log.debug("2");
                                super.channelRead(ctx, student);
                            }
                        });
                        // head -> h1 ->h2 -> h3 -> tail
                        pipeline.addLast("h3", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("3,结果{}", msg);
                                super.channelRead(ctx, msg);
                                // 分配了一个 buf 对象，然后写入一些字节。（为了触发出战处理器）
                                ch.writeAndFlush(ctx.alloc().buffer().writeBytes("xxx".getBytes()));
                            }
                        });
                        // 出战处理器，只有你向 channel 里写了数据才会触发。出战是从尾巴向前走。
                        // head -> h1 ->h2 -> h3 -> h4 -> tail
                        pipeline.addLast("h4", new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("4");
                                super.write(ctx, msg, promise);
                            }
                        });

                        // head -> h1 ->h2 -> h3 -> h4 -> h5 -> tail
                        pipeline.addLast("h5", new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("5");
                                super.write(ctx, msg, promise);
                            }
                        });

                    }
                })
                .bind(8080);
    }

    static class Student {
        String name;
    }
}
