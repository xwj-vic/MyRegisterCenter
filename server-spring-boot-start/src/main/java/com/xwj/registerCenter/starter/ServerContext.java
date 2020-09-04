package com.xwj.registerCenter.starter;

import com.xwj.registerCenter.server.entity.InstanceConfig;
import com.xwj.registerCenter.server.register.Register;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;

import java.util.Timer;
import java.util.TimerTask;

public class ServerContext implements SmartLifecycle {

    private boolean isRunning = false;

    @Autowired
    private InstanceConfig instanceConfig;

    @Autowired
    private Register register;

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable runnable) {
        runnable.run();
    }

    @Override
    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {  //定时执行服务剔除
                    @Override
                    public void run() {
                        register.eviction();
                    }
                },instanceConfig.getExpelTimerMs(),instanceConfig.getExpelTimerMs());
            }
        }).start();
    }

    @Override
    public void stop() {
        this.isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public int getPhase() {
        return 0;
    }
}
