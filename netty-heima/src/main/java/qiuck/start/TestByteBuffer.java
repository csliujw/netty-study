package qiuck.start;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j(topic = "c.TestByteBuffer")
public class TestByteBuffer {
    public static void main(String[] args) {
        try (FileChannel channel = new FileInputStream("\\data.txt").getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(8);

            while (true) {
                int read = channel.read(buffer);
                log.debug("读到的字节数 {}", read);

                if (read == -1) break;
                buffer.flip(); // 切换到读取模式
                while (buffer.hasRemaining()) {
                    byte b = buffer.get();
                }
                // 读完一次后要切换为写模式
                buffer.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
