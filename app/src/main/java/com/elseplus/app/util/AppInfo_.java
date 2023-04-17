package com.elseplus.app.util;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.Properties;


@Getter
@Setter
public class AppInfo_ {

    private String groupId;
    private String artifactId;
    private String version;
    private String description;

    private String appAlias;
    private String baseUrl;
    private String updUrl;

    // 重构单一模式实例
    private static volatile AppInfo_ instance = null;

    public static AppInfo_ getInstance() {
        synchronized (AppInfo_.class){
            //不能是单例的，因为可能会在不同的上下文中使用，所以每次都要实例化
            //if(instance == null){
                synchronized (AppInfo_.class){
                    instance = new AppInfo_();
                }
            //}
        }
        return instance;
    }

    private AppInfo_() {
            Properties properties = new Properties();
            try {
                properties.load(getClass().getClassLoader().getResourceAsStream("app_.properties"));
                if (!properties.isEmpty()) {
                    this.artifactId = properties.getProperty("app.artifactId");
                    this.groupId = properties.getProperty("app.groupId");
                    this.version = properties.getProperty("app.version");
                    this.description = properties.getProperty("app.description");

                    this.appAlias = properties.getProperty("appAlias");
                    this.baseUrl = properties.getProperty("baseUrl");
                    this.updUrl = properties.getProperty("updUrl");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }


}
