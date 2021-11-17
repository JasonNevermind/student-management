package com.example.mapper;

import com.example.bean.Ap;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
@Mapper
public interface ApMapper {
    //查询所有奖惩信息
    List<Ap> selectApList();

    //查询奖惩信息的指定属性
    List<Ap> selectApListForSome();

    //添加奖惩信息
    void insertAp(Ap ap);

    //删除奖惩信息
    void deleteApById(int apId);

    //获得所有奖惩信息学生学号
    List<String> selectAllApStudentId();

}
