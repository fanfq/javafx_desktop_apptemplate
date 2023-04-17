package com.elseplus.createconfig;

import com.elseplus.common.util.AppInfo;
import com.elseplus.common.util.ZipUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.update4j.Configuration;
import org.update4j.FileMetadata;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

public class CreateConfig {

    //https://repo1.maven.org/maven2
    private static final String MAVEN_BASE = "http://maven.aliyun.com/nexus/content/groups/public";

    ///private static final String BASE_URL = "http://192.168.5.3:8086/app-update/target/config";
    //private static final String BASE_URL = "http://192.168.5.3:8086";
    public static void main(String[] args)  {

        AppInfo appInfo = AppInfo.getInstance();//new AppInfo("config.properties");

        String rootPath = System.getProperty("maven.dir");
        String configLoc = System.getProperty("config.location");

        String dir = configLoc + "/"+appInfo.getAppAlias();

        File[] jfiles = new File(dir).listFiles();
        List<FileMetadata.Reference> refs = new ArrayList<>();
        for (File file: jfiles) {
            if (!file.getName().endsWith("jar")) {
                // do nth
                // contains("config.xml")
                continue;
            } else if (file.getName().endsWith("proguard_base.jar")) {
                //跳过混淆前的包
                continue;
            } else {
                refs.add(FileMetadata.readFrom(file.getPath()).path(file.getName()).classpath());
            }
        }

        try {
            KeyStore ks = KeyStore.getInstance("pkcs12");
            try (FileInputStream in = new FileInputStream("RSA_PKCS12.keystore")) {
                ks.load(in, "12345678".toCharArray());

                PrivateKey key = (PrivateKey) ks.getKey("fanfq.github.io", "12345678".toCharArray());
                System.out.println("key: " + key);

                Configuration config = Configuration.builder()
                        .baseUri(appInfo.getBaseUrl() + "/"+appInfo.getAppAlias())
                        .basePath("${user.dir}/"+appInfo.getAppAlias())
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

        }catch (Exception e) {
            e.printStackTrace();
        }


        ///String dir = "D:\\workspace\\mycode_2023\\idea_projs\\javafx_desktop_apptemplate\\app-update\\target\\config\\app";


        //打压缩包 app-update/app.zip
        try{
            String zipPath = rootPath+"/"+appInfo.getAppAlias()+".zip";

            FileOutputStream fos1 = new FileOutputStream(new File(zipPath));
            ZipUtil.toZip(dir, fos1, true);
        }catch (Exception e){
            e.printStackTrace();
        }


        //ini setup
        //exe installer bootstrap/target/*-windows.zip
        try{
            bootstrapArchive();
        }catch (IOException e){
            e.printStackTrace();
        }



    }


    /**
     * 根据编译机的系统环境copy交付物到指定文件夹
     * @throws IOException
     */
    public static void bootstrapArchive() throws IOException {
        String userDir = System.getProperty("user.dir");

        String destDir = userDir + "/app-update/bootstrap/";

        String srcDir = userDir + "/bootstrap/target/";

        AppInfo appInfo = AppInfo.getInstance();

        if(SystemUtils.IS_OS_WINDOWS){

            String fileName = appInfo.getAppName()+"-"+appInfo.getVersion()+"-windows.zip";
            File srcFile = new File(srcDir+fileName);
            File destFile = new File(destDir+fileName);

            FileUtils.copyFile(srcFile,destFile);

            fileName = appInfo.getAppName()+"_"+appInfo.getVersion()+".exe";
            srcFile = new File(srcDir+fileName);
            destFile = new File(destDir+fileName);

            FileUtils.copyFile(srcFile,destFile);

            fileName = appInfo.getAppName()+"_"+appInfo.getVersion()+".msi";
            srcFile = new File(srcDir+fileName);
            destFile = new File(destDir+fileName);

            FileUtils.copyFile(srcFile,destFile);

        }else if(SystemUtils.IS_OS_MAC){

            String fileName = appInfo.getAppName()+"-"+appInfo.getVersion()+"-mac.tar.gz";
            File srcFile = new File(srcDir+fileName);
            File destFile = new File(destDir+fileName);

            FileUtils.copyFile(srcFile,destFile);

            fileName = appInfo.getAppName()+"_"+appInfo.getVersion()+".dmg";
            srcFile = new File(srcDir+fileName);
            destFile = new File(destDir+fileName);

            FileUtils.copyFile(srcFile,destFile);

        }else if(SystemUtils.IS_OS_LINUX){

            String fileName = appInfo.getAppName()+"-"+appInfo.getVersion()+"-linux.tar.gz";
            File srcFile = new File(srcDir+fileName);
            File destFile = new File(destDir+fileName);

            FileUtils.copyFile(srcFile,destFile);

            fileName = appInfo.getAppName()+"_"+appInfo.getVersion()+".deb";
            srcFile = new File(srcDir+fileName);
            destFile = new File(destDir+fileName);

            FileUtils.copyFile(srcFile,destFile);

            fileName = appInfo.getAppName()+"_"+appInfo.getVersion()+".rpm";
            srcFile = new File(srcDir+fileName);
            destFile = new File(destDir+fileName);

            FileUtils.copyFile(srcFile,destFile);
        }


    }
}
