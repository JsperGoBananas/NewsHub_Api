package com.jl.newshubapi.annotation;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimit {



    int requests() default 5; // 默认每分钟最多 100 次请求

    int windowSeconds() default 60; // 时间窗口，单位为秒
}
