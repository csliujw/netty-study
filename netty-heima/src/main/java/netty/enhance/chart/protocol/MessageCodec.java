package netty.enhance.chart.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import netty.enhance.chart.message.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // 1. 加入四个字节的魔数字
        out.writeBytes(new byte[]{1, 2, 3, 4});
        // 2. 加入版本
        out.writeByte(1);
        // 3. 序列化算法，用 jdk 作为序列化算法。1 字节的序列化方式
        out.writeByte(0);
        // 4. 1 字节的指令类型。为什么指令类型是一个字节？
        out.writeByte(msg.getMessageType());
        // 5. 请求序号 四个字节
        out.writeInt(msg.getSequenceId());
        // 无意义，仅对齐填充
        out.writeByte(0xff);
        // 6. 长度
        // 7. 读取内容的字节数组
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeObject(msg);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        // 写入长度
        out.writeInt(bytes.length);
        // 写入内容
        out.writeBytes(bytes);
    }

    @Override
    // netty 约定了，解码的结果要存到 List 里面去，不然接下来的 handler 拿不到结果。
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("=============");
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        int len = in.readInt();
        byte[] bytes = new byte[len];
        ByteBuf buf = in.readBytes(bytes, 0, len);

        Message msg = null;
        System.out.println(serializerType);
        if (serializerType == 0) {// jdk 序列化方式
            // 把 bytes 数组读出来。
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            msg = (Message) objectInputStream.readObject();
            // magicNum = 16909060 就是 十六进制的 01020304
            log.debug("{} {} {} {} {} {}", magicNum, version, serializerType, messageType, sequenceId, len);
            log.debug("{}", msg);
            out.add(msg);// 确保后面的 handler 可以拿到数据。
        }
    }
}
