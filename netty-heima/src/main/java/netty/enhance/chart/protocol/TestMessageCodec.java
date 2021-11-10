package netty.enhance.chart.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import netty.enhance.chart.message.LoginRequestMessage;

public class TestMessageCodec {
    public static void main(String[] args) throws Exception {
        // 这个是无状态的，所以是线程安全的。 netty 提供的 handler 加了 @Sharable 注解的就是线程安全，可以被多线程使用的。
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        // 多个线程使用，记录了多次消息的状态，线程不安全、不能多个线程使用。
        LengthFieldBasedFrameDecoder lengthFieldBasedFrameDecoder = new LengthFieldBasedFrameDecoder(
                1024, 12, 4, 0, 0);
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                // 帧解码器，解决粘包，半包的问题。
                // 假定最大长度 1024；长度字段的偏移量 12，数了12个后才到长度字段；长度字段共4个字节；无需调整0；无需去处前面的0，因为是我们自己解析。
                lengthFieldBasedFrameDecoder,
                loggingHandler,
                new MessageCodec());
        LoginRequestMessage message = new LoginRequestMessage("zhang san", "123");
        // 测试 encode
        embeddedChannel.writeOutbound(message);
        // decode
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, buf);
        // 测试入栈。
        embeddedChannel.writeInbound(buf);
    }
}
