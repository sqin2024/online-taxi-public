package com.sqin.servicedriveruser.generator;


import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

public class MysqlGenerator {

    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/service-driver-user?characterEncoding=utf-8&ServerTimezone=GMT%2B8", "root", "123456")
                .globalConfig(builder -> {
                    builder.author("Qin").fileOverride().outputDir("C:\\SourceCode\\online-taxi-public\\service-driver-user\\src\\main\\java");
                })
                .packageConfig(builder -> {
                    builder.parent("com.sqin.servicedriveruser").pathInfo(Collections.singletonMap(OutputFile.mapperXml,
                            "C:\\SourceCode\\online-taxi-public\\service-driver-user\\src\\main\\java\\com\\sqin\\servicedriveruser\\mapper"));
                })
                .strategyConfig(builder -> {
                    builder.addInclude("driver_car_binding_relationship");
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }

}
