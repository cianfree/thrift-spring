package org.apache.thrift.spring.supports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Arvin
 * @time 2017/3/2 17:19
 */
public abstract class TimerTask {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** 第一次启动延迟 */
    private long initialDelay = 0;

    /** 间隔时间， 默认是60秒 */
    private long interval = 60000;

    /** 时间单元, 默认是毫秒 */
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    /**
     * 单位是毫秒
     *
     * @param interval
     */
    public TimerTask(long interval) {
        this.interval = interval;
    }

    public TimerTask(long interval, TimeUnit timeUnit) {
        this.interval = interval;
        this.timeUnit = timeUnit;
    }

    public TimerTask(long initialDelay, long interval, TimeUnit timeUnit) {
        this.initialDelay = initialDelay;
        this.interval = interval;
        this.timeUnit = timeUnit;
    }

    /**
     * 执行业务方法
     */
    protected abstract void executeBusiness();

    private ScheduledExecutorService service;

    /**
     * 定时器开始
     *
     * @return
     */
    public ScheduledExecutorService start() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    executeBusiness();
                } catch (Exception e) {
                    logger.error("TimerTask error: " + e.getMessage(), e);
                }
            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(runnable, initialDelay, interval, timeUnit);
        return service;
    }

    public void stop() {
        if (service != null) {
            service.shutdown();
        }
    }
}
