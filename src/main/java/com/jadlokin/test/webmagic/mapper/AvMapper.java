package com.jadlokin.test.webmagic.mapper;

import com.jadlokin.test.webmagic.entity.Av;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AvMapper extends AvBaseMapper{
    @Select("select av from touhou_all where av > 28599000")
    List<Long> selectAllAv();

    @Select("\tselect one_av from \n" +
            "\t(select av as one_av from touhou_all) one full outer join \n" +
            "\t(select av as there_av from [right]) there on one_av = there_av\n" +
            "\twhere (one_av is null or there_av is null)")
    List<Long> selectOtherAv();
}