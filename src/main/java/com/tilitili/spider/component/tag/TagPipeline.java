package com.tilitili.spider.component.tag;

import com.tilitili.common.emnus.TaskStatus;
import com.tilitili.common.entity.Tag;
import com.tilitili.common.entity.VideoTagRelation;
import com.tilitili.common.manager.TagManager;
import com.tilitili.common.manager.VideoTagRelationManager;
import com.tilitili.common.mapper.TaskMapper;
import com.tilitili.spider.util.Log;
import com.tilitili.spider.view.BaseView;
import com.tilitili.spider.view.tag.TagView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class TagPipeline implements Pipeline {

	private final TagManager tagManager;
	private final TaskMapper taskMapper;
	private final VideoTagRelationManager videoTagRelationManager;

	@Autowired
	public TagPipeline(TagManager tagManager, TaskMapper taskMapper, VideoTagRelationManager videoTagRelationManager) {
		this.tagManager = tagManager;
		this.taskMapper = taskMapper;
		this.videoTagRelationManager = videoTagRelationManager;
	}

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
			.withZone(ZoneId.systemDefault());

	@Override
	public void process(ResultItems resultItems, Task task) {
		Long av = resultItems.get("av");
		Long taskId = resultItems.get("taskId");
		BaseView<List<TagView>> data = resultItems.get("data");
		if (data.code != 0) {
			Log.error("接口返回状态不为0: %s", data);
			taskMapper.updateStatusAndRemarkById(taskId, TaskStatus.SPIDER.getValue(), TaskStatus.FAIL.getValue(), data.message);
			return;
		}
		try {
			for (TagView tagView : data.data) {
				Tag tag = new Tag().setId(tagView.tag_id)
						.setName(tagView.tag_name)
						.setCover(tagView.cover)
						.setHeadCover(tagView.head_cover)
						.setContent(tagView.content)
						.setShortContent(tagView.short_content)
						.setExternalType(tagView.type)
						.setState(tagView.state)
						.setExternalCreateTime(new Date(Instant.ofEpochSecond(tagView.ctime).toEpochMilli()))
						.setIsAtten(tagView.is_atten)
						.setLikes(tagView.likes)
						.setHates(tagView.hates)
						.setAttribute(tagView.attribute)
						.setLiked(tagView.liked)
						.setHated(tagView.hated)
						.setExtraAttr(tagView.extra_attr)
						.setTagType(tagView.tag_type)
						.setIsActivity(tagView.is_activity)
						.setColor(tagView.color)
						.setAlpha(tagView.alpha)
						.setIsSeason(tagView.is_season)
						.setSubscribedCount(tagView.subscribed_count)
						.setArchiveCount(tagView.archive_count)
						.setFeaturedCount(tagView.featured_count);

				tagManager.updateOrInsert(tag);

				VideoTagRelation videoTagRelation = new VideoTagRelation().setAv(av).setTagId(tagView.tag_id);
				videoTagRelationManager.updateOrInsert(videoTagRelation);
			}
			taskMapper.updateStatusById(taskId, TaskStatus.SPIDER.getValue(), TaskStatus.SUCCESS.getValue());
		} catch (Exception e) {
			log.error("持久化失败, av=" + av, e);
			taskMapper.updateStatusAndRemarkById(taskId, TaskStatus.SPIDER.getValue(), TaskStatus.FAIL.getValue(), e.getMessage());
		}
	}
}
