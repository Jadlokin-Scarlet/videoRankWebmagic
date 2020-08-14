package com.jadlokin.test.webmagic.mapper;

import com.jadlokin.test.webmagic.entity.Right;

public interface RightBaseMapper {
    int deleteByPrimaryKey(Long av);

    int insert(Right record);

    int insertSelective(Right record);

    Right selectByPrimaryKey(Long av);

    int updateByPrimaryKeySelective(Right record);

    int updateByPrimaryKey(Right record);
}