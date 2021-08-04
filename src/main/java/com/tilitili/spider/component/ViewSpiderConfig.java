package com.tilitili.spider.component;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.tilitili.common.emnus.TaskReason;
import com.tilitili.common.emnus.TaskStatus;
import com.tilitili.common.entity.*;
import com.tilitili.common.entity.message.TaskMessage;
import com.tilitili.common.entity.view.BaseView;
import com.tilitili.common.entity.view.view.VideoView;
import com.tilitili.common.manager.*;
import com.tilitili.common.mapper.OwnerMapper;
import com.tilitili.common.mapper.TaskMapper;
import com.tilitili.common.mapper.TmpDataNewMapper;
import com.tilitili.common.mapper.VideoDataMapper;
import com.tilitili.common.utils.Log;
import com.tilitili.spider.convert.ToOwner;
import com.tilitili.spider.convert.ToTmpDataNew;
import com.tilitili.spider.convert.ToVideoInfo;
import com.tilitili.spider.convert.ToVideoPage;
import com.tilitili.spider.service.JmsService;
import com.tilitili.spider.util.Convert;
import com.tilitili.spider.util.QQUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    private final Integer retryTimes = 10000;
    private final Integer sleepTime = 10000;
    private final String charset = "UTF-8";
    private final Set<Integer> acceptStatCode = new HashSet<>(Arrays.asList(412, 200));

    private final LinkedList<Request> requestLinkedList = new LinkedList<>();

    private final JmsService jmsService;
    private final Convert convert;
    private final QQUtil qqUtil;

    private final VideoInfoManager videoInfoManager;
    private final VideoDataManager videoDataManager;
    private final VideoPageManager videoPageManager;
    private final OwnerManager ownerManager;
    private final RightManager rightManager;

    private final VideoDataMapper videoDataMapper;
    private final TaskMapper taskMapper;
    private final OwnerMapper ownerMapper;
    private final TmpDataNewMapper tmpDataNewMapper;

    private final ToVideoInfo toVideoInfo;
    private final ToVideoPage toVideoPage;
    private final ToOwner toOwner;
    private final ToTmpDataNew toTmpDataNew;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.of("Asia/Shanghai"));

    @Autowired
    public ViewSpiderConfig(QQUtil qqUtil, VideoInfoManager videoInfoManager, TaskMapper taskMapper, JmsService jmsService, Convert convert, VideoDataManager videoDataManager, VideoPageManager videoPageManager, OwnerManager ownerManager, RightManager rightManager, VideoDataMapper videoDataMapper, OwnerMapper ownerMapper, TmpDataNewMapper tmpDataNewMapper, ToVideoInfo toVideoInfo, ToVideoPage toVideoPage, ToOwner toOwner, ToTmpDataNew toTmpDataNew) {
        this.qqUtil = qqUtil;
        this.videoInfoManager = videoInfoManager;
        this.taskMapper = taskMapper;
        this.jmsService = jmsService;
        this.convert = convert;
        this.videoDataManager = videoDataManager;
        this.videoPageManager = videoPageManager;
        this.ownerManager = ownerManager;
        this.rightManager = rightManager;
        this.videoDataMapper = videoDataMapper;
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
            Log.info("receive spider video view task: {}", taskMessage);
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

        } catch (Exception e) {
            log.error("持久化失败, av=" + av, e);
            taskMapper.updateStatusAndRemarkById(id, TaskStatus.SPIDER.getValue(), TaskStatus.FAIL.getValue(), e.getMessage());
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
//
//    private void saveVideoInfo(JSONObject obj) {
//        VideoInfo videoInfo = new VideoInfo()
//                .setBv(obj.getString("bvid"))
//                .setAv(obj.getLong("aid"))
//                .setType(obj.getString("tname"))
//                .setCopyright(obj.getInteger("copyright") - 1 == 1)
//                .setImg(obj.getString("pic"))
//                .setName(obj.getString("title"))
//                .setPubTime(formatter.format(Instant.ofEpochSecond(obj.getLong("pubdate"))))
//                .setDescription(obj.getString("desc"))
//                .setState(obj.getLong("state"))
//                .setAttribute(obj.getLong("attribute"))
//                .setDuration(obj.getLong("duration"))
//                .setOwner(obj.getJSONObject("owner").getString("name"))
//                .setDynamic(obj.getString("dynamic"));
//
//        Owner owner = ownerMapper.getByName(videoInfo.getOwner());
//        // 拉黑
//        if (owner != null && owner.getStatus() == 1) {
//            videoInfo.setIsDelete(true);
//        }
//
//        videoInfoManager.updateOrInsert(videoInfo);
//    }
//
//    private void saveVideoData(JSONObject obj) {
//        JSONObject stat = obj.getJSONObject("stat");
//        VideoData videoData = new VideoData()
//                .setAv(obj.getLong("aid"))
//                .setIssue(videoDataMapper.getNewIssue())
//                .setDanmaku(stat.getInteger("danmaku"))
//                .setShare(stat.getInteger("share"))
//                .setLike(stat.getInteger("like"))
//                .setDislike(stat.getInteger("dislike"))
//                .setEvaluation(stat.getString("evaluation"));
//        videoDataManager.updateOrInsert(videoData);
//    }
//
//    private void saveVideoPageList(JSONObject obj) {
//        JSONArray pages = obj.getJSONArray("pages");
//        if (!obj.containsKey("pages")) {
//            log.warn("该视频无page信息");
//            return ;
//        }
//        IntStream.range(0, obj.getJSONArray("pages").size())
//                .mapToObj(pages::getJSONObject)
//                .map(this::newVideoPage)
//                .map(videoPage -> videoPage.setAv(obj.getLong("aid")))
//                .forEach(videoPageManager::updateOrInsert);
//    }
//
//    private VideoPage newVideoPage(JSONObject obj) {
//        return new VideoPage()
//                .setCid(obj.getLong("cid"))
//                .setPage(obj.getInteger("page"))
//                .setFrom(obj.getString("from"))
//                .setPart(obj.getString("part"))
//                .setDuration(obj.getLong("duration"))
//                .setVid(obj.getString("vid"))
//                .setWeblink(obj.getString("weblink"));
//    }
//
//    private void saveOwner(JSONObject obj) {
//        JSONObject ownerJson = obj.getJSONObject("owner");
//        Owner owner = new Owner()
//                .setUid(ownerJson.getLong("mid"))
//                .setName(ownerJson.getString("name"))
//                .setFace(ownerJson.getString("face"));
//        ownerManager.updateOrInsert(owner);
//    }
//
//    private void saveRight(JSONObject obj, Long av) {
//        JSONObject rights = obj.getJSONObject("rights");
//        Right right = new Right()
//                .setAv(obj.getLong("aid"))
//                .setBp(rights.getShort("bp"))
//                .setElec(rights.getShort("elec"))
//                .setDownload(rights.getShort("download"))
//                .setMovie(rights.getShort("movie"))
//                .setPay(rights.getShort("pay"))
//                .setHd5(rights.getShort("hd5"))
//                .setNoReprint(rights.getShort("no_reprint"))
//                .setAutoplay(rights.getShort("autoplay"))
//                .setUgcPay(rights.getShort("ugc_pay"))
//                .setIsCooperation(rights.getShort("is_cooperation"))
//                .setUgcPayPreview(rights.getShort("ugc_pay_preview"))
//                .setNoBackground(rights.getShort("no_background"));
//        rightManager.updateOrInsert(right);
//    }
//
//    private void calculationVideoDataVideoData(JSONObject obj) {
//        JSONObject stat = obj.getJSONObject("stat");
//        VideoData videoData = new VideoData()
//                .setAv(obj.getLong("aid"))
//                .setIssue(videoDataMapper.getNewIssue())
//                .setView(stat.getInteger("view"))
//                .setDanmaku(stat.getInteger("danmaku"))
//                .setReply(stat.getInteger("reply"))
//                .setFavorite(stat.getInteger("favorite"))
//                .setCoin(stat.getInteger("coin"))
//                .setShare(stat.getInteger("share"))
//                .setLike(stat.getInteger("like"))
//                .setDislike(stat.getInteger("dislike"))
//                .setEvaluation(stat.getString("evaluation"));
//
//
//
//
//    }
}
