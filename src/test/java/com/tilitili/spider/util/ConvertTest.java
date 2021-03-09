package com.tilitili.spider.util;

import com.tilitili.common.StartApplication;
import com.tilitili.common.entity.VideoInfo;
import com.tilitili.spider.view.view.VideoView;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = StartApplication.class)
@EnableAutoConfiguration
class ConvertTest {
//    @Autowired
//    private Convert convert;
    @Test
    void videoViewToVideoInfo() {
        Convert INSTANCE = Mappers.getMapper( Convert.class );
        VideoView videoView = new VideoView();
        videoView.pubdate = 1436866637L;
        videoView.copyright = 1;
        VideoInfo videoInfo = INSTANCE.VideoViewToVideoInfo(videoView);
        System.out.println(videoInfo.getPubTime());
    }
}