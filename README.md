#javafx_desktop_apptemplate

jdk11 + javafx + update4j + javapackage + LogBack + lombok

安装[inno setup6](https://jrsoftware.org/isdl.php), 并加入到path中，用于制作windows安装程序(最好下载最新版本)
为了“iscc”这个命令行能够执行

安装[WiX Toolset](https://github.com/wixtoolset/wix3/releases/tag/wix3112rtm), 并将bin目录加入到path中，用于创建Windows安装包。
为了“candle”和"light"两个命令行能够执行

macos操作系统安装包需要安装[xcode]。

关于[JavaPackager](https://github.com/fvarrui/JavaPackager) 更多配置可参考其github

引入[update4j](https://github.com/update4j/update4j) 用于支持工具更新


## 使用前准备

使用nginx映射一个静态文件访问路径，或者其他方式，只要可以提供文件下载的服务就行，用于自动更新时下载对应配置和jar使用

找到项目根目录下的 `pom.xml`,修改相关参数

以更新服务器的URL是`http://192.168.5.3:8086`为例，只需要配置 `baseUrl`，确保最终 `updUrl` 能正常访问即可。

如果该更新服务器有多个产品，则通过 `appAlias` 区分， `appName` 为启动器的名称。

```xml
<properties>
    ...

    <!-- 打包目录 默认无需修改-->
    <root>${user.dir}/app-update</root>
    <target>${root}/target</target>
    <config.location>${target}/config</config.location>

    <appAlias>app</appAlias><!--app打包名称-->
    <appName>app-bootstrap</appName> <!--启动器名称 app-bootstrap.exe 不能有中文-->
    <baseUrl>http://192.168.5.3:8086</baseUrl> <!--更新服务器url-->
    <updUrl>${baseUrl}/${appAlias}/config.xml</updUrl><!--更新服务器资源url-->

</properties>
```

找到bootstrap项目中的JavaFxDelegate类，修改configUrl为：xxx/app/config.xml

找到create-config项目中的CreateConfig类，修改BASE_URL为：xxx

使用idea或者其他工具运行maven命令进行编译打包

```
mvn package
```

* 更新资源包

项目的根目录下生成app-update文件夹，将内部的app文件夹(`app-update/target/config/app` 或者 `app-update/app.zip`),

`/app-update/app.zip` 上传到服务器，也就是每次版本升级需要加载的内容

打开浏览器访问: xxx/app/config.xml, 可以正常访问即可

* 各个平台的交付物,只需交付一次，后续的版本升级会自动完成

找到`app-update/bootstrap`文件夹，可以发现`app-bootstrap_1.0.0.exe`、`bootstrap_1.0.0.msi` 打开即可安装，或者直接解压 `app-bootstrap-1.0.0-windows.zip` 运行免安装版

需要注意的是需要在不同的操作系统下进行打对应平台的包，支持 `windows`，`macos`，`linux`

# 加解密

为了防止工具被任意分发，可以使用证书加解密功能

* 使用jdk提供的keytool创建keystore文件

```
keytool -genkeypair -alias fanfq.github.io -keyalg RSA -keystore RSA_PKCS12.keystore -storetype pkcs12  -validity 1000


输入密钥库口令:  12345678
再次输入新口令:  12345678
您的名字与姓氏是什么?
  [Unknown]:  fanfq.github.io
您的组织单位名称是什么?
  [Unknown]:  fanfq.github.io
您的组织名称是什么?
  [Unknown]:  fanfq.github.io
您所在的城市或区域名称是什么?
  [Unknown]:  fanfq.github.io
您所在的省/市/自治区名称是什么?
  [Unknown]:  fanfq.github.io
该单位的双字母国家/地区代码是什么?
  [Unknown]:  fanfq.github.io
CN=fanfq.github.io, OU=fanfq.github.io, O=fanfq.github.io, L=fanfq.github.io, ST=fanfq.github.io, C=fanfq.github.io是否正确?
  [否]:  y

```

按照提示输入密码，demo中使用的是12345678，根据自己需求修改即可(注意文件keystore根据自己实际环境修改)

* create-config中加入加密功能

```
 KeyStore ks = KeyStore.getInstance("pkcs12");
try (FileInputStream in = new FileInputStream("RSA_PKCS12.keystore")) {
    ks.load(in, "12345678".toCharArray());

    PrivateKey key = (PrivateKey) ks.getKey("fanfq.github.io", "12345678".toCharArray());
    System.out.println("key: " + key);

    Configuration config = Configuration.builder()
            .baseUri(BASE_URL + "/app")
            .basePath("${user.dir}/app")
            .files(refs)
            .property("maven.central", MAVEN_BASE)
            .signer(key)
            .build();
    
    //签名相关
    //https://github.com/update4j/update4j/issues/5
    try (Writer out = Files.newBufferedWriter(Paths.get(dir + "/config.xml"))) {
        config.write(out);
    }
}
```

* bootstrap加入解密功能

JavaFxDelegate类中:

```
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
```

pom.xml中加入将.keystore加入到工具中:

```xml
     <additionalResource>../*.keystore</additionalResource>
```
# 注意

项目升级到java17后, 想要直接运行javafx打开弹框得话, 需要在idea得启动配置里添加vm参数

```
--add-exports=javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED
--add-exports=javafx.base/com.sun.javafx.binding=ALL-UNNAMED
--add-exports=javafx.base/com.sun.javafx.event=ALL-UNNAMED
--add-exports=javafx.graphics/com.sun.javafx.stage=ALL-UNNAMED
--add-exports=javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED
--add-exports=javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED
--add-exports=javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED
--add-opens=java.base/java.lang.reflect=ALL-UNNAMED
--add-opens=java.base/java.util=ALL-UNNAMED
```


# FAQ
/Library/Java/JavaVirtualMachines/jdk-11.0.15.jdk/Contents/Home/bin/java -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=54925:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8 -classpath /Users/fred/workspace/mycode_2023/idea_projs/javafx_desktop_apptemplate/app/target/classes:/Users/fred/workspace/mycode_2023/idea_projs/javafx_desktop_apptemplate/bootstrap/target/classes:/Users/fred/.m2/repository/org/openjfx/javafx-controls/17.0.6/javafx-controls-17.0.6.jar:/Users/fred/.m2/repository/org/openjfx/javafx-controls/17.0.6/javafx-controls-17.0.6-mac.jar:/Users/fred/.m2/repository/org/openjfx/javafx-graphics/17.0.6/javafx-graphics-17.0.6.jar:/Users/fred/.m2/repository/org/openjfx/javafx-graphics/17.0.6/javafx-graphics-17.0.6-mac.jar:/Users/fred/.m2/repository/org/openjfx/javafx-base/17.0.6/javafx-base-17.0.6.jar:/Users/fred/.m2/repository/org/openjfx/javafx-base/17.0.6/javafx-base-17.0.6-mac.jar:/Users/fred/.m2/repository/org/openjfx/javafx-fxml/17.0.6/javafx-fxml-17.0.6.jar:/Users/fred/.m2/repository/org/openjfx/javafx-fxml/17.0.6/javafx-fxml-17.0.6-mac.jar:/Users/fred/.m2/repository/org/slf4j/slf4j-api/2.0.5/slf4j-api-2.0.5.jar:/Users/fred/.m2/repository/ch/qos/logback/logback-core/1.4.6/logback-core-1.4.6.jar:/Users/fred/.m2/repository/ch/qos/logback/logback-access/1.4.6/logback-access-1.4.6.jar:/Users/fred/.m2/repository/ch/qos/logback/logback-classic/1.4.6/logback-classic-1.4.6.jar:/Users/fred/.m2/repository/org/update4j/update4j/1.5.9/update4j-1.5.9.jar com.elseplus.app.JavaFxLauncher
错误: 缺少 JavaFX 运行时组件, 需要使用该组件来运行此应用程序

--module-path $PATH_TO_FX$ --add-modules javafx.controls,javafx.fxml



https://cloud.tencent.com/developer/ask/sof/167897

//FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
直接使用默认的 fxmlLoader 通过update4j 启动时会报错: .fxml 中找不到指定的 fx:controller 类，

此时只能通过代码的方式绑定
FXMLLoader fxmlLoader = new FXMLLoader();
fxmlLoader.setLocation(getClass().getResource("/fxml/main.fxml"));
fxmlLoader.setController(new MainController());



# todo 针对指定工程指定目录进行混淆，需要完善
```xml
            <!-- 代码混淆 -->
            <plugin>
                <groupId>com.github.wvengen</groupId>
                <artifactId>proguard-maven-plugin</artifactId>
                <version>2.0.11</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>proguard</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <proguardVersion>6.2.2</proguardVersion>
                    <!-- 对本工程下的classes目录进行加载 -->
<!--                    <injar>classes</injar>-->
<!--                    <outjar>${project.build.finalName}_pg.jar</outjar>-->
<!--                    <outputDirectory>${project.build.directory}</outputDirectory>-->


                    <!-- 指定jar包进行加载 -->
                    <injar>${project.build.finalName}.jar</injar>
                    <outjar>${project.build.finalName}.jar</outjar>
                    <outputDirectory>${config.location}/app</outputDirectory>

                    <!--混淆-->
                    <obfuscate>true</obfuscate>
                    <!--是否将生成的PG文件安装部署-->
                    <!--<attach>true</attach>-->
                    <!--指定生成文件分类-->
                    <!--<attachArtifactClassifier>pg</attachArtifactClassifier>-->
                    <!--保留pom文件等-->
                    <addMavenDescriptor>false</addMavenDescriptor>
                    <!--外部配置文件-->
                    <proguardInclude>proguard.cfg</proguardInclude>
                </configuration>
                <dependencies>
                    <dependency>
                        <groupId>net.sf.proguard</groupId>
                        <artifactId>proguard-base</artifactId>
                        <version>6.2.2</version>
                    </dependency>
                </dependencies>
            </plugin>
```