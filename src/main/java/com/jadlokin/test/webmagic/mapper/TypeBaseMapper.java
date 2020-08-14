package com.jadlokin.test.webmagic.mapper;

import com.jadlokin.test.webmagic.entity.Type;

public interface TypeBaseMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Type record);

    int insertSelective(Type record);

    Type selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Type record);

    int updateByPrimaryKey(Type record);
}