package com.cty.springnettyim.domain.rabbitmq.queue;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TaskQueueDaemonThread {

    private TaskQueueDaemonThread() {
    }

    private static class LazyHolder {
        private static TaskQueueDaemonThread taskQueueDaemonThread =
                new TaskQueueDaemonThread();
    }

    public static TaskQueueDaemonThread getInstance() {
        return LazyHolder.taskQueueDaemonThread;
    }

    Executor executor = Executors.newFixedThreadPool(20);

    private Thread daemonThread;

    public void init() {
        daemonThread = new Thread(() -> execute());
        daemonThread.setDaemon(true);
        daemonThread.setName("Task Queue Daemon Thread");
        daemonThread.start();
    }

    private void execute() {
        log.info("Successfully initializing TaskQueueDaemonThread");
        while (true) {
            try {
                // 从延迟队列中取值,如果没有对象过期则队列一直等待，
                Task t1 = t.take();
                if (t1 != null) {
                    // 修改问题的状态
                    Runnable task = t1.getTask();
                    if (task == null) {
                        continue;
                    }
                    executor.execute(task);
                    log.info("[at task:" + task + "] [Time:" + System.currentTimeMillis() + "]");
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private DelayQueue<Task> t = new DelayQueue<>();

    public void put(long time, Runnable task) {
        // 转换成ns
        long nanoTime = TimeUnit.NANOSECONDS.convert(time, TimeUnit.MILLISECONDS);
        // 创建一个任务
        Task k = new Task(nanoTime, task);
        // 将任务放在延迟的队列中
        t.put(k);
    }

    public boolean endTask(Task<Runnable> task){
        return t.remove(task);
    }
}
