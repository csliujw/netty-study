package netty.study.bytebuff;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

import static netty.study.utils.ByteBufferUtil.debugAll;

@Slf4j
public class TestByteBufferReadWrite {
    public static void test1() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte) 0x61);
        buffer.put(new byte[]{0x62, 0x63, 0x64});
//        debugAll(buffer);
//        System.out.println(buffer.get());
        buffer.flip();
        buffer.get();
        debugAll(buffer);
        buffer.compact();
        // 只是把未读取的数据移动到了前面而已，并不会清空数据
        // 例如 61 62 63 61被读取了，然后 compact
        // 变成 62 63 64 64
        debugAll(buffer);
    }

    public static void allocate() {
        System.out.println(ByteBuffer.allocate(10).getClass());
        System.out.println((ByteBuffer.allocateDirect(10).getClass()));
    }

    public static void main(String[] args) {
        allocate();
    }
}
