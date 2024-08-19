package com.jl.newshubapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jl.newshubapi.model.entity.Website;
import com.jl.newshubapi.mapper.WebsiteMapper;
import com.jl.newshubapi.model.dtos.ResponseResult;
import com.jl.newshubapi.service.ArticleService;
import com.jl.newshubapi.service.IWebsiteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jl.newshubapi.utils.RSSUtil;
import com.rometools.rome.feed.synd.SyndFeed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import static com.jl.newshubapi.constants.enums.AppHttpCodeEnum.WEBSITE_EXISTED;

/**
 * <p>
 * 网站表，维护目前所有网站 服务实现类
 * </p>
 *
 * @author Jasper
 * @since 2024-08-04
 */
@Service
public class WebsiteServiceImpl extends ServiceImpl<WebsiteMapper, Website> implements IWebsiteService {

    @Autowired
    @Lazy
    private ArticleService  articleService;

    //获取数据库中所有website列表包装成json返回
    @Override
    public ResponseResult getWebsiteList() {
        return ResponseResult.okResult(baseMapper.selectList(new QueryWrapper<Website>().eq("is_show", true)));
    }

    @Override
    public ResponseResult addWebsite(String fetchDataUrl) {


        SyndFeed feed = RSSUtil.getFeed(fetchDataUrl);
        if(feed == null){
            return ResponseResult.errorResult(4004, "无法从该URL获取数据");
        }
        Website website = RSSUtil.getWebsiteInfo(feed);
        website.setFetchDataUrl(fetchDataUrl);
        Website existedWebsite = getOne(new QueryWrapper<Website>().eq("fetch_data_url", website.getFetchDataUrl()));
        if (existedWebsite != null) {
            if (existedWebsite.getIsShow()) {
                return ResponseResult.errorResult(WEBSITE_EXISTED);
            }else{
                existedWebsite.setIsShow(true);
                baseMapper.updateById(existedWebsite);
                return ResponseResult.okResult(existedWebsite);
            }
        }
        website.setIsShow(true);
        website.setTitle(feed.getTitle());
        website.setIsRss(true);
        baseMapper.insert(website);
        articleService.saveOnlineArticle(fetchDataUrl);
        return ResponseResult.okResult(website);
    }

    @Override
    public ResponseResult removeWebsite(Integer id) {
        baseMapper.update(new UpdateWrapper<Website>().eq("id", id).set("is_show",false) );
        return ResponseResult.okResult("取消订阅成功");
    }
}
