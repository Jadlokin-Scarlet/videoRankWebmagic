package com.jadlokin.test.webmagic.mapper;

import com.jadlokin.test.webmagic.entity.Av;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AvMapper extends AvBaseMapper{
    @Select("select av from touhou_all where av > 28599000")
    List<Long> selectAllAv();

    @Select("select touhou_all.av from touhou_all\n" +
            "    left join video_info on touhou_all.av = video_info.av\n" +
            "    left join video_data on touhou_all.av = video_data.av and issue = 7\n" +
            "    left join [right] on touhou_all.av = [right].av\n" +
            "    left join touhouTag on touhou_all.av = touhouTag.av\n" +
            "    left join (select av, count(1) as page from page group by av) page on touhou_all.av = page.av\n" +
            "    left join owner on video_info.owner = owner.name\n" +
            "where video_info.av is null\n" +
            "    or [right].av is null\n" +
            "    or touhouTag.av is null\n" +
            "    or page.av is null\n" +
            "    or owner.uid is null\n" +
            "    or video_info.bv is null\n" +
            "    or video_info.is_delete = 1\n" +
            "    or video_data.danmaku is null\n" +
            "    or touhouTag.tag1 is null\n" +
            "    or page.page != video_data.page\n" +
            "order by touhou_all.av desc")
    List<Long> selectOtherAv();
}