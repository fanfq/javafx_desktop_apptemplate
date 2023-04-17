package com.elseplus.app.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @program: javafx_desktop_apptemplate
 * @description:
 * @author: fangqing.fan#hotmail.com
 * @create: 2023/3/24 15:34
 **/
public class SubController extends BaseController {


    private Stage stage;
    @FXML
    Button btn;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.debug("SubController initialize");

        btn.addEventHandler(ActionEvent.ACTION,this);
    }

    /**
     * 构造方法
     * @param stage
     */
    SubController(Stage stage){

        this.stage = stage;
        log.debug("new SubController(Stage stage)");
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                log.debug("windows close event");
            }
        });
    }



    @Override
    public void handle(ActionEvent actionEvent) {
        log.debug(actionEvent.toString());

        if(actionEvent.getSource().equals(btn)){
            log.debug("on action: btn --> close");

            //Stage stage = (Stage) btn.getScene().getWindow();
            stage.close();
        }
    }
}
