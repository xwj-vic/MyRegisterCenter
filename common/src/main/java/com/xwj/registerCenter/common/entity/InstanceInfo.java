package com.xwj.registerCenter.common.entity;

import com.xwj.registerCenter.common.enums.InstanceStatus;

/**
 * 节点信息
 */
public class InstanceInfo {

    private String  ipAddr;

    private  String hostName;

    private String instanceId;

    private String instanceName;

    private int timeLimit;

    private String port;

    private InstanceStatus instanceStatus;

    private Long lastDirtyTimestamp;

    public InstanceInfo() {
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public InstanceStatus getInstanceStatus() {
        return instanceStatus;
    }

    public void setInstanceStatus(InstanceStatus instanceStatus) {
        this.instanceStatus = instanceStatus;
    }

    public Long getLastDirtyTimestamp() {
        return lastDirtyTimestamp;
    }

    public void setLastDirtyTimestamp(Long lastDirtyTimestamp) {
        this.lastDirtyTimestamp = lastDirtyTimestamp;
    }

    @Override
    public String toString() {
        return "InstanceInfo{" +
                "ipAddr='" + ipAddr + '\'' +
                ", hostName='" + hostName + '\'' +
                ", instanceId='" + instanceId + '\'' +
                ", instanceName='" + instanceName + '\'' +
                ", timeLimit=" + timeLimit +
                ", port='" + port + '\'' +
                ", instanceStatus=" + instanceStatus +
                ", lastDirtyTimestamp=" + lastDirtyTimestamp +
                '}';
    }
}
