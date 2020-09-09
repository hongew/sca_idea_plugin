package com.seczone.sca.idea.plugin.model;


import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @description: 组件安全信息
 * @author: hew
 * @create: 2020-09-09 16:17
 **/

public class JarInfo {

    private String g;
    private String a;
    private String v;
    private String grade;
    private String showInfo;

    public JarInfo(){}
    public JarInfo(String g, String a, String v, String grade) {
        this.g = g;
        this.a = a;
        this.v = v;
        this.grade = grade;
    }

    public String getG() {
        return g;
    }

    public void setG(String g) {
        this.g = g;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getShowInfo() {
        String gradeStr=(null == grade || grade.length() <1)?"unknown":grade;
        return gradeStr+"   "+String.format("%s:%s@%s",g,a,v);
    }

    public void setShowInfo(String showInfo) {
        this.showInfo = showInfo;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
