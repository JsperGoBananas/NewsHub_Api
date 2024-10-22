package com.jl.newshubapi;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;

import java.nio.file.Paths;

public class MybatisPlusCodeGenerator {

    public static void main(String[] args) {
//        // 全局配置
//        GlobalConfig globalConfig = new GlobalConfig();
//        globalConfig.setOutputDir(System.getProperty("user.dir") + "/src/main/java") // 设置输出目录
//                .setAuthor("Jasper") // 设置作者
//                .setOpen(false) // 设置生成后是否自动打开目录
//                .setFileOverride(true) // 设置文件存在时是否覆盖
//                .setServiceName("%sService") // 设置Service接口名后缀
//                .setIdType(IdType.AUTO) // 设置主键生成策略
//                .setSwagger2(true); // 设置是否生成Swagger注解
//
//        // 数据源配置
//        DataSourceConfig dataSourceConfig = new DataSourceConfig();
//        dataSourceConfig.setDbType(DbType.MYSQL) // 设置数据库类型
//                .setUrl("jdbc:mysql://localhost:3306/hotlist?useSSL=false&serverTimezone=UTC") // 数据库连接URL
//                .setUsername("root") // 数据库用户名
//                .setPassword("root") // 数据库密码
//                .setDriverName("com.mysql.cj.jdbc.Driver"); // 数据库驱动类名
//
//        // 策略配置
//        StrategyConfig strategyConfig = new StrategyConfig();
//        strategyConfig.setInclude("article") // 指定需要生成代码的表名
//                .setNaming(NamingStrategy.underline_to_camel) // 设置表名转类名策略
//                .setColumnNaming(NamingStrategy.underline_to_camel) // 设置列名转属性名策略
//                .setEntityLombokModel(true) // 设置实体类使用Lombok模型
//                .setRestControllerStyle(true) // 设置Controller使用REST风格
//                .setTablePrefix(new String[]{"tbl_"}); // 设置表名前缀
//
//        // 包配置
//        PackageConfig packageConfig = new PackageConfig();
//        packageConfig.setParent("com.jl.newshubapi") // 设置父包名
//                .setMapper("mapper") // 设置Mapper接口所在的子包名
//                .setEntity("model.entity") // 设置实体类所在的子包名
//                .setController("controller") // 设置Controller所在的子包名
//                .setService("service") // 设置Service所在的子包名
//                //XML放在resources下的mapper文件夹中
//                .setXml("mapper"); // 设置Mapper XML文件所在的子包名
//
////XML放在resources下的mapper文件夹中
//
//
//        // 模板配置
////        TemplateConfig templateConfig = new TemplateConfig();
////        templateConfig.setXml(null) // 不生成XML文件
////                .setController("templates/controller.java.vm") // 设置Controller模板路径
////                .setEntity("templates/entity.java.vm") // 设置实体类模板路径
////                .setMapper("templates/mapper.java.vm"); // 设置Mapper接口模板路径
//
//        // 整合配置
//        AutoGenerator autoGenerator = new AutoGenerator();
//        autoGenerator.setGlobalConfig(globalConfig)
//                .setDataSource(dataSourceConfig)
//                .setStrategy(strategyConfig)
//                .setPackageInfo(packageConfig);
////                .setTemplate(templateConfig);
//
//        // 执行生成
//        autoGenerator.execute();

        FastAutoGenerator.create("jdbc:mysql://localhost:3306/hotlist?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true", "admin", "lqz654321")
                .globalConfig(builder -> builder
                        .author("Jasper")
                        .outputDir(Paths.get(System.getProperty("user.dir")) + "/NewsHub-API/src/main/java")
                        .commentDate("yyyy-MM-dd")


                )
                .packageConfig(builder -> builder
                        .parent("com.jl.newshubapi")
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl")
                        .xml("mapper.xml")
//                        .pathInfo(Collections.singletonMap(OutputFile.xml, Paths.get(System.getProperty("user.dir")) + "/src/main/resources/mapper"))
                )
                .strategyConfig(builder -> builder
                        .addInclude("ai_summary")
                        .entityBuilder()
                        .enableLombok()
                        .enableTableFieldAnnotation()
                        .idType(IdType.AUTO)
                )
                .execute();
    }
}