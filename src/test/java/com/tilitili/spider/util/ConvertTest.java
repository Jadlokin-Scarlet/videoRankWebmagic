package com.tilitili.spider.util;

import com.tilitili.common.StartApplication;
import com.tilitili.common.entity.VideoInfo;
import com.tilitili.common.entity.view.view.VideoView;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.ZoneId;

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
//        videoView.aid = 672365514L;
//        videoView.bvid = "BV1NU4y1Y7aT";
//        videoView.tname = "单机游戏";
//        videoView.pic = "http://i1.hdslb.com/bfs/archive/1630076127fd80d6f4c9bce28be20c0e8c812be4.jpg";
//        videoView.title = "华彩乱战速通大赛-华彩乱战1Story模式EXNoContinue%（EXNM%）52分03秒通关";
//        videoView.desc = "注：7:02处停顿了一下（有事处理）\\n第一届华彩乱战速通大赛参赛作品\\n通关用时：52:03.15";
        videoView.pubdate = 1616744778L;
        videoView.copyright = 1;
//        videoView.duration = 1L;
        VideoInfo videoInfo = INSTANCE.VideoViewToVideoInfo(videoView);
        System.out.println(videoInfo.getPubTime());
    }
}