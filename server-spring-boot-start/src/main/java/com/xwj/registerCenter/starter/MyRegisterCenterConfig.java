package com.xwj.registerCenter.starter;

import com.xwj.registerCenter.server.controller.RegisterCenterController;
import com.xwj.registerCenter.server.entity.InstanceConfig;
import com.xwj.registerCenter.server.register.Register;
import com.xwj.registerCenter.server.servlet.RegisterCenterDispatcherServlet;
import com.xwj.registerCenter.server.servlet.ResourceInstance;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.servlet.Servlet;
import java.util.Arrays;

@Configuration
@Import(ServerContext.class) //做容器初始化 以及服务剔除功能实现(定时器)
@ConditionalOnBean(ServerAutoConfigMarker.class)
@EnableConfigurationProperties(RegisterConfigProperties.class)
public class MyRegisterCenterConfig {


    @Bean
    public RegisterCenterController registerCenterController(Register register, InstanceConfig instanceConfig) {
        RegisterCenterController registerCenterController = new RegisterCenterController(register, instanceConfig);
        return registerCenterController;
    }

    @Bean
    public InstanceConfig instanceConfig() {
        return new InstanceConfigImpl();
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean(ResourceInstance resourceInstance) {
        ServletRegistrationBean<Servlet> servletServletRegistrationBean = new ServletRegistrationBean<>();
        RegisterCenterDispatcherServlet dispatcherServlet = new RegisterCenterDispatcherServlet(resourceInstance);
        servletServletRegistrationBean.setServlet(dispatcherServlet);
        servletServletRegistrationBean.setLoadOnStartup(1);
        servletServletRegistrationBean.setUrlMappings(Arrays.asList("*"));
        return servletServletRegistrationBean;
    }

    @Bean
    public ResourceInstance resourceInstance(RegisterCenterController registerCenterController) {
        ResourceInstance resourceInstance = new ResourceInstanceImpl();
        resourceInstance.getResourceInstances().put(RegisterCenterController.class, registerCenterController);
        return resourceInstance;
    }

    @Bean
    public Register register() {
        EventInstanceRegister register = new EventInstanceRegister();
        return register;
    }

}
