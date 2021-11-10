package in.action.chapter2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * - ChannelHandler 负责接收响应事件并处理
 * - ChannelInboundHandlerAdapter 是 ChannelHandler 的子类。负责处理入站响应事件。
 * - ChannelOutboundHandlerAdapter 是 ChannelHandler 的子类。负责处理出站响应事件。
 */
@Sharable // 标识一个 ChannelHandler 可以被多个 Channel 安全地共享。
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("Server recived: " + in.toString(CharsetUtil.UTF_8));
        ctx.write(in); // 将消息写给发送者
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
        // 将 未决消息（即暂存于 ChannelOutboundBuffer 中的消息，在下一次调用 flush
        // 或 writeAndFlush() 时 将会尝试写出到 socket）冲刷到远程节点，并关闭该 Channel
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
