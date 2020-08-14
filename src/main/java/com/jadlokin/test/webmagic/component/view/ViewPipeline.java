package com.jadlokin.test.webmagic.component.view;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jadlokin.test.webmagic.entity.*;
import com.jadlokin.test.webmagic.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Component
public class ViewPipeline implements Pipeline {

	private VideoInfoMapper videoInfoMapper;
	private VideoDataMapper videoDataMapper;
	private VideoPageMapper videoPageMapper;
	private OwnerMapper ownerMapper;
	private RightMapper rightMapper;

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
		long av = Long.valueOf(resultItems.get("av"));
		if (!rep.getInteger("code").equals(0)
				|| !rep.getJSONObject("data").containsKey("pages")) {
			VideoInfo videoInfo = new VideoInfo().setAv(av).setIsDelete(true);
			videoInfoMapper.updateByPrimaryKeySelective(videoInfo);
//			videoInfoMapper.deleteByPrimaryKey(av);
			return;
		}
		JSONObject data = rep.getJSONObject("data");
		VideoInfo videoInfo = newVideoInfo(data);
		VideoData videoData = newVideoData(data);
		Stream<VideoPage> videoPageStream = newVideoPageList(data);
		Owner owner = newOwner(data);
		Right right = newRight(data);

		if (videoInfoMapper.selectByPrimaryKey(av) == null) {
			videoInfoMapper.insertSelective(videoInfo);
		} else {
			videoInfoMapper.updateByPrimaryKeySelective(videoInfo);
		}

		if (videoDataMapper.selectByPrimaryKey(av, videoData.getIssue()) == null) {
			videoDataMapper.insertSelective(videoData);
		} else {
			videoDataMapper.updateByPrimaryKeySelective(videoData);
		}

		videoPageStream.forEach(videoPage -> {
			if (videoPageMapper.selectByPrimaryKey(videoPage.getCid(), videoPage.getAv()) == null) {
				videoPageMapper.insertSelective(videoPage);
			} else {
				videoPageMapper.updateByPrimaryKeySelective(videoPage);
			}
		});

		if (ownerMapper.selectByPrimaryKey(owner.getUid()) == null) {
			ownerMapper.insertSelective(owner);
		} else {
			ownerMapper.updateByPrimaryKeySelective(owner);
		}

		if (rightMapper.selectByPrimaryKey(av) == null) {
			rightMapper.insertSelective(right);
		} else {
			rightMapper.updateByPrimaryKeySelective(right);
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
				.setIssue((short) 11)
				.setDanmaku(stat.getLong("danmaku"))
				.setShare(stat.getLong("share"))
				.setLike(stat.getLong("like"))
				.setDislike(stat.getLong("dislike"))
				.setEvaluation(stat.getString("evaluation"));
	}

	private Stream<VideoPage> newVideoPageList(JSONObject obj) {
		JSONArray pages = obj.getJSONArray("pages");
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
