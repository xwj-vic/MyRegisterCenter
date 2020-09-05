package com.xwj.registerCenter.server.controller;

import com.xwj.registerCenter.common.entity.R;
import com.xwj.registerCenter.server.annotation.Controller;
import com.xwj.registerCenter.server.annotation.RequestMapping;
import com.xwj.registerCenter.server.annotation.ResponseResources;
import com.xwj.registerCenter.server.entity.InstanceConfig;
import com.xwj.registerCenter.common.entity.InstanceInfo;
import com.xwj.registerCenter.server.entity.LeaseInfo;
import com.xwj.registerCenter.server.register.Register;


/**
 * 对外暴露接口，与客户端通信
 */
@Controller
public class RegisterCenterController {

    private Register register;

    private InstanceConfig instanceConfig;

    public RegisterCenterController(Register register, InstanceConfig instanceConfig) {
        this.register = register;
        this.instanceConfig = instanceConfig;
    }

    @RequestMapping("/register")
    @ResponseResources
    public R register(InstanceInfo instanceInfo, String isSync) {
        if (isNull(instanceInfo.getHostName())) {
            return R.error("HostName 不能为空").set("code", 400);
        } else if (isNull(instanceInfo.getInstanceId())) {
            return R.error("InstanceId 不能为空").set("code", 400);
        } else if (isNull(instanceInfo.getInstanceName())) {
            return R.error("InstanceName 不能为空").set("code", 400);
        } else if (isNull(instanceInfo.getPort())) {
            return R.error("Port 不能为空").set("code", 400);
        } else if (isNull(instanceInfo.getIpAddr())) {
            return R.error("IpAddr 不能为空").set("code", 400);
        }
        int timeLimit = LeaseInfo.DEFULT_LEASE_TIME;
        if (instanceInfo.getTimeLimit() > 0) {
            timeLimit = instanceInfo.getTimeLimit();
        }
        register.register(instanceInfo, timeLimit, isSync != null && isSync.equals("true"));
        return R.success();
    }

    @RequestMapping("/renew")
    @ResponseResources
    public R renew(String instanceName, String instanceId, String isSync) {
        if (isNull(instanceName)) {
            return R.error("instanceName 不能为空").set("code", 400);
        } else if (isNull(instanceId)) {
            return R.error("InstanceId 不能为空").set("code", 400);
        }
        register.renew(instanceName, instanceId, isSync != null && isSync.equals("true"));
        return R.success();
    }

    @RequestMapping("/cancel")
    @ResponseResources
    public R cancel(String instanceName, String instanceId, String isSync) {
        if (isNull(instanceName)) {
            return R.error("instanceName 不能为空").set("code", 400);
        } else if (isNull(instanceId)) {
            return R.error("instanceId 不能为空").set("code", 400);
        }
        register.cancel(instanceName, instanceId, isSync != null && isSync.equals("true"));
        return R.success();
    }

    public boolean isNull(String str) {
        return str == null || str.isEmpty();
    }


}
