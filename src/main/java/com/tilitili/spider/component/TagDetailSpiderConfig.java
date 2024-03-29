package com.tilitili.spider.component;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.tilitili.common.emnus.TaskReason;
import com.tilitili.common.emnus.TaskStatus;
import com.tilitili.common.entity.message.TaskMessage;
import com.tilitili.common.manager.VideoInfoManager;
import com.tilitili.common.mapper.TaskMapper;
import com.tilitili.spider.service.JmsService;
import com.tilitili.spider.util.Convert;
import com.tilitili.spider.util.QQUtil;
import com.tilitili.common.entity.view.BaseView;
import com.tilitili.common.entity.view.tagDetail.TagDetailView;
import com.tilitili.common.entity.view.view.VideoView;
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

import static com.tilitili.spider.util.BilibiliApi.getVideoForTagById;

@Slf4j
@Configuration
public class TagDetailSpiderConfig extends DuplicateRemovedScheduler implements PageProcessor, Pipeline {
    @Value("${spider.wait-time}")
    private Integer waitTime;

    private final Integer thread = 1;
    private final Boolean exitWhenComplete = false;
    private final Integer retryTimes = 10000;
    private final Integer sleepTime = 10000;
    private final String charset = "UTF-8";
    private final Set<Integer> acceptStatCode = new HashSet<>(Arrays.asList(412, 200));

    private final LinkedList<Request> requestLinkedList = new LinkedList<>();

    private final VideoInfoManager videoInfoManager;
    private final TaskMapper taskMapper;
    private final JmsService jmsService;
    private final Convert convert;
    private final QQUtil qqUtil;
    private final Environment environment;

    @Bean
    public Spider tagDetailSpider(TagDetailSpiderConfig spiderConfig) {
        return Spider.create(spiderConfig).addPipeline(spiderConfig).setScheduler(spiderConfig).setExitWhenComplete(exitWhenComplete).thread(thread);
    }

    @Autowired
    public TagDetailSpiderConfig(QQUtil qqUtil, VideoInfoManager videoInfoManager, TaskMapper taskMapper, JmsService jmsService, Convert convert, Environment environment) {
        this.qqUtil = qqUtil;
        this.videoInfoManager = videoInfoManager;
        this.taskMapper = taskMapper;
        this.jmsService = jmsService;
        this.convert = convert;
        this.environment = environment;
    }

    @Override
    public Request poll(Task task) {
        if (requestLinkedList.isEmpty()) {
            TaskMessage taskMessage = jmsService.receiveAndConvert(TaskReason.SPIDER_NEW_VIDEO.destination);
            log.info("receive spider video tag task: {}", taskMessage);
            if (taskMessage == null) { return null;}
            for (int page = 1; page <= 20; page ++) {
                requestLinkedList.add(new Request(getVideoForTagById(page, taskMessage.getValue(), taskMessage.getId())));
            }
        }
        return requestLinkedList.poll();
    }

    @Override
    public void process(Page page) {
        Long taskId = Long.valueOf(page.getUrl().regex("_id_=([^&]+)").get());
        Long tagId = Long.valueOf(page.getUrl().regex("tag_id=([^&]+)").get());
        BaseView<TagDetailView> data = JSONObject.parseObject(page.getJson().get(), new TypeReference<BaseView<TagDetailView>>() {});
        if (Objects.equals(data.code, -412)) {
            log.error("被风控: {}", data);
            qqUtil.sendRiskError(this.getClass());
            try {
                Thread.sleep(waitTime * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            page.addTargetRequest(page.getUrl().get());
            page.setSkip(true);
        }else {
            page.putField("tagId", tagId);
            page.putField("taskId", taskId);
            page.putField("data", data);
        }
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        Long av = resultItems.get("av");
        Long taskId = resultItems.get("taskId");
        String ip = environment.getProperty("ip");
        BaseView<TagDetailView> req = resultItems.get("data");
        if (req.code != 0 || req.data == null || req.data.news == null || req.data.news.archives == null) {
            log.error("接口返回状态不为0: {}", req);
            taskMapper.updateStatusAndRemarkById(taskId, TaskStatus.SPIDER.getValue(), TaskStatus.FAIL.getValue(), req.message);
            return;
        }
        try {
            List<VideoView> videoViewList = req.data.news.archives;
            videoViewList.parallelStream().map(convert::VideoViewToVideoInfo).forEach(videoInfoManager::updateOrInsert);
            taskMapper.updateStatusAndIpById(taskId, TaskStatus.SPIDER.getValue(), TaskStatus.SUCCESS.getValue(), ip);
        } catch (Exception e) {
            log.error("持久化失败, av=" + av, e);
            taskMapper.updateStatusAndIpAndRemarkById(taskId, TaskStatus.SPIDER.getValue(), TaskStatus.FAIL.getValue(), ip, e.getMessage());
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
