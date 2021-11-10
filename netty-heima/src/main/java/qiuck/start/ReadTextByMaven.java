package qiuck.start;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ReadTextByMaven {
    public static void main(String[] args) throws IOException {
        URL resource = ReadTextByMaven.class.getResource("/data.txt");
        System.out.println(resource.getPath());
        InputStream resourceAsStream = ReadTextByMaven.class.getClassLoader().getResourceAsStream("data.txt");
        byte[] buf = new byte[1024];
        int read = resourceAsStream.read(buf);
        System.out.println(new String(buf,0,buf.length));

    }
}
