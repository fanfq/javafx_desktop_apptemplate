package com.elseplus.bootstrap;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.update4j.Archive;
import org.update4j.Configuration;
import org.update4j.FileMetadata;
import org.update4j.UpdateOptions;
import org.update4j.inject.InjectSource;
import org.update4j.inject.Injectable;
import org.update4j.service.UpdateHandler;

import java.io.File;
import java.nio.file.Path;

/**
 * @program: javafx_desktop_apptemplate
 * @description:
 * @author: fangqing.fan#hotmail.com
 * @create: 2023/3/23 11:14
 **/
public class SplashView extends FXMLView implements UpdateHandler, Injectable {

    private Configuration config;


    @FXML
    private StackPane progressContainer;
    @FXML
    private Pane primary;


    private DoubleProperty primaryPercent;
    private DoubleProperty secondaryPercent;
    private BooleanProperty running;
    private volatile boolean abort;

    @InjectSource
    private Stage primaryStage;


    @InjectSource
    private Image inverted = JavaFxDelegate.inverted;


    /**
     * 如果有跟新则自动跟新，跟新完成后launch目标页面，反之则直接launch目标页面
     *
     * @param config
     * @param primaryStage
     */
    public SplashView(Configuration config, Stage primaryStage) {

        this.config = config;
        this.primaryStage = primaryStage;

        primaryPercent = new SimpleDoubleProperty(this, "primaryPercent");
        secondaryPercent = new SimpleDoubleProperty(this, "secondaryPercent");

        primary.maxWidthProperty().bind(progressContainer.widthProperty().multiply(primaryPercent));

        running = new SimpleBooleanProperty(this, "running");

        //FadeTransition fade = new FadeTransition(Duration.seconds(1.5), status);
        //fade.setToValue(0);

        running.addListener((obs, ov, nv) -> {
            if (nv) {
                //fade.stop();
                //status.setOpacity(1);
            } else {
                //fade.playFromStart();
                primaryPercent.set(0);
                secondaryPercent.set(0);
            }
        });

        primary.visibleProperty().bind(running);

        //开始
        launchPressed();
    }

    /**
     * 初始化
     */
    void launchPressed(){
        Task<Boolean> checkUpdates = checkUpdates();

        checkUpdates.setOnSucceeded(evt -> {
            Thread run = new Thread(() -> {
                config.launch(this);
            });

            //FIXME: add opt-out checkbox
            if (checkUpdates.getValue()) {
                //有版本跟新
                updatePressed();
                System.out.println("有版本跟新");

//                ButtonType launch = new ButtonType("更新", ButtonBar.ButtonData.OK_DONE);
//                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//                alert.setHeaderText("新版本提示");
//                alert.setContentText("存在新版本，需要更新");
//                alert.getButtonTypes().setAll(launch);
//
//                alert.showAndWait().filter(bt -> bt == launch).ifPresent(buttonType -> updatePressed());
            } else {
                //没有版本跟新
                System.out.println("没有版本跟新");
                run.start();
            }
        });

        run(checkUpdates);
    }

    /**
     * 开始跟新
     */
    void updatePressed(){
        if (running.get()) {
            abort = true;
            return;
        }

        running.set(true);

        //status.setText("Checking for updates...");

        Task<Boolean> checkUpdates = checkUpdates();
        checkUpdates.setOnSucceeded(evt -> {
            if (!checkUpdates.getValue()) {
                //status.setText("No updates found");
                running.set(false);
            } else {
                Task<Void> doUpdate = new Task<>() {

                    @Override
                    protected Void call() throws Exception {
//					    Path zip = Paths.get("app-update.zip");
                        Path zip = new File(System.getProperty("user.dir"), "app-update.zip").toPath();
                        if(config.update(UpdateOptions.archive(zip).updateHandler(SplashView.this)).getException() == null) {
                            Archive.read(zip).install();
                        }

                        return null;
                    }

                };
                doUpdate.setOnSucceeded(stateEvent -> {
                    launchPressed();
                });
                run(doUpdate);
            }
        });

        run(checkUpdates);
    }








    /**
     * 检查是否需要跟新
     * @return
     */
    private Task<Boolean> checkUpdates() {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return config.requiresUpdate();
            }

        };
    }

    private void run(Runnable runnable) {
        Thread runner = new Thread(runnable);
        runner.setDaemon(true);
        runner.start();
    }



    @Override
    public void updateDownloadFileProgress(FileMetadata file, float frac) throws AbortException {
        Platform.runLater(() -> {
            //status.setText("Downloading " + file.getPath().getFileName() + " (" + ((int) (100 * frac)) + "%)");
            //System.out.println("Downloading " + file.getPath().getFileName() + " (" + ((int) (100 * frac)) + "%)");
            secondaryPercent.set(frac);
        });

        if (abort) {
            throw new AbortException();
        }
    }

    @Override
    public void updateDownloadProgress(float frac) throws InterruptedException {
        Platform.runLater(() -> primaryPercent.set(frac));
        System.out.println("updateDownloadProgress "  + " (" + ((int) (100 * frac)) + "%)");
        //if(slow.isSelected())
            Thread.sleep(1);
    }

    @Override
    public void failed(Throwable t) {
        Platform.runLater(() -> {
            if (t instanceof AbortException) {
                //status.setText("Update aborted");

                System.out.println("  -->  failed 1 -->  Update aborted");

            }else {

                System.out.println("  -->  failed 2 -->  Update aborted");
//                status.setText("Failed: " + t.getClass().getSimpleName() + ": " + t.getMessage());
//                Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                alert.setTitle("Failed");
//                alert.setHeaderText("发生异常");
//                alert.setContentText("Failed: " + t.getClass().getSimpleName() + ": " + t.getMessage());
//                alert.showAndWait();
            }
        });
    }

    @Override
    public void succeeded() {
        //Platform.runLater(() -> status.setText("Download complete"));
        System.out.println("  -->  succeeded");
    }

    @Override
    public void stop() {
        Platform.runLater(() -> running.set(false));
        abort = false;

        System.out.println("  -->  stop");
    }
}
