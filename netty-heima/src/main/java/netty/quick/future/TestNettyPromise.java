package netty.quick.future;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TestNettyPromise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        DefaultPromise<Integer> promise = new DefaultPromise<>(group.next());
        Thread th = new Thread(() -> {
            try {
                // 开启线程，计算完毕后向 promise 填充结果。
                System.out.println("开始计算");
                int i = 1 / 0;
                TimeUnit.SECONDS.sleep(3);
                System.out.println("计算完成");
                // 设置值
                promise.setSuccess(80);
            } catch (InterruptedException e) {
                promise.setFailure(e);
                e.printStackTrace();
            }
        });
        th.start();
        log.debug("等待结果");
        log.debug("结果是{}", promise.get());
    }
}
