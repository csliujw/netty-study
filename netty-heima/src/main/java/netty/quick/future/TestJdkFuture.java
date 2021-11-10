package netty.quick.future;

import java.util.concurrent.*;

public class TestJdkFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<Integer> future = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                TimeUnit.SECONDS.sleep(1);
                return 50;
            }
        });
        // 主线程通过 future 来获取结果
        System.out.println("等待结果");
        // 在线程间传递结果。future 是被动的，由执行任务的线程把结果填到future对象中。（任务给future，main等结果）
        Integer integer = future.get();
        System.out.println(integer);
    }
}
