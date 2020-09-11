package com.seczone.sca.idea.plugin.model;


import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.fest.util.Lists;

import java.util.List;

/**
 * @description: 组件安全信息
 * @author: hew
 * @create: 2020-09-09 16:17
 **/

@Data
public class JarInfo {

    private String g;
    private String a;
    private String v;
    private String grade;
    private String showInfo;
    private List<CveInfo> cveInfoList = Lists.newArrayList();
    private String cveNo;

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
        String gradeStr=(null == grade || grade.length() <1)?"unknown":grade;
        return gradeStr+"   "+String.format("%s:%s@%s",g,a,v);
    }


    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
