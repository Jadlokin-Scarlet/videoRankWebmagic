package com.jadlokin.test.webmagic.mapper;

import com.jadlokin.test.webmagic.entity.Av;

public interface AvBaseMapper {
    int insert(Av record);

    int insertSelective(Av record);
}