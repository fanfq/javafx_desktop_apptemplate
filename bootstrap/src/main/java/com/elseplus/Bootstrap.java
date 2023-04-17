package com.elseplus;

import com.elseplus.bootstrap.JavaFxDelegate;
import javafx.application.Application;

public class Bootstrap {

    /**
     * 解决
     * 错误: 缺少 JavaFX 运行时组件, 需要使用该组件来运行此应用程序
     * @param args
     */
    public static void main(String[] args) {
        Application.launch(JavaFxDelegate.class,args);
    }
}