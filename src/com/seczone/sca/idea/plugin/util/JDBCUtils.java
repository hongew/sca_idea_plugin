package com.seczone.sca.idea.plugin.util;

import com.intellij.openapi.ui.Messages;
import com.seczone.sca.idea.plugin.model.CveInfo;
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
    private static String jdbcUrl1;
    private static String jdbcUrl2;
    private  static String driverClassName;

    static {
        try {
            // 读取数据库配置文件
            Properties properties = new Properties();
            properties.load(JDBCUtils.class.getClassLoader().getResourceAsStream("config/jdbc.properties"));

            username = properties.getProperty("username");
            password = properties.getProperty("password");
            jdbcUrl1 = properties.getProperty("jdbcUrl1");
            jdbcUrl2 = properties.getProperty("jdbcUrl2");
            driverClassName = properties.getProperty("driverClassName");
            // 注册驱动
            Class.forName(driverClassName);
        } catch (Exception e) {
            e.printStackTrace();
            Messages.showErrorDialog("读取数据库配置文件错误："+e.getMessage(),"error");
        }
    }
    /**
     * 获取连接 portal
     * @return 连接对象
     */
    public static Connection getConnection1() throws SQLException {
        Connection conn = DriverManager.getConnection(jdbcUrl1, username, password);
        return conn;
    }

    /**
     * 获取连接 base
     * @return 连接对象
     */
    public static Connection getConnection2() throws SQLException {
        Connection conn = DriverManager.getConnection(jdbcUrl2, username, password);
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

    // 查询组件数据
    public static List<JarInfo> findJars(String sql) throws Exception{
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        List<JarInfo> list = new ArrayList<>();
        try {
            conn = JDBCUtils.getConnection1();
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

    // 查询组件cve数据
    public static List<JarInfo> findJarCves(String sql) throws Exception{
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        List<JarInfo> list = new ArrayList<>();
        try {
            conn = JDBCUtils.getConnection2();
            st = conn.createStatement();
            //执行sql
            rs = st.executeQuery(sql);
            JarInfo bean = null;
            while (rs.next()){
                String groupName = rs.getString("g");
                String artifactName = rs.getString("a");
                String version = rs.getString("v");
                String cveNo = rs.getString("custom_cve_no");
                String cnnvdNo = rs.getString("custom_cnnvd_no");
                bean = new JarInfo(groupName,artifactName,version);
                bean.setCveNo(cveNo);
                bean.setCnnvdNo(cnnvdNo);
                list.add(bean);
            }
        }finally {
            JDBCUtils.close(rs,st,conn);
        }
        return list;
    }

    // 查询cve风险等级数据
    public static List<CveInfo> findCves(String sql) throws Exception{
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        List<CveInfo> list = new ArrayList<>();
        try {
            conn = JDBCUtils.getConnection2();
            st = conn.createStatement();
            //执行sql
            rs = st.executeQuery(sql);
            CveInfo bean = null;
            while (rs.next()){
                String name = rs.getString("name");
                String severity = rs.getString("severity");
                bean = new CveInfo(name,null,severity);
                list.add(bean);
            }
        }finally {
            JDBCUtils.close(rs,st,conn);
        }
        return list;
    }
}
