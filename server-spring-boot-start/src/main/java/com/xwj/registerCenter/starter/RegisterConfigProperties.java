package com.xwj.registerCenter.starter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("my.register.moudle")
public class RegisterConfigProperties {

    private String hostname;

    @Value("${xwj.register.client.url}")
    private String registerUrl;

    private int expelTimerMs;

    public int getExpelTimerMs() {
        return expelTimerMs;
    }

    public void setExpelTimerMs(int expelTimerMs) {
        this.expelTimerMs = expelTimerMs;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getRegisterUrl() {
        return registerUrl;
    }

    public void setRegisterUrl(String registerUrl) {
        this.registerUrl = registerUrl;
    }
}
