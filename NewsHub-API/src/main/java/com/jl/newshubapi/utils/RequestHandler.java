package com.jl.newshubapi.utils;


import com.jl.newshubapi.model.requests.Request;
import com.jl.newshubapi.model.entity.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;


@Slf4j
@Component
public class RequestHandler {

    public static final int REFRESH_TIME = 30;
    @Autowired
    StringRedisTemplate redisTemplate;


    private static final CloseableHttpClient client = HttpClientBuilder.create().build();

    public static String get(String url) {
        try {
            HttpGet httpGet = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    byte[] responseBytes = EntityUtils.toByteArray(entity);
                    String decodedResponse = new String(responseBytes, Charset.forName("UTF-8"));
                    return decodedResponse;
                }else{
                    return null;
                }
            }
        } catch (Exception e) {
            log.error("Failed to send GET request to " + url, e);
            throw new RuntimeException("Failed to send GET request to " + url, e);
        }
    }


    public static String post(Request request) {

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 创建HttpPost对象，并设置目标URL
            HttpPost httpPost = new HttpPost(request.getUrl());

            // 设置请求头（可选）
            for (Tuple<String, String> pair : request.getHeaders()) {
                httpPost.setHeader(pair.getFirst(), pair.getSecond());
            }

            httpPost.setEntity(new StringEntity(request.getBody()));

            // 发送请求并获取响应
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                // 获取响应状态码
                int statusCode = response.getStatusLine().getStatusCode();
                // 获取响应体
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    String responseString = EntityUtils.toString(responseEntity);
                    return responseString;
                }else{
                    return null;
                }
            }
        } catch (Exception e) {
            log.error("Failed to send POST request to " + request.getUrl(), e);
            throw new RuntimeException("Failed to send POST request to " + request.getUrl());
        }
    }

}








