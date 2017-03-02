package org.apache.thrift.spring.client.impl;

import org.apache.thrift.spring.config.TSConfig;
import org.apache.thrift.spring.supports.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 定时刷新服务器列表
 *
 * @author Arvin
 * @time 2017/3/2 14:26
 */
public abstract class AbstractTimerTSConfigProvider extends AbstractTSConfigProvider {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** 定时刷新间隔，单位是毫秒，默认是60秒更新一次 */
    private int interval = 60000;

    public AbstractTimerTSConfigProvider(int interval) {
        this.interval = interval;
        initTimer();
    }

    private void initTimer() {
        // 开启定时器，每隔一段时间就去刷新配置列表
        TimerTask timerTask = new TimerTask(0, interval, TimeUnit.MILLISECONDS) {
            @Override
            protected void executeBusiness() {
                try {
                    logger.debug("---------------------------------------------------------------------------------------------------------");
                    logger.debug("重新同步加载Thrift服务器列表.....");
                    List<TSConfig> configList = syncLoadTSConfig();
                    setConfigList(configList);
                    logger.debug("重新同步加载Thrift服务器列表成功.....");
                    logger.debug("新的服务器列表：");
                    for (TSConfig config : configList) {
                        logger.debug(TSConfig.serialize(config));
                    }
                    logger.debug("---------------------------------------------------------------------------------------------------------");
                } catch (Exception e) {
                    logger.error("重新同步加载Thrift服务器列表失败.....", e);
                }
            }
        };
        timerTask.start();
    }

    public int getInterval() {
        return interval;
    }

    /**
     * 同步加载Thrift Server config
     */
    protected abstract List<TSConfig> syncLoadTSConfig();
}
