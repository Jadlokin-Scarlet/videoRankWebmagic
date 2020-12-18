package com.tilitili.spider.util;

public class BilibiliApi {
    public static String getTagForVideoByAv(Long av, Long batchId) {
        return String.format("https://api.bilibili.com/x/web-interface/view/detail/tag?aid=%s&_id_=%s", av, batchId);
    }

    public static String getVideoForTagById(Integer pageNo, Long tagId, Long batchId) {
        return String.format("http://api.bilibili.com/x/tag/detail?ps=50&pn=%s&tag_id=%s&_id_=%s", pageNo, tagId, batchId);
    }

    public static String getTagInfoByName(String tagName, Long batchId) {
        return String.format("https://api.bilibili.com/x/tag/info?tag_name=%s&_id_=%s", tagName, batchId);
    }
}
