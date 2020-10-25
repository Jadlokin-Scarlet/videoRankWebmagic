package com.tilitili.spider.component.view;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tilitili.common.entity.*;
import com.tilitili.common.mapper.VideoDataMapper;
import com.tilitili.common.mapper.VideoInfoMapper;
import com.tilitili.common.mapper.RightMapper;
import com.tilitili.common.mapper.VideoPageMapper;
import com.tilitili.common.mapper.OwnerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Component
public class ViewPipeline implements Pipeline {

	private final VideoInfoMapper videoInfoMapper;
	private final VideoDataMapper videoDataMapper;
	private final VideoPageMapper videoPageMapper;
	private final OwnerMapper ownerMapper;
	private final RightMapper rightMapper;

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
			.withZone(ZoneId.systemDefault());

	@Autowired
	public ViewPipeline(VideoPageMapper videoPageMapper, VideoInfoMapper videoInfoMapper, VideoDataMapper videoDataMapper, OwnerMapper ownerMapper, RightMapper rightMapper) {
		this.videoPageMapper = videoPageMapper;
		this.videoInfoMapper = videoInfoMapper;
		this.videoDataMapper = videoDataMapper;
		this.ownerMapper = ownerMapper;
		this.rightMapper = rightMapper;
	}

	@Override
	public void process(ResultItems resultItems, Task task) {
		JSONObject rep = resultItems.get("rep");
		if (rep == null) {
			return;
		}
		long av = Long.parseLong(resultItems.get("av"));
		if (!rep.getInteger("code").equals(0)) {
			log.error(rep.toString());
			VideoInfo videoInfo = new VideoInfo().setAv(av).setIsDelete(true);
			videoInfoMapper.update(videoInfo);
			return;
		}
		JSONObject data = rep.getJSONObject("data");

		VideoInfo videoInfo = newVideoInfo(data);
		if (videoInfoMapper.getByAv(av) == null) {
			videoInfoMapper.insert(videoInfo);
		} else {
			videoInfoMapper.update(videoInfo);
		}

		VideoData videoData = newVideoData(data);
		if (videoDataMapper.getByAvAndIssue(av, videoData.getIssue()) == null) {
			videoDataMapper.insert(videoData);
		} else {
			videoDataMapper.update(videoData);
		}

		if (! rep.getJSONObject("data").containsKey("pages")) {
			Stream<VideoPage> videoPageStream = newVideoPageList(data);
			videoPageStream.forEach(videoPage -> {
				if (videoPageMapper.getByCidAndAv(videoPage.getCid(), videoPage.getAv()) == null) {
					videoPageMapper.insert(videoPage);
				} else {
					videoPageMapper.update(videoPage);
				}
			});
		}

		Owner owner = newOwner(data);
		if (ownerMapper.getByUid(owner.getUid()) == null) {
			ownerMapper.insert(owner);
		} else {
			ownerMapper.update(owner);
		}

		Right right = newRight(data);
		if (rightMapper.getByAv(av) == null) {
			rightMapper.insert(right);
		} else {
			rightMapper.update(right);
		}
	}

	private VideoInfo newVideoInfo(JSONObject obj) {
		return new VideoInfo()
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
	}

	private VideoData newVideoData(JSONObject obj) {
		JSONObject stat = obj.getJSONObject("stat");
		return new VideoData()
				.setAv(obj.getLong("aid"))
				.setIssue(videoDataMapper.getNewIssue())
				.setDanmaku(stat.getInteger("danmaku"))
				.setShare(stat.getInteger("share"))
				.setLike(stat.getInteger("like"))
				.setDislike(stat.getInteger("dislike"))
				.setEvaluation(stat.getString("evaluation"));
	}

	private Stream<VideoPage> newVideoPageList(JSONObject obj) {
		JSONArray pages = obj.getJSONArray("pages");
		if (!obj.containsKey("pages")) {
			log.warn("该视频无page信息");
			return Stream.empty();
		}
		return IntStream.range(0, obj.getJSONArray("pages").size())
				.mapToObj(pages::getJSONObject)
				.map(this::newVideoPage)
				.map(videoPage -> videoPage.setAv(obj.getLong("aid")));
	}

	private VideoPage newVideoPage(JSONObject obj) {
		return new VideoPage()
//				.setAv(obj.getLong("aid"))
				.setCid(obj.getLong("cid"))
				.setPage(obj.getInteger("page"))
				.setFrom(obj.getString("from"))
				.setPart(obj.getString("part"))
				.setDuration(obj.getLong("duration"))
				.setVid(obj.getString("vid"))
				.setWeblink(obj.getString("weblink"));
	}

	private Owner newOwner(JSONObject obj) {
		JSONObject owner = obj.getJSONObject("owner");
		return new Owner()
				.setUid(owner.getLong("mid"))
				.setName(owner.getString("name"))
				.setFace(owner.getString("face"));
	}

	private Right newRight(JSONObject obj) {
		JSONObject rights = obj.getJSONObject("rights");
		return new Right()
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
	}

}
