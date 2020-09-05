package com.xwj.registerCenter.starter;

import com.xwj.registerCenter.common.entity.InstanceInfo;
import com.xwj.registerCenter.server.register.PeerAwareInstanceRegistryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

//发布事件
public class EventInstanceRegister extends PeerAwareInstanceRegistryImpl {

    @Autowired
    private ApplicationContext applicationContext;

    public void handleRegisterEvent(InstanceInfo info, int timeLimit, boolean isReplication) {
        pushEvent(new InstanceRegisterEvent(this, info, timeLimit, isReplication));
    }

    public void pushEvent(ApplicationEvent event) {
        applicationContext.publishEvent(event);
    }

    @Override
    public void register(InstanceInfo info, int timeLimit, boolean isReplication) {
        handleRegisterEvent(info, timeLimit, isReplication);
        super.register(info, timeLimit, isReplication);
    }

    @Override
    public boolean renew(String appName, String instanceId, boolean isReplication) {
        boolean renew = super.renew(appName, instanceId, isReplication);
        if (!renew) {
            //eureka 当续约返回false时，会重新去注册
        }
        return renew;
    }

    @Override
    public boolean cancel(String appName, String instanceId, boolean isReplication) {
        return super.cancel(appName, instanceId, isReplication);
    }


}
