package com.tilitili.spider.component.owner;

import com.tilitili.common.emnus.TaskStatus;
import com.tilitili.common.entity.Owner;
import com.tilitili.common.manager.OwnerManager;
import com.tilitili.common.mapper.OwnerMapper;
import com.tilitili.common.mapper.TaskMapper;
import com.tilitili.common.utils.Log;
import com.tilitili.common.entity.view.BaseView;
import com.tilitili.common.entity.view.owner.OwnerView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

@Slf4j
@Component
public class OwnerPipeline implements Pipeline {
	private final OwnerMapper ownerMapper;
	private final OwnerManager ownerManager;
	private final TaskMapper taskMapper;

	@Autowired
	public OwnerPipeline(OwnerMapper ownerMapper, OwnerManager ownerManager, TaskMapper taskMapper) {
		this.ownerMapper = ownerMapper;
		this.ownerManager = ownerManager;
		this.taskMapper = taskMapper;
	}

	@Override
	public void process(ResultItems resultItems, Task task) {
		Long uid = resultItems.get("uid");
		Long taskId = resultItems.get("taskId");
		BaseView<OwnerView> data = resultItems.get("data");
		if (data.code != 0) {
			Log.error("接口返回状态不为0: %s", data);
			ownerMapper.update(new Owner().setUid(uid).setRemark(data.message));
			taskMapper.updateStatusAndRemarkById(taskId, TaskStatus.SPIDER.getValue(), TaskStatus.FAIL.getValue(), data.message);
			return;
		}
		try {
			OwnerView ownerView = data.data;
			Owner owner = new Owner().setUid(ownerView.mid).setName(ownerView.name).setFace(ownerView.face).setSex(ownerView.sex).setSign(ownerView.sign).setRank(ownerView.rank).setLevel(ownerView.level).setJointime(ownerView.jointime).setMoral(ownerView.moral).setSilence(ownerView.silence).setBirthday(ownerView.birthday).setCoins(ownerView.coins).setFansBadge(ownerView.fans_badge).setOfficialRole(ownerView.official.role).setOfficialTitle(ownerView.official.title).setOfficialDesc(ownerView.official.desc).setOfficialType(ownerView.official.type).setVipType(ownerView.vip.type).setVipStatus(ownerView.vip.status).setVipThemeType(ownerView.vip.theme_type).setVipLabelPath(ownerView.vip.label.path).setVipLabelText(ownerView.vip.label.text).setVipLabelTheme(ownerView.vip.label.label_theme).setVipAvatarSubscript(ownerView.vip.avatar_subscript).setVipNicknameColor(ownerView.vip.nickname_color).setPendantPid(ownerView.pendant.pid).setPendantName(ownerView.pendant.name).setPendantImage(ownerView.pendant.image).setPendantExpire(ownerView.pendant.expire).setPendantImageEnhance(ownerView.pendant.image_enhance).setPendantImageEnhanceFrame(ownerView.pendant.image_enhance_frame).setNameplateNid(ownerView.nameplate.nid).setNameplateName(ownerView.nameplate.name).setNameplateImage(ownerView.nameplate.image).setNameplateImageSmall(ownerView.nameplate.image_small).setNameplateLevel(ownerView.nameplate.level).setNameplateCondition(ownerView.nameplate.condition).setTopPhoto(ownerView.top_photo).setRoomId(ownerView.live_room.roomid).setRoomUrl(ownerView.live_room.url);

			ownerManager.updateOrInsert(owner);

			taskMapper.updateStatusById(taskId, TaskStatus.SPIDER.getValue(), TaskStatus.SUCCESS.getValue());
		} catch (Exception e) {
			log.error("持久化失败, uid=" + uid, e);
			taskMapper.updateStatusAndRemarkById(taskId, TaskStatus.SPIDER.getValue(), TaskStatus.FAIL.getValue(), e.getMessage());
		}
	}
}
