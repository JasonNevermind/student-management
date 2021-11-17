package com.example.mapper;

import com.example.bean.Sc;
import com.example.bean.ScInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface ScMapper {

    //选课列表
    List<ScInfo> selectScList();

    //根据id查询
    Sc selectScById(@Param("studentId") String studentId, @Param("courseId") String courseId);

    //根据学号查询选课信息，成绩查询使用
    List<ScInfo> selectScInfoById(String studentId);

    //根据学号查询课程号，查询学生选的课程
    List<String> selectCourseIdById(String studentId);

    //添加
    void insertSc(Sc sc);

    //修改
    void updateSc(HashMap<String, Object> map);

    //根据id删除
    void deleteScById(@Param("studentId") String studentId, @Param("courseId") String courseId);

    //获得所有学生的学号
    List<String> selectAllStudentId();

    //获得所有课程的课程号
    List<String> selectAllCourseId();
}
