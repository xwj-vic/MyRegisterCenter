package com.xwj.registerCenter.server.entity;

/**
 * 给节点续约
 */
public class LeaseInfo<T> {

    T object;

    //默认过期时间，eureka默认也是90s,故用90s（参考eureka，总没坏处）
    // 90s内没收到心跳
    public static final int DEFULT_LEASE_TIME = 90;

    //节点最后活跃时间
    private long lastActivityTime;

    //每次续期时间
    // 这里跟eureka不一样， eureka是当前时间加上 90s(默认没有收到心跳的过期时间)，以此来续期。
    // 这里单独给一个续期时间，避免歧义，日后自己再回头翻看以前写代码的时候，也不会觉得混乱
    private long timeLimit;

    //剔除节点的时间
    private long expelTime;

    public LeaseInfo(T object, long lastActivityTime, long timeLimit) {
        this.object = object;
        this.lastActivityTime = lastActivityTime;
        this.timeLimit = timeLimit;
    }


    /**
     * 简单粗暴的续约，eureka对续约这块也很简单粗暴
     */
    public void renew() {
        this.lastActivityTime = System.currentTimeMillis() + this.timeLimit;
    }

    /**
     * 判断过期，印象中记得翻看eureka源码的时候，关于续约或过期这块，eureka写的是有问题的，原因是共用了同一个变量，具体是哪个想不起来了，太细节了，
     * 怪当初没对这块做笔记啊
     *
     * @return
     */
    public boolean isExpired() {
        return expelTime > 0 || System.currentTimeMillis() > (lastActivityTime + timeLimit);
    }


    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public long getLastActivityTime() {
        return lastActivityTime;
    }

    public void setLastActivityTime(long lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    public long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public long getExpelTime() {
        return expelTime;
    }

    public void setExpelTime(long expelTime) {
        this.expelTime = expelTime;
    }
}
