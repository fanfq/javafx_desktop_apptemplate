package com.elseplus;

import com.elseplus.app.JavaFxLauncher;
import javafx.application.Application;

/**
 * @program: javafx_desktop_apptemplate
 * @description:
 * @author: fangqing.fan#hotmail.com
 * @create: 2023/3/23 10:02
 **/
public class App {

    /**
     * 解决
     * 错误: 缺少 JavaFX 运行时组件, 需要使用该组件来运行此应用程序
     * @param args
     */
    public static void main(String[] args) {
        Application.launch(JavaFxLauncher.class,args);
    }
}
