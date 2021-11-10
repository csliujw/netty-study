package netty.quick.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class TestByteBuf {
    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        // PooledUnsafeDirectByteBuf(ridx: 0, widx: 0, cap: 256)
        // ridx 读指针     widx 写指针
        System.out.println(buf);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            builder.append("a.txt");
        }
        buf.writeBytes(builder.toString().getBytes());
        System.out.println(buf);
    }
}
