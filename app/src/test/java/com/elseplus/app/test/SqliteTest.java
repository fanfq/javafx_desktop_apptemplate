package com.elseplus.app.test;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SqliteTest {

    private static final String fileName = "assets/sqlite.db";

    public static void main(String[] args) throws Exception {

        File dbFile = new File(fileName);
        if (!dbFile.exists()) {
            //解决文件路径问题，会自动创建在主jar包的同目录下
            InputStream inputStream = SqliteTest.class.getClassLoader().getResourceAsStream(fileName);

            // 使用common-io的工具类即可转换
            FileUtils.copyToFile(inputStream, dbFile);
        }

        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:"+fileName);
        Statement stmt = conn.createStatement();

        stmt.executeUpdate("DROP TABLE IF EXISTS person");
        stmt.executeUpdate("CREATE TABLE person(id INTEGER, name STRING)");
        stmt.executeUpdate("INSERT INTO person VALUES(1, 'john')");
        stmt.executeUpdate("INSERT INTO person VALUES(2, 'david')");
        stmt.executeUpdate("INSERT INTO person VALUES(3, 'henry')");
        ResultSet rs = stmt.executeQuery("SELECT * FROM person");
        while (rs.next()) {
            System.out.println("id=>" + rs.getInt("id") + ", name=>" + rs.getString("name"));
        }
        stmt.close();
        conn.close();
    }

}
