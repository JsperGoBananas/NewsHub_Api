package com.jl.newshubapi.converter;

import com.jl.newshubapi.model.entity.Article;
import com.jl.newshubapi.utils.TimeUtil;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import org.jdom2.Attribute;
import org.jdom2.Element;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RssConverter{

    public List<Article> convertToArticleList(SyndFeed feed, LocalDateTime lastestUpdateTime,String source) {
        // 获取所有内容项
        List<Article> articles = new ArrayList<>();
        List<SyndEntry> entries = feed.getEntries();
        //如果entry的getPublishedDate为空，则将其设置为当前时间
        if(entries.get(0).getPublishedDate()==null){
            entries.forEach(entry -> entry.setPublishedDate(Date.from(TimeUtil.getCurrentUTCTime().toInstant(ZoneOffset.UTC)))
            );
        }
        if (lastestUpdateTime != null) {
            entries.removeIf(entry -> entry.getPublishedDate()!=null&&TimeUtil.convertTimestampToUTC(entry.getPublishedDate().getTime()).isBefore(lastestUpdateTime));
            entries.removeIf(entry -> entry.getPublishedDate()!=null&&TimeUtil.convertTimestampToUTC(entry.getPublishedDate().getTime()).isEqual(lastestUpdateTime));
        }
        for (SyndEntry entry : entries) {
            Article article = new Article();
            article.setTitle(entry.getTitle());
            article.setLink(entry.getLink());
            if(entry.getPublishedDate() != null){
                article.setUpdatedTime(TimeUtil.convertTimestampToUTC(entry.getPublishedDate().getTime()));
            }else{
                article.setUpdatedTime(TimeUtil.getCurrentUTCTime());
            }
            article.setCoverImage(getFirstImage(entry));
            //if description is not null and is less than 500 characters, set it as description, avoid main content showing in description tag
            if (entry.getDescription() != null && entry.getDescription().getValue().length() < 1000) {
                String descriptionWithoutImg = entry.getDescription().getValue().replaceAll("<img[^>]*>", "").replaceAll("<a[^>]*>.*?</a>", "");
                article.setDescription(descriptionWithoutImg);
            }
            if(article.getTitle() == null && article.getDescription() != null){
                article.setTitle(article.getDescription().substring(0, Math.min(article.getDescription().length(), 50)));
            }
            article.setSource(source);
            articles.add(article);
        }
        return articles;
    }

    private  String getFirstImage(SyndEntry entry) {
        // 优先检查 enclosures
        List<SyndEnclosure> enclosures = entry.getEnclosures();
        if (enclosures != null && !enclosures.isEmpty()) {
            for (SyndEnclosure enclosure : enclosures) {
                if (enclosure.getType().startsWith("image/")) {
                    return enclosure.getUrl();
                }
            }
        }

        // 递归查找 foreignMarkup 中的所有可能的图片 URL
        for (Object foreignMarkupObj : entry.getForeignMarkup()) {
            if (foreignMarkupObj instanceof Element) {
                Element foreignMarkup = (Element) foreignMarkupObj;
                String imageUrl = findImageUrlInElement(foreignMarkup);
                if (imageUrl != null) {
                    return imageUrl;
                }
            }
        }

        // 最后在内容或描述中查找图片
        String content = entry.getContents().stream()
                .map(c -> c.getValue())
                .findFirst()
                .orElse(entry.getDescription() != null ? entry.getDescription().getValue() : null);
        if (content != null) {
            String imageUrl = extractFirstImageUrl(content);
            if (imageUrl != null) {
                return imageUrl;
            }
        }

        return null;  // 没有找到图片
    }
    // 递归查找元素中的图片 URL
    private String findImageUrlInElement(Element element) {
        // 先检查当前元素是否包含图片 URL
        if ("content".equals(element.getName()) && "media".equals(element.getNamespacePrefix())) {
//            if (element.getAttributeValue("type")!=null && element.getAttributeValue("type").startsWith("image/")) {
//                return element.getAttributeValue("url");
//            }
            //遍历所有attributes找到startWith image/的url
            for (Attribute attribute : element.getAttributes()) {
                if (attribute.getValue().startsWith("image")) {
                    return element.getAttributeValue("url");
                }
            }
        }

        if ("thumbnail".equals(element.getName()) && "media".equals(element.getNamespacePrefix())) {
            return element.getAttributeValue("url");
        }

        // 递归检查子元素
        for (Element child : element.getChildren()) {
            String imageUrl = findImageUrlInElement(child);
            if (imageUrl != null) {
                return imageUrl;
            }
        }

        return null;
    }

    private  String extractFirstImageUrl(String content) {
        String imgRegex = "<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(imgRegex, java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
