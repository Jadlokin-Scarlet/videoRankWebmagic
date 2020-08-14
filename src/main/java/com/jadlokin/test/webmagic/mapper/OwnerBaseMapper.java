package com.jadlokin.test.webmagic.mapper;

import com.jadlokin.test.webmagic.entity.Owner;

public interface OwnerBaseMapper {
    int deleteByPrimaryKey(Long uid);

    int insert(Owner record);

    int insertSelective(Owner record);

    Owner selectByPrimaryKey(Long uid);

    int updateByPrimaryKeySelective(Owner record);

    int updateByPrimaryKey(Owner record);
}