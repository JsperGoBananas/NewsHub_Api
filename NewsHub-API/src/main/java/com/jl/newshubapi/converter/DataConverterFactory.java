package com.jl.newshubapi.converter;

import com.jl.newshubapi.constants.enums.DataSource;

//根据不同的数据源返回不同的数据转换器
public class DataConverterFactory {


    public static DataConverter getConverter(Integer source) {
        if (DataSource.WIKIPEDIA_MOST_READ_EN.getId().equals(source)) {
            return new WikipediaMostreadConverter();
        }  else if (DataSource.WIKIPEDIA_HISTORY_EN.getId().equals(source)) {
            return new WikipediaHistoryConverter();
        }  else if (DataSource.BBC_NEWS.getId().equals(source)) {
            return new BBCConverter();
        } else if (DataSource.NY_TIMES.getId().equals(source)  ) {
            return new NYTimesConverter();
        } else if (DataSource.CCTV_NEWS.getId().equals(source)) {
            return new CCTVConverter();
        } else if (DataSource.SSPAI.getId().equals(source)) {
            return new SSPaiConverter();
        } else if (DataSource.ZHIHU.getId().equals(source)) {
            return new ZhihuConverter();
        } else if (DataSource.ZHIHU_DAILY.getId().equals(source)) {
            return new ZhihuDailyConverter();
        } else if (DataSource.THIRTY_SIX_KR.getId().equals(source)) {
            return new Kr36Converter();
        } else if(DataSource.GITHUB.getId().equals(source)){
            return new GithubConverter();
        } else if(DataSource.CTO51.getId().equals(source)){
            return new Cto51Converter();
        } else if(DataSource.TENCENT_NEWS.getId().equals(source)){
            return new TencentConverter();
        }else if (DataSource.TOUTIAO_NEWS.getId().equals(source)){
            return new ToutiaoConverter();
        }
        return null;
    }
}
