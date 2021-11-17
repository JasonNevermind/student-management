package com.example.mapper;

import com.example.bean.Major;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MajorMapper {
    //查询所有专业
    List<Major> selectMajorList();

    //根据专业名查询专业
    Major selectMajorByName(String majorName);

    //添加专业
    void insertMajor(Major major);

    //修改专业
    void updateMajor(Map<String,Object> map);

    //删除专业
    void deleteMajorByName(String majorName);

    //获得所有专业名
    List<String> selectAllMajorName();

    //获得所有专业的院系名
    List<String> selectAllDepartNameByMajor();
}
