package com.pj.webmagic;
import com.pj.webmagic.commons.Producer;
import com.pj.webmagic.service.Receiver;
import com.pj.webmagic.util.ReturnResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.Set;

@SpringBootApplication
@Controller
@EnableAsync
public class WebmagicApplication {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private Producer producer;
    @Autowired
    private Receiver receiver;

    public static void main(String[] args) {
        SpringApplication.run(WebmagicApplication.class, args);
    }
    @PostConstruct
    private void init(){
        receiver.run();
    }

    @RequestMapping("/")
    public String index(Model model) {
        Set<Object> pictures = redisTemplate.opsForZSet().range("pictures", 0, 40);
        model.addAttribute("pictures",pictures);
        return "index";
    }

    @GetMapping("getPictures/{number}")
    @ResponseBody
    public ReturnResult gtePictures(@PathVariable int number) {
        Object pictures = redisTemplate.opsForZSet().range("pictures", number - 5, number);
        return ReturnResult.ok(pictures);
    }
    @GetMapping("sendMessage/{value}")
    @ResponseBody
    public void sendMessage(@PathVariable String value) {
        producer.sendMessage(value);
    }

}
