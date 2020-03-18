package com.jadlokin.test.webmagic;

import com.jadlokin.test.webmagic.entity.VideoData;
import com.jadlokin.test.webmagic.mapper.VideoDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = StartApplication.class)
@EnableAutoConfiguration
public class StartApplicationTest {

	@Autowired
	private VideoDataMapper videoDataMapper;

	private Long getPoint(VideoData newData, VideoData oldData) {
		VideoData data = new VideoData()
				.setCoin(newData.getCoin() - oldData.getCoin())
				.setFavorite(newData.getFavorite() - oldData.getFavorite())
				.setPage(newData.getPage())
				.setReply(newData.getReply() - oldData.getReply())
				.setView(newData.getView() - oldData.getView());
		double v = data.getView() * 0.1 / (data.getPage() - 1);
		double a = (v + data.getFavorite()) /
				(v + data.getFavorite() + data.getReply() * 10) * 10;
		double b = Math.min(((data.getFavorite() + data.getCoin()) / v) * 0.75, 2);
		return Math.round((v + data.getReply() * a
				+ (data.getFavorite() + data.getCoin()) * 0.5) * b);
	}

	private Long getPoint(VideoData newData) {
		return getPoint(newData, new VideoData()
				.setCoin(0L)
				.setFavorite(0L)
				.setPage(0L).setReply(0L)
				.setView(0L));
	}
	@Test
	public void main() {
//		List<VideoData> videoDataList = videoDataMapper.selectAllDisparityBetweenTwoIssue((short) 1);
//		videoDataList.stream()
//				.map(videoData -> new VideoData()
//						.setAv(videoData.getAv())
//						.setIssue(videoData.getIssue())
//						.setPoint(getPoint(videoData)))
//				.forEach(videoDataMapper::updateByPrimaryKeySelective);
	}

	public List<VideoData> updatePoint() {
		List<VideoData> oldVideoDataList = videoDataMapper.selectAll((short) 1);
		log.info("video data get");
		oldVideoDataList.sort((a, b) -> (int) (a.getAv() - b.getAv()));
		log.info("video data sorted");
		List<VideoData> newVideoDataList = videoDataMapper.selectAll((short) 0);
		log.info("video data get");
		newVideoDataList.sort((a, b) -> (int) (a.getAv() - b.getAv()));
		log.info("video data sorted");
		int i = 0,j = 0;
		while (i < newVideoDataList.size()) {
			VideoData newData = newVideoDataList.get(i);
			if (j >= oldVideoDataList.size()) {
//				log.info("[" + i +"][" + j + "]found new video av" + newData.getAv());
				newData.setPoint(getPoint(newData));
				i++;
				continue;
			}
			VideoData oldData = oldVideoDataList.get(j);
			if(newData.getAv().equals(oldData.getAv())) {
				newData.setPoint(getPoint(newData, oldData));
				i++;
				j++;
				continue;
			}
			j++;
//			int k = j;
//			while(++k < oldVideoDataList.size()) {
//				oldData = oldVideoDataList.get(k);
//				if (newData.getAv().equals(oldData.getAv())) {
//					log.warn("[" + i +"][" + j + "]jump j to av" + newData.getAv());
//					j = k;
//					newData.setPoint(getPoint(newData, oldData));
//				}
//			}
			log.warn("[" + i +"][" + j + "]not found old video av" + newData.getAv());
//			newData.setPoint(getPoint(newData));
		}
		if(!newVideoDataList.get(100000).getPoint().equals(
				getPoint(newVideoDataList.get(100000), oldVideoDataList.get(100000))
		)){
			log.error("数据校验出错");
			log.error("[" + newVideoDataList.get(100000).getPoint() + "][" + getPoint(newVideoDataList.get(100000), oldVideoDataList.get(100000)) + "]");
			log.error("[" + newVideoDataList.get(100000) + "][" + oldVideoDataList.get(100000) + "]");
			return null;
		}
		return newVideoDataList;
	}

}