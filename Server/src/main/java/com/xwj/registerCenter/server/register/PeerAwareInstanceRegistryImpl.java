package com.xwj.registerCenter.server.register;

import com.xwj.registerCenter.server.entity.InstanceInfo;
import com.xwj.registerCenter.server.enums.InstanceAction;
import com.xwj.registerCenter.server.enums.InstanceStatus;

/**
 * 责任链模式， isReplication为true表示集群同步
 */
public class PeerAwareInstanceRegistryImpl extends InstanceRegister {

    @Override
    public void register(InstanceInfo info, int timeLimit, boolean isReplication) {
        super.register(info, timeLimit, isReplication);
        replicateToPeers(InstanceAction.Register, info.getInstanceName(), info.getInstanceId(), info, null, isReplication);
    }

    @Override
    public boolean renew(String appName, String instanceId, boolean isReplication) {
        if (super.renew(appName, instanceId, isReplication)) {
            replicateToPeers(InstanceAction.Heartbeat, appName, instanceId, null, null, isReplication);
            return true;
        }
        return false;
    }

    @Override
    public boolean cancel(String appName, String instanceId, boolean isReplication) {
        if (super.cancel(appName, instanceId, isReplication)) {
            replicateToPeers(InstanceAction.Cancel, appName, instanceId, null, null, isReplication);
            return true;
        }
        return false;
    }

    @Override
    public boolean eviction() {
        super.eviction();
        return false;
    }


    /**
     * 模拟处理集群同步
     * @param action
     * @param appName
     * @param id
     * @param info
     * @param newStatus
     * @param isReplication
     */
    public void replicateToPeers(InstanceAction action, String appName, String id,
                                 InstanceInfo info /* optional */,
                                 InstanceStatus newStatus /* optional */, boolean isReplication) {
        if (isReplication)
            System.out.println("集群同步");
    }
}
