package com.xwj.registerCenter.client.context;

/**
 * 客户端服务发现暂时未做具体实现
 * 实现思路也很简单
 * 创建两个线程池：服务发现线程池，心跳连接线程池
 * 服务发现线程池：定时全量拉取或增量拉取注册信息
 * 心跳连接线程池：定时调用服务端的renew,进行心跳续约
 */
public class RegisterClientContext {


    public RegisterClientContext() {

    }


}
