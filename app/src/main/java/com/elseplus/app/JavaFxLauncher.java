package com.elseplus.app;

import com.alibaba.fastjson2.JSONObject;
import com.elseplus.app.controller.MainController;
import com.elseplus.app.util.AppInfo_;
import com.elseplus.common.util.AppInfo;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.update4j.LaunchContext;
import org.update4j.inject.InjectTarget;
import org.update4j.service.Launcher;


@Slf4j
public class JavaFxLauncher extends Application implements Launcher {

    @InjectTarget
    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        show(stage);
        log.debug("app start");
    }

    @Override
    public void init() throws Exception {
        super.init();
        log.debug("app init");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        log.debug("app stop");
    }

    private void show(Stage stage) throws Exception{

        AppInfo_ appInfo = AppInfo_.getInstance();//("app_.properties");
        log.info("[app] ### appInfo_ : {}",JSONObject.toJSONString(appInfo));

        AppInfo appInfo_ = AppInfo.getInstance();
        log.info("[app] ### appInfo : {}",JSONObject.toJSONString(appInfo_));

        stage.setMinHeight(480);
        stage.setMinWidth(800);
        stage.setResizable(false);

        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/main.fxml"));
        fxmlLoader.setController(new MainController(stage)); //使用此代码，必移除fx:controller)属性

        Scene scene = new Scene(fxmlLoader.load(), 800, 480);

        stage.setTitle(appInfo.getAppAlias()+" - "+ appInfo.getDescription() +"  v."+ appInfo.getVersion() );
        stage.setScene(scene);
        stage.getIcons().add(new Image("icons/icon.png"));
        stage.show();
    }

    @Override
    public void run(LaunchContext launchContext) {

        Platform.runLater(() -> {
            try {
                show(new Stage());
                primaryStage.hide();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

    @Override
    public long version() {
        return Launcher.super.version();
    }
}
