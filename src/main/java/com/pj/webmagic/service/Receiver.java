package com.pj.webmagic.service;

import com.pj.webmagic.commons.PictureProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ResultItemsCollectorPipeline;

import java.util.List;

@Component
@SuppressWarnings("all")
public class Receiver {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private Spider spider;

    public void receiveMessage(String message) {
        if (message.equals("\"start\"")) {
            run();
        } else if (message.equals("\"stop\"")) {
            stop();
        } else if (message.equals("\"delete\"")) {
            delete();
        }
    }

    public void run() {
        ZSetOperations ops = redisTemplate.opsForZSet();
        if (ops.size("pictures") < 1) {
            ResultItemsCollectorPipeline pipeline = new ResultItemsCollectorPipeline();
            spider = Spider.create(new PictureProcessor());
            spider.addUrl("https://www.meizitu.com/")
                    .addPipeline(pipeline)
                    .thread(120)
                    .run();
            pipeline.getCollected().forEach(e -> {
                for (Object picture : (List) e.get("pictures")) {
                    ops.add("pictures", picture, -1);
                }
            });
        }
    }

    public void stop() {
        this.spider.stop();
    }

    public void delete() {
        redisTemplate.opsForZSet().removeRange("pictures", 0, 133);
    }

}