package com.example.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private String courseId;//课程号
    private String courseName;//课程名
    private Integer credit;//学分
}
