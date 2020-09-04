package com.xwj.registerCenter.server.register;

import com.xwj.registerCenter.server.entity.InstanceInfo;
import com.xwj.registerCenter.server.entity.LeaseInfo;
import com.xwj.registerCenter.server.enums.InstanceStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class InstanceRegister implements Register {

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock read = readWriteLock.readLock();

    /**
     * eureka就是这么设计的，这个map设计的很巧妙
     * 最外层Key是application name
     * value的map 的key是instanceID,服务实例ID ， value对应的具体服务
     * 这样就可以管理一个application聚合内的同种类型的实例了
     */
    private ConcurrentHashMap<String, Map<String, LeaseInfo<InstanceInfo>>> registerMap = new ConcurrentHashMap();

    /**
     * eureka还会有缓存和自我保护机制，先不实现，一步步来
     */
    @Override
    public void register(InstanceInfo info, int timeLimit, boolean isReplication) {
        try {
            read.lock();
            System.out.println("服务注册 info:" + info);
            if (registerMap.containsKey(info.getInstanceName())) {
                Map<String, LeaseInfo<InstanceInfo>> gMap = registerMap.get(info.getInstanceName());
                LeaseInfo<InstanceInfo> existingLease = gMap.get(info.getInstanceId());
                if (existingLease != null && existingLease.getObject() != null) { //解决重复注册
                    Long existingLastDirtyTimestamp = existingLease.getObject().getLastDirtyTimestamp(); //获取当前存在的实例的最后操作时间戳
                    Long registrationLastDirtyTimestamp = info.getLastDirtyTimestamp(); //获取传进来的实例的最后操作时间戳
                    if (existingLastDirtyTimestamp > registrationLastDirtyTimestamp) {
                        info = existingLease.getObject();
                    }
                }
                LeaseInfo<InstanceInfo> lease = new LeaseInfo<InstanceInfo>(info, System.currentTimeMillis(), timeLimit);
                if (existingLease != null) { //当冲突的时候，会设置最后一次正常工作时间
                    lease.setLastActivityTime(existingLease.getLastActivityTime()); //设置最后一次正常工作时间
                }
                gMap.put(lease.getObject().getInstanceId(), lease);
                lease.getObject().setInstanceStatus(InstanceStatus.UP);
            }
        } finally {
            read.unlock();
        }
    }

    @Override
    public boolean renew(String appName, String instanceId, boolean isReplication) {
        System.out.println("心跳续约");
        Map<String, LeaseInfo<InstanceInfo>> gMap = registerMap.get(appName);
        if (gMap == null) return false;
        LeaseInfo<InstanceInfo> leaseInfo = gMap.get(instanceId);
        if (leaseInfo.getObject().getInstanceStatus() == InstanceStatus.UNKNOWN)
            return false;
        leaseInfo.renew();
        return true;
    }

    @Override
    public boolean cancel(String appName, String instanceId, boolean isReplication) {
        System.out.println("服务下架");
        return internalCancel(appName, instanceId, isReplication);
    }

    @Override
    public boolean eviction() {
        System.out.println("服务剔除");

        return true;
    }

    private boolean internalCancel(String appName, String instanceId, boolean isReplication) {
        try {
            read.lock();
            Map<String, LeaseInfo<InstanceInfo>> gMap = registerMap.get(appName);
            if (gMap == null) return false;
            LeaseInfo<InstanceInfo> leaseInfo = gMap.get(instanceId);
            if (leaseInfo == null) return false;
            gMap.remove(instanceId);
        } finally {
            read.unlock();
        }
        return true;
    }


}
