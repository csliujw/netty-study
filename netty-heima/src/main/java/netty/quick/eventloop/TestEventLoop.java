package netty.quick.eventloop;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j(topic = "c.TestEventLoop")
public class TestEventLoop {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup(2); // io 事件、普通任务、定时任务
//        DefaultEventLoopGroup eventExecutors1 = new DefaultEventLoopGroup();// 普通任务、定时任务

        // 执行普通事件.把任务提交给了事件循环组的某一个事件循环对象去执行。
        group.next().submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                log.debug("ok");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 执行定时任务。启动一个定时任务，以一定的频率执行
        // initialDelay 初始延时事件，线程启动后 1s 才运行。period 2，每隔2s 运行一次
        group.next().scheduleAtFixedRate(() -> {
            log.debug("fixed rate");
        }, 1, 2, TimeUnit.SECONDS);
        log.debug("main");
    }
}
