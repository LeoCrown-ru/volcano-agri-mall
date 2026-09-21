package com.cloud.gen;

import com.cloud.common.security.annotation.EnableCustomConfig;
import com.cloud.common.security.annotation.EnableRyFeignClients;
import com.cloud.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 代码生成
 * 
 * @author cloud
 */
@EnableCustomConfig
@EnableCustomSwagger2   
@EnableRyFeignClients
@SpringBootApplication
public class CloudGenApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(CloudGenApplication.class, args);
        System.out.println("代码生成模块启动成功");
    }
}
