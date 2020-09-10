package com.seczone.sca.idea.plugin.util;

import com.intellij.openapi.ui.Messages;
import com.seczone.sca.idea.plugin.model.JarInfo;

import java.io.FileReader;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @description: jdbs工具类
 * @author: hew
 * @create: 2020-09-09 16:06
 **/
public class JDBCUtils {

    private static String username;
    private static String password;
    private static String jdbcUrl;
    private  static String driverClassName;

    static {
        try {
            // 读取数据库配置文件
            Properties properties = new Properties();
            properties.load(JDBCUtils.class.getClassLoader().getResourceAsStream("config/jdbc.properties"));

            username = properties.getProperty("username");
            password = properties.getProperty("password");
            jdbcUrl = properties.getProperty("jdbcUrl");
            driverClassName = properties.getProperty("driverClassName");
            // 注册驱动
            Class.forName(driverClassName);
        } catch (Exception e) {
            e.printStackTrace();
            Messages.showErrorDialog("读取数据库配置文件错误："+e.getMessage(),"error");
        }
    }
    /**
     * 获取连接
     * @return 连接对象
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
        return conn;
    }

    /**
     * 释放资源
     * @param rs
     * @param st
     * @param conn
     */
    public static void close(ResultSet rs, Statement st, Connection conn){
        if (rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(st != null){
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 查询数据
    public static List<JarInfo> findAll(String sql) throws Exception{
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        List<JarInfo> list = new ArrayList<>();
        try {
            conn = JDBCUtils.getConnection();
            st = conn.createStatement();
            //执行sql
            rs = st.executeQuery(sql);
            JarInfo bean = null;
            while (rs.next()){
                String groupName = rs.getString("group_name");
                String artifactName = rs.getString("artifact_name");
                String version = rs.getString("version");
                String grade = rs.getString("grade");
                bean = new JarInfo(groupName,artifactName,version,grade);
                list.add(bean);
            }
        }finally {
            JDBCUtils.close(rs,st,conn);
        }
        return list;
    }
}
