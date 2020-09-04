package com.xwj.registerCenter.server.enums;

/**
 * 节点行为
 */
public enum InstanceAction {
    Register("Register", 0), //注册
    Heartbeat("Heartbeat", 1), //续约
    Cancel("Cancel", 2); //下架

    private String name;
    private int value;

    InstanceAction(String name, int value) {
        this.name = name;
        this.value = value;
    }
}
