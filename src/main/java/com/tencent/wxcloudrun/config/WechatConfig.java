package com.tencent.wxcloudrun.config;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(WeChatOfficialAccountProperties.class)
public class WechatConfig {

}
