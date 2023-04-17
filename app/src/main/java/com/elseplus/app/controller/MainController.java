package com.elseplus.app.controller;

import com.elseplus.app.model.UserCache;
import com.elseplus.app.network.HttpCallBack;
import com.elseplus.app.network.HttpReq;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @program: javafx_desktop_apptemplate
 * @description:
 * @author: fangqing.fan#hotmail.com
 * @create: 2023/3/24 12:28
 **/
public class MainController extends BaseController {

    @FXML
    Button btn;

    @FXML
    Button btn_alert;

    @FXML
    Button btn_alert_subview;
    @FXML
    Button btn_goto_subview;

    @FXML
    Button btn_open_subview;


    @FXML
    Button btn_http_req;
    @FXML
    TextArea ta_output;


    @FXML Button btn_file_chose;
    @FXML TextField tx_file_chose;
    @FXML Button btn_file_read;
    @FXML TextArea ta_file_read;


    @FXML Button btn_dir_chose;
    @FXML TextField tx_dir_chose;
    @FXML Button bth_file_write;
    @FXML TextArea ta_file_write;


    @FXML TextField tx_username;
    @FXML TextField tx_password;
    @FXML Button btn_upd_cache;
    @FXML Button btn_del_cache;
    @FXML Button btn_load_cache;

    @FXML Button btn_sys_notify;

    private Stage stage;

