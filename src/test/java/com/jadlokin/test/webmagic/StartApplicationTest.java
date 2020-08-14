package com.jadlokin.test.webmagic;

import com.jadlokin.test.webmagic.entity.VideoData;
import com.jadlokin.test.webmagic.entity.VideoInfo;
import com.jadlokin.test.webmagic.mapper.AvMapper;
import com.jadlokin.test.webmagic.mapper.VideoDataMapper;
import com.jadlokin.test.webmagic.mapper.VideoInfoMapper;
import com.jadlokin.test.webmagic.mapper.VideoMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = StartApplication.class)
@EnableAutoConfiguration
public class StartApplicationTest {
	@Autowired
	private AvMapper avMapper;
	@Autowired
	private VideoDataMapper videoDataMapper;
	@Autowired
	private VideoInfoMapper videoInfoMapper;
	@Autowired
	private VideoMapper videoMapper;

	@Test
	public void main() {
		List<Long> avList = avMapper.selectAllAv();
		log.info(avList.toString());
	}

	public void rmiConnector() {
	}

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


	public void checkBvRule() {
		List<VideoInfo> videoInfoList = videoInfoMapper.selectAll();
		String table = "fZodR9XQDSUm21yCkr6zBqiveYah8bt4xsWpHnJE7jL5VG3guMTKNPAwcF";
		Map<Character, Integer> map = IntStream.range(0, 58).boxed()
				.collect(Collectors.toMap(table::charAt, integer -> integer));
		List<Integer> indexs = Arrays.asList(11, 10, 3, 8, 4, 6, 2, 9, 5, 7);
		Collections.reverse(indexs);
		log.info("start");
		boolean ans = videoInfoList.stream()
				.allMatch(videoInfo -> {
					Long av = videoInfo.getAv();
					String bv = videoInfo.getBv();
					Long newAv = indexs.stream()
							.map(bv::charAt)
							.map(map::get)
							.map(Long::valueOf)
							.reduce(0L, (a, b) ->
									a * 58 + b
							);
					newAv -= 100618342136696320L;
					newAv ^= 177451812L;
					log.info("[av" + av + "][" + bv + "]compute ans is " + newAv);
					return av.equals(newAv);
				});
		log.warn("ans = " + ans);
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