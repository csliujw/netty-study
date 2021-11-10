package netty.quick.start;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class QuickServer {
    public static void main(String[] args) {
        // 1. 服务器端的启动器。负责组装 netty 组件，启动服务器
        new ServerBootstrap()
                // 2. Group 类似我们前面写的 BoosEventLoop  WorkerEventLoop(selector,thread)
                .group(new NioEventLoopGroup())
                // 3.选择一个 ServerChannel 的实现。 OIO 其实就是 BIO
                .channel(NioServerSocketChannel.class)
                // 4.BOSS 负责处理连接， worker(child) 负责处理读写，决定了 worker(child) 能执行哪些操作
                .childHandler(
                        // 5.和客户端进行数据读写的通道。channel 代表和客户端进行数据读写的通道 Initializer 初始化，负责添加别的 handler
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                // 6.添加具体的 handler
                                ch.pipeline().addLast(new StringDecoder()); // 将 ByteBuf 转换为字符串
                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        ctx.fireChannelRead(msg);
                                        System.out.println(msg);
                                    }
                                });
                            }
                        })
                // 7.绑定监听端口
                .bind(8080);
        System.out.println(123);
    }
}
