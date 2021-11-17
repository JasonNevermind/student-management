package com.example.mapper;


import com.example.bean.Student;
import com.example.bean.StudentInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface StudentMapper {

    //学生信息视图
    List<StudentInfo> getStudentInfoView();

    //查询所有学生
    List<Student> selectStudentList();

    //根据id查询学生
    Student selectStudentById(String id);

    //添加学生
    void insertStudent(Student student);

    //修改学生
    void updateStudent(HashMap<String,Object> map);

    //根据id删除学生
    void deleteStudentById(String id);

    //获得所有学生的学号
    List<String> selectAllStudentId();
}
