package com.pj.webmagic.commons;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.ResultItemsCollectorPipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class PictureProcessor implements PageProcessor {
    private String url;
    private String resultKey;
    private JSONArray patterns;
    private static List<String> alls=new ArrayList<>();
    private static String[] typeUrls = new String[]{/*"https://www.meizitu.com/a/pure.html", */"https://www.meizitu.com/a/cute.html"/*, "https://www.meizitu.com/a/sexy.html", "https://www.meizitu.com/a/fuli.html", "https://www.meizitu.com/a/legs.html", "https://www.meizitu.com/a/rixi.html", "https://www.meizitu.com/a/qingchun.html", "https://www.meizitu.com/a/yundong.html", "https://www.meizitu.com/a/qingchun.html", "https://www.meizitu.com/a/sifang.html", "https://www.meizitu.com/tag/mote_6_1.html", "https://www.meizitu.com/tag/keai_64_1.html", "https://www.meizitu.com/a/bijini.html", "https://www.meizitu.com/tag/qizhi_53_1.html", "https://www.meizitu.com/tag/banluo_5_1.html", "https://www.meizitu.com/tag/nvshen_460_1.html", "https://www.meizitu.com/tag/quanluo_4_1.html", "https://www.meizitu.com/tag/meitun_42_1.html", "https://www.meizitu.com/tag/chengshu_487_1.html"*/};

    @Override
    public void process(Page page) {
        /*Html html = page.getHtml();
        List<String> firstPage = html.xpath("//div[@class='picnew clearfix']/ul/li | //div[@class='hotpic clearfix']/ul/li").css("a", "href").all();
        page.addTargetRequests(firstPage);
        html = page.getHtml();
        List<String> imgUrls = html.css("#picture").css("img", "src").all();*/

        //获取末页码，拿到页数
        String newUrl = "";
        Html html = page.getHtml();
        String pageData = html.css("#wp_page_numbers a").regex(".*末页.*").get();
        String pageSize = Pattern.compile("[^0-9]").matcher(pageData).replaceAll("").trim();
        for(int i=1;i<=Integer.valueOf(pageSize);i++){
            String url = page.getUrl().toString();
            //拼接页码
            if(url.contains("_")){
                newUrl = url.substring(0,url.indexOf("_")) + "_" + i + ".html";
            }else{
                newUrl = url.replace(".html", "_" + i + ".html");
            }
            //设置为目标页
            page.addTargetRequest(newUrl);
            //开始爬目标页上的页面连接
            List<String> all = page.getHtml().xpath("//div[@class='pic']").css("a", "href").all();
            page.putField(/*url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("."))*/"cute",all);
        }
    }

    public static void main(String[] args) {
        ResultItemsCollectorPipeline pipeline = new ResultItemsCollectorPipeline();
        Spider.create(new PictureProcessor()).addUrl(typeUrls)
                .addPipeline(pipeline)
                .thread(120)
                .run();
        List<ResultItems> collected = pipeline.getCollected();
        System.err.println(collected.get(0).getAll());
    }

    @Override
    public Site getSite() {
        return Site
                .me()
                .setDomain("https://www.meizitu.com/")
                .setSleepTime(2000)
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
    }
}
