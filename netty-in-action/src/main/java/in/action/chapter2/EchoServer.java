package in.action.chapter2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * 梳理一下：
 * - NioEventLoop 轮询 Channel
 * - ServerBootstrap 实例；并指定 NioEventLoop 处理 实例中的连接
 * - 为 ServerBootstrap 指定传输类型为 NioServerSocketChannel
 * - 为 ServerBootstrap 绑定 ChannelInitializer，
 * ---- ChannelInitializer 中的 initChannel 中为 添加的连接绑定 handler
 * - 绑定监听的端口，异步等带好绑定的结束
 * - 异步阻塞当前线程（main线程）直到 channel 关闭，然后关闭线程池并释放所有的资源
 */
@Slf4j
public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        int port = 8080;
        log.debug("hello");
        new EchoServer(port).start();
    }

    private void start() {
        EchoServerHandler handler = new EchoServerHandler();
        NioEventLoopGroup group = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            ServerBootstrap b = serverBootstrap.group(group) // 创建一个 ServerBootstrap 实例，并指定 NioEventLoopGroup 来接受和处理新的连接。
                    .channel(NioServerSocketChannel.class) // 指定所使用的 NIO 传输 Channel，类型为 NioServerSocketChannel
                    .childHandler(new ChannelInitializer<SocketChannel>() { // ChannelInitializer ==> 当一个新的连接被接受时，一个新的子 Channel 将会被创建
                        // 而 ChannelInitializer 会把一个你的 EchoServerHandler 的实例添加到该 Channel 的 ChannelPipeline 中
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(handler); // 此处是多个新连接共享一个 handler
                        }
                    });
            ChannelFuture sync = b.bind(80).sync(); // 异步调用绑定服务器，sync 等待服务器绑定结束
            sync.channel().closeFuture().sync(); // 获取 Channel 的 CloseFuture 并阻塞当前线程，直到它完成。
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

}
