package com.example.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private String studentId;//学号
    private String studentName;//姓名
    private String studentGender;//性别
    private Integer studentAge;//年龄
    private String className;//班级名
}
