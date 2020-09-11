package com.seczone.sca.idea.plugin.model;


import com.alibaba.fastjson.JSONObject;
import com.seczone.sca.idea.plugin.util.Utils;
import lombok.Data;
import org.fest.util.Lists;

import java.util.List;

/**
 * @description: 组件漏洞信息
 * @author: hew
 * @create: 2020-09-11 10:17
 **/

@Data
public class CveInfo {

    private String name;
    private String description;
    private String severity;
    private String showInfo;

    public CveInfo(){}
    public CveInfo(String name, String description, String severity) {
        this.name = name;
        this.description = description;
        this.severity = severity;
    }

    public String getShowInfo() {
        String gradeStr=(null == severity || severity.length() <1)?"UNKNOWN":severity;
        return gradeStr+"   "+name+"   "+ (Utils.isEmpty(description)?"":description);
    }


    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
