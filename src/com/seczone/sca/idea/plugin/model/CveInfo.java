package com.seczone.sca.idea.plugin.model;


import com.seczone.sca.idea.plugin.util.Utils;


/**
 * @description: 组件漏洞信息
 * @author: hew
 * @create: 2020-09-11 10:17
 **/

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
        String gradeStr=(null == severity || severity.length() <1)?"UNKNOWN":getSeverity();
        return gradeStr+"   "+name+"   "+ (Utils.isEmpty(description)?"":description);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeverity() {
        if ("高危".equals(severity)){
            severity = "HIGH";
        }else if("中危".equals(severity)){
            severity = "MEDIUM";
        }else if("超危".equals(severity)){
            severity = "CRITICAL";
        }else if("低危".equals(severity)){
            severity = "LOW";
        }
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
