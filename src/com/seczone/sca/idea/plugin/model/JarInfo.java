package com.seczone.sca.idea.plugin.model;



import java.util.ArrayList;
import java.util.List;

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
    private List<CveInfo> cveInfoList = new ArrayList();
    private String cveNo;
    private String cnnvdNo;

    public JarInfo(){}
    public JarInfo(String g, String a, String v) {
        this.g = g;
        this.a = a;
        this.v = v;
    }
    public JarInfo(String g, String a, String v, String grade) {
        this.g = g;
        this.a = a;
        this.v = v;
        this.grade = grade;
    }

    public String getShowInfo() {
        String gradeStr=(null == grade || grade.length() <1)?"UNKNOWN":grade;
        return gradeStr+"   "+String.format("%s:%s@%s",g,a,v);
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

    public List<CveInfo> getCveInfoList() {
        return cveInfoList;
    }

    public void setCveInfoList(List<CveInfo> cveInfoList) {
        this.cveInfoList = cveInfoList;
    }

    public String getCveNo() {
        return cveNo;
    }

    public void setCveNo(String cveNo) {
        this.cveNo = cveNo;
    }

    public String getCnnvdNo() {
        return cnnvdNo;
    }

    public void setCnnvdNo(String cnnvdNo) {
        this.cnnvdNo = cnnvdNo;
    }
}
