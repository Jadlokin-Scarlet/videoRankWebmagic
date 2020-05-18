package com.jadlokin.test.webmagic.mapper;

import com.jadlokin.test.webmagic.entity.Av;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AvMapper extends AvBaseMapper{
    @Select("select av from touhou_all where av > 66000")
    List<Long> selectAllAv();
}