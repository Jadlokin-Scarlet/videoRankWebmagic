package com.tilitili.spider.component;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.tilitili.common.emnus.TaskReason;
import com.tilitili.common.emnus.TaskStatus;
import com.tilitili.common.entity.Owner;
import com.tilitili.common.entity.TmpDataNew;
import com.tilitili.common.entity.VideoInfo;
import com.tilitili.common.entity.VideoPage;
import com.tilitili.common.entity.message.TaskMessage;
import com.tilitili.common.entity.view.BaseView;
import com.tilitili.common.entity.view.view.VideoView;
import com.tilitili.common.manager.OwnerManager;
import com.tilitili.common.manager.VideoInfoManager;
import com.tilitili.common.manager.VideoPageManager;
import com.tilitili.common.mapper.OwnerMapper;
import com.tilitili.common.mapper.TaskMapper;
import com.tilitili.common.mapper.TmpDataNewMapper;
import com.tilitili.spider.convert.ToOwner;
import com.tilitili.spider.convert.ToTmpDataNew;
import com.tilitili.spider.convert.ToVideoInfo;
import com.tilitili.spider.convert.ToVideoPage;
import com.tilitili.spider.service.JmsService;
import com.tilitili.spider.util.QQUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;

import java.util.*;
import java.util.stream.Collectors;

import static com.tilitili.spider.util.BilibiliApi.getVideoByAv;

@Slf4j
@Configuration
public class ViewSpiderConfig extends DuplicateRemovedScheduler implements PageProcessor, Pipeline {
    @Value("${spider.wait-time}")
    private Integer waitTime;

    private final Integer thread = 1;
    private final Boolean exitWhenComplete = false;
    private final Integer retryTimes = 9000;
    private final Integer sleepTime = 9000;
    private final String charset = "UTF-8";
    private final Set<Integer> acceptStatCode = new HashSet<>(Arrays.asList(412, 200));

    private final LinkedList<Request> requestLinkedList = new LinkedList<>();

    private final JmsService jmsService;
    private final QQUtil qqUtil;
    private final Environment environment;

    private final VideoInfoManager videoInfoManager;
    private final VideoPageManager videoPageManager;
    private final OwnerManager ownerManager;

    private final TaskMapper taskMapper;
    private final OwnerMapper ownerMapper;
    private final TmpDataNewMapper tmpDataNewMapper;

    private final ToVideoInfo toVideoInfo;
    private final ToVideoPage toVideoPage;
    private final ToOwner toOwner;
    private final ToTmpDataNew toTmpDataNew;

    @Autowired
    public ViewSpiderConfig(QQUtil qqUtil, VideoInfoManager videoInfoManager, TaskMapper taskMapper, JmsService jmsService, Environment environment, VideoPageManager videoPageManager, OwnerManager ownerManager, OwnerMapper ownerMapper, TmpDataNewMapper tmpDataNewMapper, ToVideoInfo toVideoInfo, ToVideoPage toVideoPage, ToOwner toOwner, ToTmpDataNew toTmpDataNew) {
        this.qqUtil = qqUtil;
        this.videoInfoManager = videoInfoManager;
        this.taskMapper = taskMapper;
        this.jmsService = jmsService;
        this.environment = environment;
        this.videoPageManager = videoPageManager;
        this.ownerManager = ownerManager;
        this.ownerMapper = ownerMapper;
        this.tmpDataNewMapper = tmpDataNewMapper;
        this.toVideoInfo = toVideoInfo;
        this.toVideoPage = toVideoPage;
        this.toOwner = toOwner;
        this.toTmpDataNew = toTmpDataNew;
    }

    @Bean
    public Spider viewSpider(ViewSpiderConfig spiderConfig) {
        return Spider.create(spiderConfig).addPipeline(spiderConfig).setScheduler(spiderConfig).setExitWhenComplete(exitWhenComplete).thread(thread);
    }

