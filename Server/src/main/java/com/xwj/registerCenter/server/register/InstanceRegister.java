package com.xwj.registerCenter.server.register;

import com.xwj.registerCenter.server.entity.InstanceInfo;
import com.xwj.registerCenter.server.entity.LeaseInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class InstanceRegister implements Register {

    /**
     * eureka就是这么设计的，这个map设计的很巧妙
     * 最外层Key是application name
     * value的map 的key是instanceID,服务实例ID ， value对应的具体服务
     * 这样就可以管理一个application聚合内的同种类型的实例了
     */
    private ConcurrentHashMap<String, Map<String, LeaseInfo<InstanceInfo>>> registerMap = new ConcurrentHashMap();

    @Override
    public void register(InstanceInfo info, int timeLimit, boolean isReplication) {
        System.out.println("服务注册 info:" + info);
    }

    @Override
    public boolean renew(String appName, String instanceId, boolean isReplication) {
        System.out.println("心跳续约");
        return true;
    }

    @Override
    public boolean cancel(String appName, String instanceId, boolean isReplication) {
        System.out.println("服务下架");
        return true;
    }

    @Override
    public boolean eviction() {
        System.out.println("服务剔除");
        return true;
    }

}
