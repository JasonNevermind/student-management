package com.example.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Class {

    private String className;//班级名
    private String majorName;//专业名
    private int classNum;//班级人数，默认为0

    public Class(String className, String majorName) {
        this.className = className;
        this.majorName = majorName;
    }
}
