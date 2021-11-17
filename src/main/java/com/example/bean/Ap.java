package com.example.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Ap {
    private int apId;//序号
    private String studentId;//学号
    private String studentName;//姓名
    private String apTime;//时间字符串
    private String apType;//类型（奖励/惩罚）
    private String apDesc;//说明

    public Ap(String studentId, String apTime, String apType, String apDesc) {
        this.studentId = studentId;
        this.apTime = apTime;
        this.apType = apType;
        this.apDesc = apDesc;
    }
}
