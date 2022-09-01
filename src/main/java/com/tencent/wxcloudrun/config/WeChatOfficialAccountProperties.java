package com.tencent.wxcloudrun.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ConfigurationProperties("wechat.official-account")
@Data
public class WeChatOfficialAccountProperties {

	private String token;


}
