package com.xwj.registerCenter.server.servlet;

import com.xwj.registerCenter.server.annotation.Controller;
import com.xwj.registerCenter.server.annotation.RequestMapping;
import com.xwj.registerCenter.server.annotation.ResponseResources;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class RegisterCenterDispatcherServlet extends HttpServlet {

    private String projectPath = this.getClass().getResource("/").getPath();
    private String prefix = "";
    private String suffix = "";
    private Map<String, Method> methodMap = new HashMap();

    private ResourceInstance resourceInstance;

    public RegisterCenterDispatcherServlet(ResourceInstance resourceInstance) {
        this.resourceInstance = resourceInstance;
    }

    @Override
    public void init() throws ServletException {
        projectPath = getPath();
        projectPath = projectPath.replaceAll("%20", " ");
        scanProjectByPath(projectPath + "/");
    }

    public String getPath() {
        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        if (System.getProperty("os.name").contains("dows")) {
            path = path.substring(1, path.length());
        }
        if (path.contains("jar")) {
            path = path.substring(0, path.lastIndexOf("."));
            return path.substring(0, path.lastIndexOf("/"));
        }
        return path.replace("target/classes/", "");
    }

    public void scanProjectByPath(String path) {
        File file = new File(path);
        scanFile(file);
    }

    /**
     * 启动扫描所有的controller，放进map里
     *
     * @param file
     */
    public void scanFile(File file) {
        if (file.isDirectory()) {
            for (File file1 : Objects.requireNonNull(file.listFiles())) {
                scanFile(file1);
            }
        } else {
            String path = file.getPath();
            if (path.substring(path.lastIndexOf(".")).equals(".jar")) {
                JarFile jarFile = null;
                try {
                    jarFile = new JarFile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    String elementName = entries.nextElement().toString();
                    String classPath = elementName.replaceAll("/", ".");

                    String className = classPath.substring(0, classPath.lastIndexOf("."));
                    int i = classPath.lastIndexOf(".");
                    if (i != -1 && classPath.substring(i).equals(".class")) {
                        try {
                            Class<?> clazz = Class.forName(className);
                            boolean annotationPresent = clazz.isAnnotationPresent(Controller.class);
                            if (annotationPresent) {
                                RequestMapping annotation = clazz.getAnnotation(RequestMapping.class);

                                String classRequestMappingPath = "";
                                if (annotation != null) {
                                    classRequestMappingPath = annotation.value();
                                }
                                //解析所有方法
                                for (Method method : clazz.getMethods()) {
                                    RequestMapping methodRequestMapping = method.getAnnotation(RequestMapping.class);
                                    if (methodRequestMapping != null) {
                                        String requestMapping = methodRequestMapping.value();
                                        methodMap.put(classRequestMappingPath + requestMapping, method);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String requestURI = request.getRequestURI();
            Method method = methodMap.get(requestURI);
            if (method != null) {
                Parameter[] parameters = method.getParameters();
                Object[] objects = new Object[parameters.length];

                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    //
                    String name = parameter.getName();
                    Class<?> type = parameter.getType();
                    if (type.equals(HttpServletRequest.class)) {
                        objects[i] = request;
                    } else if (type.equals(HttpServletResponse.class)) {
                        objects[i] = response;
                    } else if (type.equals(String.class)) {
                        objects[i] = request.getParameter(name);
                    } else {
                        Object o = type.newInstance();
                        for (Field field : type.getDeclaredFields()) {
                            field.setAccessible(true);
                            String fieldName = field.getName();
                            if (field.getType().equals(int.class)) {
                                field.set(o, Integer.parseInt(request.getParameter(fieldName)));
                            } else {
                                field.set(o, request.getParameter(fieldName));
                            }
                        }
                        objects[i] = o;
                    }
                }
                Class<?> declaringClass = method.getDeclaringClass();
                Object o = resourceInstance.getResourceInstances().get(declaringClass);
                if (o == null) {
                    o = declaringClass.newInstance();
                }
                Object invoke = method.invoke(o, objects);
                String s = String.valueOf(invoke);
                response.setContentType("text/html;charset=utf-8");
                if (method.isAnnotationPresent(ResponseResources.class)) {
                    response.getWriter().write(s);
                    response.getWriter().flush();
                } else {
                    if (invoke.getClass().equals(String.class)) {
                        request.getRequestDispatcher(prefix + s + suffix).forward(request, response);
//                    response.sendRedirect();
                    }
                }
            } else {
                response.setStatus(404);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
