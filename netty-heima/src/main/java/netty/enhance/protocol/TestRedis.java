package netty.enhance.protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.Charset;

public class TestRedis {
    /**
     * set name hello
     * 3个元素，每个命令 键值的长度
     * set 命令是3个字节 $3
     * name 是四个字节 $4
     * hello 是五个字节 $8
     * 多个命令间要用回车换行。
     */
    public static void main(String[] args) {
        final byte[] LINE = new byte[]{13, 10};// 13 回车 10 换行
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(worker);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(
                            new ChannelInboundHandlerAdapter() {
                                // 连接建立就执行，发送数据
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ByteBuf buf = ctx.alloc().buffer();
                                    buf.writeBytes("*3".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("$3".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("set".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("$4".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("name".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("$5".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("Hello".getBytes());
                                    buf.writeBytes(LINE);
                                    ctx.writeAndFlush(buf);
                                    super.channelActive(ctx);
                                }

                                @Override
                                // 接收 redis 返回的结果
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf buf = (ByteBuf) msg;
                                    String retVal = buf.toString(Charset.defaultCharset());
                                    System.out.println(retVal);
                                    super.channelRead(ctx, msg);
                                }
                            }
                    );
                }
            });
            ChannelFuture localhost = bootstrap.connect("localhost", 6379).sync();
            localhost.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }
}
