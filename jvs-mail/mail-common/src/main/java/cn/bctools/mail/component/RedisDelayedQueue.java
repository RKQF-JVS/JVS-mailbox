package cn.bctools.mail.component;

import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author admin
 * @ClassName: RedisDelayedQueue
 * @Description: 延迟队列

 */
@Component
@Slf4j
public class RedisDelayedQueue {
    /**
     * 任务回调监听
     *
     * @param <T>
     */
    public interface TaskEventListener<T> {
        /**
         * 执行方法
         *
         * @param t
         */
        void invoke(T t);
    }

    @Autowired
    private RedissonClient redissonClient;

    private final Lock lock = new ReentrantLock();

    /**
     * 添加队列
     *
     * @param t        DTO传输类
     * @param delay    时间数量
     * @param timeUnit 时间单位
     * @param <T>      泛型
     */
    public <T> void addQueue(T t, Class<?> zClass, long delay, TimeUnit timeUnit) {
        RBlockingQueue<T> blockingFairQueue = redissonClient.getBlockingQueue(zClass.getName());
        RDelayedQueue<T> delayedQueue = redissonClient.getDelayedQueue(blockingFairQueue);
        delayedQueue.offer(t, delay, timeUnit);
    }

    /**
     * 获取队列
     *
     * @param zClass            DTO泛型
     * @param taskEventListener 任务回调监听
     * @param <T>               泛型
     * @return
     */
    public <T> void getQueue(Class<?> zClass, TaskEventListener<T> taskEventListener) {
        //由定时线程定时拉取队列
        CronUtil.schedule("*/1 * * * * *", (Task) () -> {
            lock.lock();
            try {
                RBlockingQueue<T> blockingFairQueue = redissonClient.getBlockingQueue(zClass.getName());
                if (blockingFairQueue.size() == 0) {
                    return;
                } else {
                    //放弃线程占用
                    Thread.sleep(0);
                }
                log.debug("获取队列数据,{}", blockingFairQueue.size());
                while (blockingFairQueue.size() > 0) {
                    T t = blockingFairQueue.take();
                    taskEventListener.invoke(t);
                }
            } catch (InterruptedException e) {
                log.error("获取队列失败{}，", e.getMessage());
            } finally {
                lock.unlock();
            }
        });
        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }
}
