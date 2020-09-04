package com.xwj.registerCenter.server.enums;

/**
 * 节点状态
 */
public enum InstanceStatus {

    UP("up",0),
    DOWN("DOWN",1),
    UNKNOWN("Unkown", 2);

    private String name;
    private int  value;

    InstanceStatus(String name, int value) {
        this.name = name;
        this.value = value;
    }
}
