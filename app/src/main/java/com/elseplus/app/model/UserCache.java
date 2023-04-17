package com.elseplus.app.model;

import com.alibaba.fastjson2.JSONObject;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 * @program: javafx_desktop_apptemplate
 * @description:
 * @author: fangqing.fan#hotmail.com
 * @create: 2023/3/27 17:19
 **/

@Slf4j
@Data
public class UserCache {

    private String username;
    private String password;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    //@JsonIgnore
    private String fileName = "assets/user.json";
    private static volatile UserCache instance = null;

    public static UserCache getInstance() {
        synchronized (UserCache.class) {
            if (instance == null) {
                synchronized (UserCache.class) {
                    instance = new UserCache().init();
                }
            }
        }
        return instance;
    }

    private UserCache(){
        this.username="";
        this.password="";
    }

    public void upd() {
        try {
            String jsonStr = JSONObject.toJSONString(this);
            InputStream inputStream = new ByteArrayInputStream(jsonStr.getBytes());
            //解决文件路径问题，会自动创建在主jar包的同目录下
            File docxFile = new File(fileName);
            // 使用common-io的工具类即可转换
            FileUtils.copyToFile(inputStream, docxFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private UserCache init() {
        try {

            File jsonFile = new File(fileName);
            if (!jsonFile.exists()) {
                //解决文件路径问题，会自动创建在主jar包的同目录下
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

                if(inputStream == null){
                    //该配置文件尚未创建初始值
                    log.warn("{} can't found in resource,create a new default file",fileName);
                    String str = JSONObject.toJSONString(new UserCache());
                    log.warn("json:{}",str);
                    inputStream = new ByteArrayInputStream(str.getBytes());
                }

                // 使用common-io的工具类即可转换
                FileUtils.copyToFile(inputStream, jsonFile);
            }

            //读取文件
            FileInputStream fis = new FileInputStream(jsonFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                stringBuffer.append(line);
            }

            return JSONObject.parseObject(stringBuffer.toString(), UserCache.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
