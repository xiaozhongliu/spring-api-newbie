package com.webapi.seed;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.webapi.seed.domain.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Mybatis Plus Code Generator
 *
 * @author xiaozhong
 * @since 2017-12-30
 */
public class Codegen {

    private static Config config;

    public static void main(String[] args) {

        try {
            String configPath = "./api/src/main/resources/application.yml";
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            config = mapper.readValue(new File(configPath), Config.class);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Map<String, String> pkConfig = config.pack;
        Map<String, String> dsConfig = config.spring.datasource;
        String rootPath = config.pack.get("root-path");
        String[] tableNames = {
                "account",
                "account_role",
                "bookmark"
        };

        GlobalConfig globalConfig;
        globalConfig = new GlobalConfig();
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        StrategyConfig strategyConfig = new StrategyConfig();
        PackageConfig packConfig = new PackageConfig();
        TemplateConfig tplConfig = new TemplateConfig();
        InjectionConfig injectConfig = new InjectionConfig() {
            @Override
            public void initMap() {
            }
        };

        globalConfig.setAuthor(pkConfig.get("author"))
                .setOutputDir(rootPath)
                .setFileOverride(false)
                .setEnableCache(true)
                .setActiveRecord(false)
                .setBaseResultMap(true)
                .setBaseColumnList(true)
                .setMapperName("%sDao");
        dataSourceConfig.setDbType(DbType.MYSQL)
                .setDriverName(dsConfig.get("driver-class-name"))
                .setUrl(dsConfig.get("url"))
                .setUsername(dsConfig.get("username"))
                .setPassword(dsConfig.get("password"));
        strategyConfig.setInclude(tableNames)
                .setCapitalMode(true)
                .setEntityLombokModel(true)
                .setDbColumnUnderline(true)
                .setNaming(NamingStrategy.underline_to_camel)
                .setSuperControllerClass(pkConfig.get("baseController"));
        packConfig.setParent(pkConfig.get("name"))
                .setController("controller")
                .setServiceImpl("service")
                .setEntity("entity")
                .setMapper("dao");

        // close default generation on dao, xml, service and serviceImpl files
        tplConfig.setXml(null)
                .setMapper(null)
                .setService(null)
                .setServiceImpl(null);

        // customize out dir of generated files
        List<FileOutConfig> focList = new ArrayList<>();
        focList.add(new FileOutConfig("/templates/mapper.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return String.format("%s/com/webapi/seed/dao/%sDao.java", rootPath, tableInfo.getEntityName());
            }
        });
        focList.add(new FileOutConfig("/templates/mapper.xml.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return String.format("./src/main/resources/mapper/%s.xml", tableInfo.getEntityName());
            }
        });
        focList.add(new FileOutConfig("/templates/serviceImpl.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return String.format("%s/com/webapi/seed/service/%sService.java", rootPath, tableInfo.getEntityName());
            }
        });
        injectConfig.setFileOutConfigList(focList);

        new AutoGenerator().setGlobalConfig(globalConfig)
                .setDataSource(dataSourceConfig)
                .setStrategy(strategyConfig)
                .setPackageInfo(packConfig)
                .setTemplate(tplConfig)
                .setCfg(injectConfig)
                .execute();
    }

}
