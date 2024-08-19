package com.jl.newshubapi.utils;

import com.jl.newshubapi.model.entity.Website;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.extern.slf4j.Slf4j;

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
        website.setIconUrl(getBaseUrl(feed.getLink())+"/favicon.ico");
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
}
