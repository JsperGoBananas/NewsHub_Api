package com.jl.newshubapi.aspect;

import com.jl.newshubapi.annotation.RateLimit;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class RateLimitAspect {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private HttpServletRequest request;

    @Pointcut("@annotation(rateLimit)")
    public void rateLimitPointcut(RateLimit rateLimit) {}

    @Before("rateLimitPointcut(rateLimit)")
    public void checkRateLimit(JoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        //记录方法名
        String methodName = joinPoint.getSignature().getName();
        //获取参数
        Object[] args = joinPoint.getArgs();
        //获取参数数量
        int argCount = args.length;
        //把参数拼接成[参数1,参数2,参数3]的形式
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < argCount; i++) {
            sb.append(args[i]);
            if (i < argCount - 1) {
                sb.append(",");
            }
        }
        sb.append("]");

        log.info("RateLimitAspect: {}", methodName);
        String ip = getClientIP();
        log.info("Request IP: {}", ip);
        if(args == null){

        }
        //key = rate:limit:ip:methodName:args
        String key = "rate:limit:" + ip+":"+methodName + ":" + sb;
        long currentTimeMillis = System.currentTimeMillis();

        // 从注解中获取限流参数
        int requests = rateLimit.requests();
        int windowSeconds = rateLimit.windowSeconds();
        long windowMillis = windowSeconds * 1000L;


        // 移除过期的请求记录
        long windowStart = currentTimeMillis - windowMillis;
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);

        // 计算当前时间窗口内的请求数
        Long requestCount = redisTemplate.opsForZSet().zCard(key);

        // 设置过期时间
        redisTemplate.expire(key, windowSeconds, TimeUnit.SECONDS);

        if (requestCount != null && requestCount+1 > requests) {
            throw new RuntimeException("Rate limit exceeded");
        }else{
            // 添加当前时间戳
            redisTemplate.opsForZSet().add(key, String.valueOf(currentTimeMillis), currentTimeMillis);
        }
    }

    private String getClientIP() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
