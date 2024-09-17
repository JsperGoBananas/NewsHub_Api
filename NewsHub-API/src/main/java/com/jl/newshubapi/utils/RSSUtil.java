package com.jl.newshubapi.utils;

import com.jl.newshubapi.model.entity.Website;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
public class RSSUtil {

    public static SyndFeed getFeed(String url){

        try {
            URL feedUrl = null;
            feedUrl = new URL(url);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));
            return feed;
        } catch (MalformedURLException e) {
            log.error("URL格式错误", e);
            return null ;
        }catch (FeedException e) {
            log.error("解析Feed错误", e);
            return null;
        } catch (IOException e) {
            log.error("IO错误", e);
            return null;
        }

    }

    public static Website getWebsiteInfo(SyndFeed feed){
        Website website = new Website();
        website.setTitle(feed.getTitle());
        website.setHomepage(feed.getLink());
        if (feed.getDescription() != null && feed.getDescription().length()<20) {
            website.setCategoryName(feed.getDescription());
        }
        website.setIconUrl(RSSUtil.getBestIconUrl(getBaseUrl(feed.getLink())));
        return website;
    }

    public static String getBaseUrl(String link){
        try {
            URL url = new URL(link);
            String baseUrl = url.getProtocol() + "://" + url.getHost();
            return baseUrl;
        } catch (MalformedURLException e) {
            log.error("URL格式错误", e);
            return null;
        }
    }

    /**
     * 获取网站的最高清图标链接。
     *
     * @param url 要查询的网站 URL
     * @return 最高清的图标链接
     * @throws IOException 如果出现网络问题或无法解析 HTML 内容
     */
    public static String getBestIconUrl(String url)  {
        // 获取网站主页的 HTML 内容
        Document doc = null;
        try {
            //加上User-Agent，否则有些网站会拒绝访问
//            doc = Jsoup.connect(url).get();
            doc = Jsoup.connect(url).userAgent("Mozilla").get();
        } catch (IOException e) {
            log.error("无法获取"+url+"主页", e);
            return url+"/favicon.ico";
        }

        // 查找图标链接
        Elements iconLinks = doc.select("link[rel~=(icon|shortcut icon)]");

        String bestIconUrl = null;
        int bestSize = 0;

        // 遍历所有找到的图标链接
        for (Element link : iconLinks) {
            log.info("找到图标链接：" + link);
            String href = link.attr("href");
            String sizes = link.attr("sizes");

            // 解析尺寸
            int size = parseSize(sizes);

            // 选择尺寸最大的图标
            if (size >= bestSize) {
                bestSize = size;
                bestIconUrl = href;
            }
        }

        // 如果找不到图标，则使用默认的 /favicon.ico 路径
        if (bestIconUrl == null) {
            log.info("无法找到图标链接，使用默认的 /favicon.ico 路径");
            bestIconUrl = url + "/favicon.ico";
        } else {
            if (bestIconUrl.startsWith("//")) {
                bestIconUrl = "https:" + bestIconUrl;
            }
            // 如果图标链接是相对路径，将其转换为绝对路径
            else if (!bestIconUrl.startsWith("http") ) {
                bestIconUrl = url + bestIconUrl;
            }
        }
        log.info("找到最佳图标链接：" + bestIconUrl);
        return bestIconUrl;
    }

    /**
     * 解析图标的尺寸。
     *
     * @param sizes 尺寸字符串
     * @return 尺寸的面积（宽度 * 高度）
     */
    private static int parseSize(String sizes) {
        if (sizes == null || sizes.isEmpty()) {
            return 0;
        }
        String[] parts = sizes.split(" ");
        for (String part : parts) {
            String[] sizeParts = part.split("x");
            if (sizeParts.length == 2) {
                try {
                    int width = Integer.parseInt(sizeParts[0]);
                    int height = Integer.parseInt(sizeParts[1]);
                    return width * height;
                } catch (NumberFormatException e) {
                    // 如果无法解析尺寸，则跳过
                }
            }
        }
        return 0;
    }
}
