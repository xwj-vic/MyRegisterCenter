package com.xwj.registerCenter.starter;

import com.xwj.registerCenter.server.entity.InstanceConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class InstanceConfigImpl implements InstanceConfig {

    @Autowired
    RegisterConfigProperties registerConfigProperties;

    @Override
    public String getInstanceName() {
        return registerConfigProperties.getHostname();
    }

    @Override
    public String getRegisterUrl() {
        return registerConfigProperties.getRegisterUrl();
    }

    @Override
    public long getExpelTimerMs() {
        return registerConfigProperties.getExpelTimerMs();
    }
}