    @Override
    public Request poll(Task task) {
        if (requestLinkedList.isEmpty()) {
            TaskMessage taskMessage = jmsService.receiveAndConvert(TaskReason.SUPPLEMENT_VIDEO.destination);
            if (taskMessage == null) { return null;}
            log.info("receive spider video view task: {}", taskMessage);
            requestLinkedList.add(new Request(getVideoByAv(taskMessage.getValue(), taskMessage.getId())));
        }
        return requestLinkedList.poll();
    }

    @Override
    public void process(Page page) {
        String id = page.getUrl().regex("_id_=([^&]+)", 1).get();
        String av = page.getUrl().regex("aid=([^&]+)", 1).get();
        BaseView<VideoView> data = JSONObject.parseObject(page.getJson().get(), new TypeReference<BaseView<VideoView>>(){});
        Integer code = data.code;
        if (code.equals(-412) || code.equals(-504)) {
            log.error("被风控:{}", data);
            qqUtil.sendRiskError(this.getClass());
            try {
                Thread.sleep(waitTime * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            page.addTargetRequest(page.getUrl().get());
            page.setSkip(true);
        }else {
            page.putField("id", id);
            page.putField("av", av);
            page.putField("data", data);
        }
    }

    @Override
    public void process(ResultItems resultItems, us.codecraft.webmagic.Task task) {
        long id = Long.parseLong(resultItems.get("id"));
        long av = Long.parseLong(resultItems.get("av"));
        String ip = environment.getProperty("ip");
        BaseView<VideoView> data = resultItems.get("data");
        if (data == null) { return; }
        try {
            if (data.code != 0 || data.data == null) {
                log.error(JSONObject.toJSONString(data));
                VideoInfo videoInfo = new VideoInfo().setAv(av).setStatus(data.code);
                videoInfoManager.updateOrInsert(videoInfo);
                String message = data.data == null? "data is null": data.message;
                taskMapper.updateStatusAndRemarkById(id, TaskStatus.SPIDER.getValue(), TaskStatus.FAIL.getValue(), message);
                return;
            } else {
                VideoInfo videoInfo = new VideoInfo().setAv(av).setStatus(0);
                videoInfoManager.updateOrInsert(videoInfo);
            }
            VideoView videoView = data.data;
            VideoInfo videoInfo = toVideoInfo.fromVideoView(videoView);
            List<VideoPage> pageList = videoView.pages.stream().map(pageView -> toVideoPage.fromVideoView(pageView, videoView)).collect(Collectors.toList());
            Owner owner = toOwner.fromVideoView(videoView);
            TmpDataNew tmpDataNew = toTmpDataNew.fromVideoView(videoView);


            Owner oldOwner = ownerMapper.getByName(videoInfo.getOwner());
            // 拉黑
            if (oldOwner != null && oldOwner.getStatus() == 1) {
                videoInfo.setIsDelete(true);
            }

            videoInfoManager.updateOrInsert(videoInfo);

            pageList.forEach(videoPageManager::updateOrInsert);

            ownerManager.updateOrInsert(owner);

            TmpDataNew oldTmpDataNew = tmpDataNewMapper.getTmpDataNewByAv(av);
            if (oldTmpDataNew != null && oldTmpDataNew.getCollectTime() == null) {
                tmpDataNewMapper.updateTmpDataNew(tmpDataNew);
            }

            taskMapper.updateStatusAndIpById(id, TaskStatus.SPIDER.getValue(), TaskStatus.SUCCESS.getValue(), ip);
        } catch (Exception e) {
            log.error("持久化失败, av=" + av, e);
            taskMapper.updateStatusAndIpAndRemarkById(id, TaskStatus.SPIDER.getValue(), TaskStatus.FAIL.getValue(), ip, e.getMessage());
        }
    }

    @Override
    public void push(Request request, Task task) {
        requestLinkedList.addFirst(request);
    }

    @Override
    public Site getSite() {
        return Site.me().setRetryTimes(retryTimes).setSleepTime(sleepTime).setCharset(charset).setAcceptStatCode(acceptStatCode);
    }

}
