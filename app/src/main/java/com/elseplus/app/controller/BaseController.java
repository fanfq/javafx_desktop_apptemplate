package com.elseplus.app.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @program: javafx_desktop_apptemplate
 * @description:
 * @author: fangqing.fan#hotmail.com
 * @create: 2023/3/24 15:34
 **/
public abstract class BaseController implements Initializable, EventHandler<ActionEvent> {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     *
     * @param fxml
     * @param controller
     * @return
     * @throws IOException
     */
    public Parent getParent(String fxml,BaseController controller) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(fxml));
        if(controller !=null){
            fxmlLoader.setController(controller); //使用此代码，必移除fx:controller)属性\
        }

        return fxmlLoader.load();
    }

    /**
     * 弹出对话框
     * @param content
     */
    public void alertMsg(String content){
        //弹出对话框
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * 文件选择器
     * @return
     */
    public String chooseFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File");
//                fileChooser.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));
//                fileChooser.getExtensionFilters().add(new ExtensionFilter("All Files", "*.*"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"), new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            //log.debug(file.getAbsolutePath());
            return file.getAbsolutePath();
        }
        return null;
    }

    /**
     * 文件夹选择器
     * @return
     */
    public String chooseDirectory(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Folder");
        File directory = directoryChooser.showDialog(new Stage());
        if (directory != null) {
            //log.debug(directory.getAbsolutePath());
            return directory.getAbsolutePath();
        }
        return null;
    }


}
