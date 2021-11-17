package com.example.mapper;


import com.example.bean.Course;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CourseMapper {
    //查询所有课程
    List<Course> selectCourseList();

    //根据id查询课程
    Course selectCourseById(String id);

    //添加课程
    void insertCourse(Course course);

    //修改课程
    void updateCourse(Map<String,Object> map);

    //根据id删除课程
    void deleteCourseById(String id);

    //获得所有课程的课程号
    List<String> selectAllCourseId();
}
