package com.example.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sc {
    private String studentId;//学号
    private String courseId;//课程号
    private Integer grade;//成绩
}
