package com.teng.common;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

public class CodeGenerator {
    public static void main(String[] args){
        //创建对象
        AutoGenerator autoGenerator = new AutoGenerator();
        //数据源
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.MYSQL) // 设置数据库类型，如MySQL、Oracle等
                .setUrl("jdbc:mysql://localhost:3306/agriculture") // 数据库连接URL
                .setUsername("root") // 数据库用户名
                .setPassword("001208@Dxw") // 数据库密码
                .setDriverName("com.mysql.cj.jdbc.Driver"); // 数据库驱动类名
        autoGenerator.setDataSource(dataSourceConfig);
        //全局策略配置
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setOutputDir("D:\\project\\spring_vue_yolo\\Springboot_project\\src\\main\\java") // 设置生成代码的输出目录,你项目的目录
                .setAuthor("admin") // 设置作者信息
                .setOpen(false); // 设置生成后是否自动打开目录
        //去掉Service的
        globalConfig.setServiceName("%sService");
        autoGenerator.setGlobalConfig(globalConfig);
        //包配置
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent("com.teng") // 设置父包名
                .setModuleName(null)
                .setMapper("mapper") // 设置Mapper接口所在的子包名
                .setEntity("entity") // 设置实体类所在的子包名
                .setService("service")
                .setServiceImpl("service.impl")
                .setController("controller"); // 设置Controller所在的子包名
        autoGenerator.setPackageInfo(packageConfig);
        //策略配置
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setInclude("user") // 指定需要生成代码的表名
                .setNaming(NamingStrategy.underline_to_camel)
                .setColumnNaming(NamingStrategy.underline_to_camel)
                .setEntityLombokModel(true); // 设置实体类使用Lombok模型
        autoGenerator.setStrategy(strategyConfig);
        //启动
        autoGenerator.execute();
    }
}
