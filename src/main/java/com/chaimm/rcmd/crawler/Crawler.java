package com.chaimm.rcmd.crawler;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 大闲人柴毛毛
 * @date 2018/2/7 上午9:14
 *
 * @description 爬虫的顶层抽象类
 * 每个Crawler对象为一个平台的爬虫
 * 本类作为一个定时任务，因此需要实现Runnable接口
 */
public abstract class Crawler implements Runnable {

    /** 每个爬虫私有的线程池 */
    protected ThreadPoolExecutor executor;

    /** 本平台爬虫的启动时间(h) */
    protected long startDelayTime;

    /** 本平台爬虫的间隔时间执行(h) (从上一次定时任务执行完成后开始计时) */
    protected long period;


    /**
     * 本平台的爬取过程
     * PS：每个平台爬取过程不一样，因此需要各平台的爬虫自行实现
     */
    public abstract void crawl();

    /**
     * 供子类设置本平台爬虫开始时间
     */
    protected abstract void setStartDelayTime();

    /**
     * 供子类设置本平台爬虫的执行间隔
     */
    protected abstract void setPeriod();

    /**
     * 供子类设置本平台爬虫所需的线程池
     */
    protected abstract void setExecutor();

    /**
     * 供Starter获取本平台爬虫的启动时延
     * @return
     */
    public long getStartDelayTime() {
        return startDelayTime;
    }

    /**
     * 供Starter获取本平台爬虫的执行间隔
     * @return
     */
    public long getPeriod() {
        return period;
    }

    /**
     * 供定时线程池调用
     * PS:Starter负责在系统初始化时，将各个爬虫作为定时任务，扔进线程池；
     * 其中，任务调度周期由各个爬虫实现类自行设置；
     * 当到期，则调用各个爬虫的run()，而run()又调用了爬虫们格子的crawl()函数
     */
    @Override
    public void run() {
        crawl();
    }
}
