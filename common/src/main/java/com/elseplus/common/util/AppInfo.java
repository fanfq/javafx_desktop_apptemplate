package com.elseplus.common.util;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.Properties;


@Getter
@Setter
public class AppInfo {

    private String groupId;
    private String artifactId;
    private String version;
    private String description;

    private String appAlias;

    private String appName;
    private String baseUrl;
    private String updUrl;

    // 重构单一模式实例
    private static volatile AppInfo instance = null;

    public static AppInfo getInstance() {
        synchronized (AppInfo.class){
            //不能是单例的，因为可能会在不同的上下文中使用，所以每次都要实例化
            if(instance == null){
                synchronized (AppInfo.class){
                    instance = new AppInfo();
                }
            }
        }
        return instance;
    }

    private AppInfo() {
            Properties properties = new Properties();
            try {
                properties.load(getClass().getClassLoader().getResourceAsStream("app.properties"));
                if (!properties.isEmpty()) {
                    this.artifactId = properties.getProperty("app.artifactId");
                    this.groupId = properties.getProperty("app.groupId");
                    this.version = properties.getProperty("app.version");
                    this.description = properties.getProperty("app.description");

                    this.appAlias = properties.getProperty("appAlias");
                    this.baseUrl = properties.getProperty("baseUrl");
                    this.updUrl = properties.getProperty("updUrl");
                    this.appName = properties.getProperty("appName");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }


}
