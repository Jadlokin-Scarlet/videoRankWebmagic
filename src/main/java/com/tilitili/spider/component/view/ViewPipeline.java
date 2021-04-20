package com.tilitili.spider.component.view;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tilitili.common.emnus.TaskStatus;
import com.tilitili.common.entity.*;
import com.tilitili.common.manager.*;
import com.tilitili.common.mapper.TaskMapper;
import com.tilitili.common.mapper.VideoDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.IntStream;

@Slf4j
@Component
public class ViewPipeline implements Pipeline {

	private final VideoInfoManager videoInfoManager;
	private final VideoDataManager videoDataManager;
	private final VideoPageManager videoPageManager;
	private final OwnerManager ownerManager;
	private final RightManager rightManager;

	private final VideoDataMapper videoDataMapper;
	private final TaskMapper taskMapper;

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
			.withZone(ZoneId.of("Asia/Shanghai"));

	@Autowired
	public ViewPipeline(VideoPageManager videoPageManager, VideoInfoManager videoInfoManager, VideoDataManager videoDataManager, OwnerManager ownerManager, RightManager rightManager, VideoDataMapper videoDataMapper, TaskMapper taskMapper) {
		this.videoPageManager = videoPageManager;
		this.videoInfoManager = videoInfoManager;
		this.videoDataManager = videoDataManager;
		this.ownerManager = ownerManager;
		this.rightManager = rightManager;
		this.videoDataMapper = videoDataMapper;
		this.taskMapper = taskMapper;
	}

	@Override
	public void process(ResultItems resultItems, us.codecraft.webmagic.Task task) {
		JSONObject rep = resultItems.get("rep");
		if (rep == null) {
			return;
		}
		long av = Long.parseLong(resultItems.get("av"));
		long id = Long.parseLong(resultItems.get("id"));
		try {
			if (!rep.getInteger("code").equals(0) || rep.getJSONObject("data") == null) {
				log.error(rep.toString());
				VideoInfo videoInfo = new VideoInfo().setAv(av).setStatus(rep.getInteger("code"));
				videoInfoManager.updateOrInsert(videoInfo);
				String message = rep.getJSONObject("data") == null? "data is null": rep.getString("message");
				taskMapper.updateStatusAndRemarkById(id, TaskStatus.SPIDER.getValue(), TaskStatus.FAIL.getValue(), message);
				return;
			}else {
				VideoInfo videoInfo = new VideoInfo().setAv(av).setStatus(0);
				videoInfoManager.updateOrInsert(videoInfo);
			}
			JSONObject data = rep.getJSONObject("data");
			saveVideoInfo(data);
//			saveVideoData(data);
			saveVideoPageList(data);
			saveOwner(data);
			saveRight(data, av);
			taskMapper.updateStatusById(id, TaskStatus.SPIDER.getValue(), TaskStatus.SUCCESS.getValue());
		} catch (Exception e) {
			log.error("持久化失败, av=" + av, e);
			taskMapper.updateStatusAndRemarkById(id, TaskStatus.SPIDER.getValue(), TaskStatus.FAIL.getValue(), e.getMessage());
		}
	}

	private void saveVideoInfo(JSONObject obj) {
		VideoInfo videoInfo = new VideoInfo()
				.setBv(obj.getString("bvid"))
				.setAv(obj.getLong("aid"))
				.setType(obj.getString("tname"))
				.setCopyright(obj.getInteger("copyright") - 1 == 1)
				.setImg(obj.getString("pic"))
				.setName(obj.getString("title"))
				.setPubTime(formatter.format(Instant.ofEpochSecond(obj.getLong("pubdate"))))
				.setDescription(obj.getString("desc"))
				.setState(obj.getLong("state"))
				.setAttribute(obj.getLong("attribute"))
				.setDuration(obj.getLong("duration"))
				.setOwner(obj.getJSONObject("owner").getString("name"))
				.setDynamic(obj.getString("dynamic"));
		videoInfoManager.updateOrInsert(videoInfo);
	}

	private void saveVideoData(JSONObject obj) {
		JSONObject stat = obj.getJSONObject("stat");
		VideoData videoData = new VideoData()
				.setAv(obj.getLong("aid"))
				.setIssue(videoDataMapper.getNewIssue())
				.setDanmaku(stat.getInteger("danmaku"))
				.setShare(stat.getInteger("share"))
				.setLike(stat.getInteger("like"))
				.setDislike(stat.getInteger("dislike"))
				.setEvaluation(stat.getString("evaluation"));
		videoDataManager.updateOrInsert(videoData);
	}

	private void saveVideoPageList(JSONObject obj) {
		JSONArray pages = obj.getJSONArray("pages");
		if (!obj.containsKey("pages")) {
			log.warn("该视频无page信息");
			return ;
		}
		IntStream.range(0, obj.getJSONArray("pages").size())
				.mapToObj(pages::getJSONObject)
				.map(this::newVideoPage)
				.map(videoPage -> videoPage.setAv(obj.getLong("aid")))
				.forEach(videoPageManager::updateOrInsert);
	}

	private VideoPage newVideoPage(JSONObject obj) {
		return new VideoPage()
				.setCid(obj.getLong("cid"))
				.setPage(obj.getInteger("page"))
				.setFrom(obj.getString("from"))
				.setPart(obj.getString("part"))
				.setDuration(obj.getLong("duration"))
				.setVid(obj.getString("vid"))
				.setWeblink(obj.getString("weblink"));
	}

	private void saveOwner(JSONObject obj) {
		JSONObject ownerJson = obj.getJSONObject("owner");
		Owner owner = new Owner()
				.setUid(ownerJson.getLong("mid"))
				.setName(ownerJson.getString("name"))
				.setFace(ownerJson.getString("face"));
		ownerManager.updateOrInsert(owner);
	}

	private void saveRight(JSONObject obj, Long av) {
		JSONObject rights = obj.getJSONObject("rights");
		Right right = new Right()
				.setAv(obj.getLong("aid"))
				.setBp(rights.getShort("bp"))
				.setElec(rights.getShort("elec"))
				.setDownload(rights.getShort("download"))
				.setMovie(rights.getShort("movie"))
				.setPay(rights.getShort("pay"))
				.setHd5(rights.getShort("hd5"))
				.setNoReprint(rights.getShort("no_reprint"))
				.setAutoplay(rights.getShort("autoplay"))
				.setUgcPay(rights.getShort("ugc_pay"))
				.setIsCooperation(rights.getShort("is_cooperation"))
				.setUgcPayPreview(rights.getShort("ugc_pay_preview"))
				.setNoBackground(rights.getShort("no_background"));
		rightManager.updateOrInsert(right);
	}

}
