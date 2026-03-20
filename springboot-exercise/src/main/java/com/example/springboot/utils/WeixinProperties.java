package com.example.springboot.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
//@Component 可以用于标识各种类型的组件，包括但不限于控制器（Controller）、服务（Service）、存储库（Repository）等。这是因为 @Component 注解是一个通用的组件注解，它是更具体的注解（如 @Controller、@Service、@Repository）的基础
@ConfigurationProperties(prefix = "weixin")
//通过使用@ConfigurationProperties注解，您可以将外部配置文件中的键值对配置属性绑定到一个Java类的字段上。这样，您就可以方便地将配置属性注入到您的应用程序中
@Data
public class WeixinProperties {
    private String jscode2sessionUrl; // 登录凭证校验请求地址

    private String appid; // 小程序 appId

    private String secret; // 小程序 appSecret

    private String access;

}
