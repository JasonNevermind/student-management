package com.example.mapper;

import com.example.bean.Class;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClassMapper {
    //查询所有班级
    List<Class> selectClassList();

    //根据班级名查询班级
    Class selectClassByName(String className);

    //添加班级
    void insertClass(Class cla);

    //修改班级
    void updateClass(Map<String, Object> map);

    //删除班级
    void deleteClassByName(String className);

    //获得所有班级名
    List<String> selectAllClassName();

    //获得所有班级的专业名
    List<String> selectAllMajorNameByClass();
}
