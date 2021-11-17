package com.example.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

//成绩查询使用，选课列表使用
public class ScInfo {
    private String studentId;
    private String studentName;
    private String courseId;
    private String courseName;
    private Integer credit;
    private Integer grade;
}
