package com.jl.newshubapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jl.newshubapi.constants.enums.AppHttpCodeEnum;
import com.jl.newshubapi.model.entity.*;
import com.jl.newshubapi.mapper.AiSummaryMapper;
import com.jl.newshubapi.model.dtos.ResponseResult;
import com.jl.newshubapi.service.ArticleService;
import com.jl.newshubapi.service.IAiSummaryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jl.newshubapi.utils.RequestUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jasper
 * @since 2024-10-09
 */
@Service
public class AiSummaryServiceImpl extends ServiceImpl<AiSummaryMapper, AiSummary> implements IAiSummaryService {
    @Value("${gemini.url}")
    private String geminiUrl;

    @Value("${gemini.key}")
    private String geminiKey;

    @Autowired
    private ArticleService articleService;

    private final String prompt = "用中文总结并按类型和重要性生成一篇摘要 返回HTML格式: ";

    private final CloseableHttpClient client = RequestUtil.getHttpClient();
    @Override
    public ResponseResult getSummary(Integer id) {
        AiSummary summary = getOne(new QueryWrapper<AiSummary>().eq("source", id).orderByDesc("generated_time").last("limit 1"));
        if (summary != null) {
            return ResponseResult.okResult(summary);
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
    }


    @Override
    public String getAISummary(Integer source) {
        HttpPost httpPost = new HttpPost(geminiUrl);
        httpPost.setHeader("Content-Type", "application/json");
        //添加参数 key 为 geminiKey
        httpPost.setHeader("key", geminiKey);
        ContentData model = buildContent(source);

        if (model == null) {
            return null;
        }
        httpPost.setEntity(new StringEntity(JSON.toJSONString(model), "UTF-8"));
        try (CloseableHttpResponse response = client.execute(httpPost)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                byte[] responseBytes = EntityUtils.toByteArray(entity);
                String decodedResponse = new String(responseBytes, Charset.forName("UTF-8"));
                String result = JSON.parseObject(decodedResponse).getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");
                return result;

            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Failed to send POST request to " + geminiUrl, e);
            throw new RuntimeException("Failed to send POST request to " + geminiUrl);
        }
    }


    private ContentData buildContent(Integer source){
        ContentData contentData = new ContentData();

        // 创建 ContentPart 实例
        ContentData.ContentPart contentPart = new ContentData.ContentPart();

        // 创建 Part 实例，并设置内容
        ContentData.Part part = new ContentData.Part();
        List<Article> last12HoursArticles = articleService.getLast12HoursArticles(source);
        List<String> collect = last12HoursArticles.stream().map(Article::getDescription).filter(desc->desc!=null).collect(Collectors.toList());
        System.out.println("数据来源:"+source+" "+collect);
        // if collect is empty, return null
        if (collect.isEmpty() || collect.size() == 0){
            return null;
        }
        part.setText(prompt+collect);

        // 将 Part 实例加入到 ContentPart 的 parts 列表
        List<ContentData.Part> parts = new ArrayList<>();
        parts.add(part);
        contentPart.setParts(parts);

        // 将 ContentPart 实例加入到 ContentData 的 contents 列表
        List<ContentData.ContentPart> contents = new ArrayList<>();
        contents.add(contentPart);
        contentData.setContents(contents);
        return contentData;
    }
}
