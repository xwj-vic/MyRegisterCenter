package com.xwj.registerCenter.server.register;


import com.xwj.registerCenter.server.entity.InstanceInfo;

public interface Register {

    /**
     * 注册
     * @param info
     * @param timeLimit
     * @param isReplication
     */
    void register(InstanceInfo info, int timeLimit, boolean isReplication);

    /**
     * 续约
     * @param appName
     * @param instanceId
     * @param isReplication
     */
    boolean renew(String appName, String instanceId, boolean isReplication);

    /**
     * 退出
     * @param appName
     * @param instanceId
     * @param isReplication
     */
    boolean cancel(String appName, String instanceId, boolean isReplication);


    /**
     * 剔除
     */
    boolean eviction();
}