    public MainController(Stage stage){
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        log.debug("MainController initialize");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("onClickAction");
            }
        });

        btn_alert.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                alertMsg("测试弹出对话框");
            }
        });

        btn_goto_subview.addEventHandler(ActionEvent.ACTION,this);
        btn_alert_subview.addEventHandler(ActionEvent.ACTION,this);
        btn_open_subview.addEventHandler(ActionEvent.ACTION,this);
        btn_http_req.addEventHandler(ActionEvent.ACTION,this);


        btn_file_chose.addEventHandler(ActionEvent.ACTION,this);
        btn_file_read.addEventHandler(ActionEvent.ACTION,this);
        btn_dir_chose.addEventHandler(ActionEvent.ACTION,this);
        bth_file_write.addEventHandler(ActionEvent.ACTION,this);

        btn_upd_cache.addEventHandler(ActionEvent.ACTION,this);
        btn_del_cache.addEventHandler(ActionEvent.ACTION,this);
        btn_load_cache.addEventHandler(ActionEvent.ACTION,this);

        btn_sys_notify.addEventHandler(ActionEvent.ACTION,this);
    }


    public void displayTray() throws AWTException {
        //Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();

        //If the icon is a file
        //Image image = Toolkit.getDefaultToolkit().createImage("icons/icon.png");
        //Alternative (if the icon is on the classpath):
        Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/icons/icon.png"));

        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        //Let the system resize the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("System tray icon demo");
        tray.add(trayIcon);

        trayIcon.displayMessage("Hello, World", "notification demo", TrayIcon.MessageType.INFO);

        //tray.remove(trayIcon);
    }

    @Override
    public void handle(ActionEvent actionEvent) {

        log.debug(actionEvent.toString());

        //系統通知
        if(actionEvent.getSource().equals(btn_sys_notify)){

            try {
                displayTray();
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }

        }

        if(actionEvent.getSource().equals(btn_open_subview)){
            log.debug("on action: btn_open_subview");

            Stage stage = new Stage();

            try {
                Parent parent = getParent("/fxml/sub.fxml",new SubController(stage));
                stage.setTitle("open subview");
                stage.setScene(new Scene(parent));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        if(actionEvent.getSource().equals(btn_goto_subview)){
            log.debug("on action: btn_goto_subview");

            //这里是取到Stage的具体代码,通过某个控件获取到
            this.stage.close();

            Stage stage = new Stage();

            try {
                Parent parent = getParent("/fxml/sub.fxml",new SubController(stage));
                stage.setTitle("goto subview");
                stage.setScene(new Scene(parent));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        if(actionEvent.getSource().equals(btn_alert_subview)){
            log.debug("on action: btn_alert_subview");

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            //stage.setResizable(false);
            //stage.initStyle(StageStyle.UNDECORATED);//不可拖动

            try {
                Parent parent = getParent("/fxml/sub.fxml",new SubController(stage));
                stage.setTitle("alert subview");
                stage.setScene(new Scene(parent));
                stage.showAndWait();
                stage.setAlwaysOnTop(true);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        //
        if(actionEvent.getSource().equals(btn_http_req)) {
            log.debug("on action: btn_http_req");

            String url = "https://devsdkapi.fanfq.com/sdk/api/req/analysis";
            HttpReq.getInstance().get(url, new HttpCallBack() {
                @Override
                public void success(Object t) {
                    log.debug(""+t);

                    String str = ta_output.getText() + "\n";
                    ta_output.setText(str + t.toString());
                }

                @Override
                public void fail(String fail) {
                    log.debug(fail);

                    ta_output.setText(fail);
                }
            });

            log.debug("zzz");
        }


        //选择文件
        if(actionEvent.getSource().equals(btn_file_chose)) {
            log.debug("on action: btn_file_chose");

            String filepath = chooseFile();
            tx_file_chose.setText(filepath);
        }


        //文件导入
        if(actionEvent.getSource().equals(btn_file_read)) {
            log.debug("on action: btn_file_read");

            String filepath = tx_file_chose.getText();

            //读取导入文件
            File importFile = new File(filepath);

            try{
                StringBuffer stringBuffer = new StringBuffer();

                FileInputStream fileInputStream = new FileInputStream(importFile);
                //按行读取
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line).append("\n");
                }
                bufferedReader.close();
                inputStreamReader.close();
                fileInputStream.close();


                Platform.runLater(()->{
                    //更新 UI 数据的代码
                    ta_file_read.setText(stringBuffer.toString());
                });

            }catch (IOException e){
                e.printStackTrace();
            }
        }

        //选择文件夹
        if(actionEvent.getSource().equals(btn_dir_chose)) {
            log.debug("on action: btn_dir_chose");

            String filepath = chooseDirectory();
            tx_dir_chose.setText(filepath);
        }

        //文件导出
        if(actionEvent.getSource().equals(bth_file_write)) {
            log.debug("on action: bth_file_write");


            String content = ta_file_write.getText();

            String filepath = tx_dir_chose.getText() + File.separator+"output"+ File.separator;
            String fileName = filepath+"123.txt";

            try{
                File dir = new File(filepath);
                if(!dir.exists()){
                    dir.mkdirs();
                }

                InputStream inputStream = new ByteArrayInputStream(content.getBytes());

                FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                byte[] bytes =  new byte[1024];
                int index;
                while ((index = inputStream.read(bytes)) != -1) {
                    fileOutputStream.write(bytes,0,index);// 写入数据
                    fileOutputStream.flush();
                }
                inputStream.close();
                fileOutputStream.close();

                log.debug("file write finished:{}",fileName);

            }catch (IOException e){
                e.printStackTrace();
            }
        }


        //跟新缓存
        if(actionEvent.getSource().equals(btn_upd_cache)) {
            log.debug("on action: btn_upd_cache");

            UserCache userCache = UserCache.getInstance();
            userCache.setUsername(tx_username.getText());
            userCache.setPassword(tx_password.getText());
            userCache.upd();
        }

        //清除缓存
        if(actionEvent.getSource().equals(btn_del_cache)) {
            log.debug("on action: btn_del_cache");
            tx_username.setText("");
            tx_password.setText("");
        }

        //加载缓存
        if(actionEvent.getSource().equals(btn_load_cache)) {
            log.debug("on action: btn_load_cache");

            UserCache userCache = UserCache.getInstance();
            tx_username.setText(userCache.getUsername());
            tx_password.setText(userCache.getPassword());

        }
    }
}
