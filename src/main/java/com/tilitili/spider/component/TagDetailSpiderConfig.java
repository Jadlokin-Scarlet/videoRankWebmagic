package com.tilitili.spider.component;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.tilitili.common.emnus.TaskStatus;
import com.tilitili.common.entity.message.TaskMessage;
import com.tilitili.common.mapper.TaskMapper;
import com.tilitili.common.utils.Log;
import com.tilitili.spider.service.JmsService;
import com.tilitili.spider.view.BaseView;
import com.tilitili.spider.view.tagDetail.TagDetailView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;

import java.util.*;

import static com.tilitili.spider.util.BilibiliApi.getVideoForTagById;

@Configuration
public class TagDetailSpiderConfig extends DuplicateRemovedScheduler implements PageProcessor, Pipeline {

    private final Integer thread = 1;
    private final Boolean exitWhenComplete = false;
    private final Integer retryTimes = 5000;
    private final Integer sleepTime = 5000;
    private final String charset = "UTF-8";
    private final Set<Integer> acceptStatCode = new HashSet<>(Arrays.asList(412, 200));

    private final LinkedList<Request> requestLinkedList = new LinkedList<>();

    private final TaskMapper taskMapper;
    private final JmsService jmsService;

    @Bean
    public Spider tagDetailSpider(TagDetailSpiderConfig spiderConfig) {
        return Spider.create(spiderConfig).addPipeline(spiderConfig).setScheduler(spiderConfig).setExitWhenComplete(exitWhenComplete).thread(thread);
    }

    @Autowired
    public TagDetailSpiderConfig(TaskMapper taskMapper, JmsService jmsService) {
        this.taskMapper = taskMapper;
        this.jmsService = jmsService;
    }

    @Override
    public Request poll(Task task) {
        if (requestLinkedList.isEmpty()) {
            TaskMessage taskMessage = jmsService.receiveAndConvert("SpiderVideoTagTaskMessage");
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
        BaseView<List<TagDetailView>> data = JSONObject.parseObject(page.getJson().get(), new TypeReference<BaseView<List<TagDetailView>>>() {});
        if (Objects.equals(data.code, -412)) {
            Log.error("被风控: ", data);
            try {
                Thread.sleep(20 * 60 * 1000);
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
        BaseView<List<TagDetailView>> data = resultItems.get("data");
        if (data.code != 0) {
            Log.error("接口返回状态不为0: %s", data);
            taskMapper.updateStatusAndRemarkById(taskId, TaskStatus.SPIDER.getValue(), TaskStatus.FAIL.getValue(), data.message);
            return;
        }
        try {

            taskMapper.updateStatusById(taskId, TaskStatus.SPIDER.getValue(), TaskStatus.SUCCESS.getValue());
        } catch (Exception e) {
            Log.error("持久化失败, av=" + av, e);
            taskMapper.updateStatusAndRemarkById(taskId, TaskStatus.SPIDER.getValue(), TaskStatus.FAIL.getValue(), e.getMessage());
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
