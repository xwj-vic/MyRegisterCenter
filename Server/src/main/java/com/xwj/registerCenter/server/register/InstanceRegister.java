package com.xwj.registerCenter.server.register;

import com.xwj.registerCenter.common.entity.R;
import com.xwj.registerCenter.common.enums.InstanceStatus;
import com.xwj.registerCenter.common.entity.InstanceInfo;
import com.xwj.registerCenter.server.entity.LeaseInfo;

import java.util.*;
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


    @Override
    public R getInstanceInfo() {
        return null;
    }

    @Override
    public R getUpdateInstanceInfo() {
        return null;
    }

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
        evict();
        return true;
    }

    /**
     * 思路
     * 先找出所有需要剔除的服务放进一个list里，然后分段删除
     */
    private void evict() {
        List<LeaseInfo<InstanceInfo>> expiredLeases = new ArrayList<>(); //获取所有需要剔除的服务信息，放到集合里
        for (Map.Entry<String, Map<String, LeaseInfo<InstanceInfo>>> groupEntry : registerMap.entrySet()) {
            Map<String, LeaseInfo<InstanceInfo>> leaseMap = groupEntry.getValue();
            if (leaseMap != null) {
                for (Map.Entry<String, LeaseInfo<InstanceInfo>> leaseEntry : leaseMap.entrySet()) {
                    LeaseInfo<InstanceInfo> lease = leaseEntry.getValue();
                    if (lease.isExpired() && lease.getObject() != null) {
                        expiredLeases.add(lease);
                    }
                }
            }
        }

        int registrySize = 0;
        for (Map<String, LeaseInfo<InstanceInfo>> entry : registerMap.values())
            registrySize += entry.size(); //获取所有已注册的服务数量
        int registrySizeThreshold = (int) (registrySize * 0.85); //参考eureka 0.85,这里主要获取需要保留下来的数量
        int evictionLimit = registrySize - registrySizeThreshold; //相减得到本次预计需要剔除的服务数量

        int toEvict = Math.min(expiredLeases.size(), evictionLimit); //为了安全起见，做一下比较，取最小值
        if (toEvict > 0) { //执行剔除
            Random random = new Random(System.currentTimeMillis());
            for (int i = 0; i < toEvict; i++) { //随机剔除
                // Pick a random item (Knuth shuffle algorithm)
                int next = i + random.nextInt(expiredLeases.size() - i);
                Collections.swap(expiredLeases, i, next);
                LeaseInfo<InstanceInfo> lease = expiredLeases.get(i);
                String appName = lease.getObject().getInstanceName();
                String id = lease.getObject().getInstanceId();
                internalCancel(appName, id, false); //服务下架
            }
        }
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
