package com.xwj.registerCenter.starter;



import com.xwj.registerCenter.server.servlet.ResourceInstance;

import java.util.HashMap;
import java.util.Map;

public class ResourceInstanceImpl implements ResourceInstance {

    Map<Class,Object> resourcesMap = new HashMap<>();

    @Override
    public Map<Class, Object> getResourceInstances() {
        return resourcesMap;
    }
}
