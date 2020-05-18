package com.jadlokin.test.webmagic.mapper;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface VideoMapper{

    List<Long> selectAllAvThatNoInfo();

    @Select("select one_av as av  from " +
            "(select av as one_av, is_delete from video_info where is_delete = 0) one left join " +
            "(select av as two_av, page as two_page, issue from video_data where issue = 11) two on one_av = two_av left join " +
            "(select av as there_av, max(page) as there_page from page group by av) there on one_av = there_av " +
            "where two_page != there_page")
    List<Long> selectNoPageVideo();

}