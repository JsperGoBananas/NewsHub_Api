package com.jl.newshubapi.config;

import jakarta.annotation.Resource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;

public class MybatisConfig {
    public class CommonDao extends SqlSessionDaoSupport {
        @Resource
        public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory){
            super.setSqlSessionFactory(sqlSessionFactory);
        }
    }
}
