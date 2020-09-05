package com.xwj.registerCenter.server.register;


import com.xwj.registerCenter.common.entity.InstanceInfo;
import com.xwj.registerCenter.common.entity.R;

public interface Register {

    /**
     * 获取注册中心注册信息
     *
     * @return
     */
    R getInstanceInfo();

    /**
     * 获取注册中心更新的注册信息
     *
     * @return
     */
    R getUpdateInstanceInfo();


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
