package com.example.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

//学生信息视图使用
public class StudentInfo {
    private String studentId;
    private String studentName;
    private String className;
    private String majorName;
    private String departName;
}
