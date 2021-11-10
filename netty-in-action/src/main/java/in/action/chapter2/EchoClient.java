package in.action.chapter2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 梳理一下：
 * - NioEventLoop 轮询 Channel
 * - Bootstrap 实例；并指定 NioEventLoop 处理 实例中的连接
 * - channel 指定 socket 的类型
 * - handler 新连接来到时如何处理
 * ---- initChannel ，为新连接指定用什么 handler 处理新连接。
 * - bootstrap.connect(this.host, this.port).sync(); 连接并等待连接完成
 * - sync.channel().closeFuture().sync(); 异步阻塞当前线程（main线程）直到 channel 关闭，然后关闭线程池并释放所有的资源
 */
public class EchoClient {
    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) {
        EchoClient localhost = new EchoClient("localhost", 80);
        localhost.start();
    }

    private void start() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        EchoClientHandler echoClientHandler = new EchoClientHandler();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(echoClientHandler);
                        }
                    });
            ChannelFuture sync = bootstrap.connect(this.host, this.port).sync();
            sync.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
