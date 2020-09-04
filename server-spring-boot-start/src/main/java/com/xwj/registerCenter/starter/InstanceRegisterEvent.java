package com.xwj.registerCenter.starter;

import com.xwj.registerCenter.server.entity.InstanceInfo;
import org.springframework.context.ApplicationEvent;

public class InstanceRegisterEvent extends ApplicationEvent {

    private InstanceInfo instanceInfo;

    private int timeLimit;

    private  boolean isReplication;



    public InstanceRegisterEvent(Object source, InstanceInfo info, int timeLimit, boolean isReplication) {
        super(source);
        this.instanceInfo = info;
        this.isReplication = isReplication;
        this.timeLimit = timeLimit;

    }

    public InstanceInfo getInstanceInfo() {
        return instanceInfo;
    }

    public void setInstanceInfo(InstanceInfo instanceInfo) {
        this.instanceInfo = instanceInfo;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public boolean isReplication() {
        return isReplication;
    }

    public void setReplication(boolean replication) {
        isReplication = replication;
    }
}
