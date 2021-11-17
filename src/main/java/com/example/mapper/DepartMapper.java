package com.example.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DepartMapper {

    //查询所有院系名
    List<String> selectAllDepartName();

    //添加院系，只需一个院系名
    void insertDepart(String departName);

    //修改院系，需要旧的和新的院系名
    void updateDepart(Map<String,String> map);

    //删除院系
    void deleteDepartByName(String departName);
}
