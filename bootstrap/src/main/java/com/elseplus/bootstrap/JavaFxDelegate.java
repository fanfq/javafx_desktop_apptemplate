package com.elseplus.bootstrap;

import com.alibaba.fastjson2.JSONObject;
import com.elseplus.common.util.AppInfo;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;
import org.update4j.Configuration;
import org.update4j.service.Delegate;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
public class JavaFxDelegate extends Application implements Delegate {

    public static List<Image> images;
    public static Image inverted;

    @Override
    public void init() throws Exception {
//		super.init();
        System.setProperty("update4j.suppress.warning", "true");

        List<String> sizes = List.of("tiny", "small", "medium", "large", "xlarge");
        images = sizes.stream()
                .map(s -> ("/icons/update4j-icon-" + s + ".png"))
                .map(s -> getClass().getResource(s).toExternalForm())
                .map(Image::new)
                .collect(Collectors.toList());
        inverted = new Image("/icons/update4j-icon-invert.png");
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
//        primaryStage.setTitle("Hello World");
//        primaryStage.setScene(new Scene(rootNode, 300, 275));
//        primaryStage.show();

        //"http://192.168.5.3:8086/app-update/target/config/app/config.xml"

        //log.debug("###url:{}","http://192.168.3.115:8086/app/config.xml");
//        AppInfo appInfo = new AppInfo("app.properties");
//        log.debug(JSONObject.toJSONString(appInfo));
        AppInfo appInfo = AppInfo.getInstance(); //new AppInfo("bootstrap.properties");
        log.info("[bootstrap] ### appInfo : {}",JSONObject.toJSONString(appInfo));


        //URL configUrl = new URL("http://192.168.3.115:8086/app/config.xml");
        URL configUrl = new URL(appInfo.getUpdUrl());
        try (Reader in = new InputStreamReader(configUrl.openStream(), StandardCharsets.UTF_8);
             InputStream certIn = Files.newInputStream(Paths.get("RSA_PKCS12.keystore"))) {
            KeyStore ks = KeyStore.getInstance("pkcs12");
            ks.load(certIn, "12345678".toCharArray());
            Certificate certificate = ks.getCertificate("fanfq.github.io");
            PublicKey publicKey = certificate.getPublicKey();
            //启动模式设置为dev 可选test/dev/prod
            //Configuration config = Configuration.read(in, publicKey, Map.of("profiles.active", "prod"));

            Configuration config = Configuration.read(in, publicKey);

            //StartupView startup = new StartupView(config, primaryStage);
            SplashView startup = new SplashView(config,primaryStage);

            Scene scene = new Scene(startup);
            scene.getStylesheets().add(getClass().getResource("root.css").toExternalForm());

            //primaryStage.getIcons().addAll(images);
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image("icons/icon.png"));
            primaryStage.setResizable(false);
            primaryStage.initStyle(StageStyle.UNDECORATED);//设定窗口无边框


            //primaryStage.setTitle("Update4j Demo Launcher");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Optional<ButtonType>  buttonType = new Alert(Alert.AlertType.ERROR, "发生错误，请检查配置或者网络", new ButtonType[]{ButtonType.CLOSE}).showAndWait();
            buttonType.ifPresent(buttonType1 -> {
                Platform.exit();
                System.exit(0);
            });
        }

    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public long version() {
        return 0;
    }

    @Override
    public void main(List<String> list) throws Throwable {
        launch();
    }
}
