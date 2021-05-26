package com.tilitili.spider.util;

import com.tilitili.common.manager.MiraiManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QQUtil {
    private final MiraiManager miraiManager;

    @Value("${spider.wait-time}")
    private Integer waitTime;

    @Autowired
    public QQUtil(MiraiManager miraiManager) {
        this.miraiManager = miraiManager;
    }

    public void sendRiskError(Class<?> clazz) {
        miraiManager.sendGroupMessage("Plain", String.format("爬虫[%s]被风控，预计[%s]分钟后恢复", clazz.getName(),waitTime));
    }

}
