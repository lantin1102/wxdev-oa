package com.tencent.wxcloudrun.controller;


import com.tencent.wxcloudrun.config.WeChatOfficialAccountProperties;
import com.tencent.wxcloudrun.util.EncryptUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;
import java.util.TreeSet;

@Controller
@RequestMapping("/wechat")
@Slf4j
public class WeChatController {

	@Autowired
	private WeChatOfficialAccountProperties officialAccountProperties;


	@GetMapping("/auth")
	public void auth(@RequestParam("code") String code) {
		log.info("进入auth方法");
		log.info("获取用户code={}", code);
	}

	@GetMapping("/wx")
	@ResponseBody
	public String testServer(String signature,
	                         String timestamp,
	                         String nonce,
	                         String echostr) {
		if (!StringUtils.hasText(signature)||
				!StringUtils.hasText(timestamp)||
				!StringUtils.hasText(nonce)||
				!StringUtils.hasText(echostr)){
			log.warn("param missing");
			return "failure";
		}

		log.info("get message for wechat,signature:{},timestamp:{},nonce:{},echostr:{}",
				signature, timestamp, nonce, echostr);
		boolean b = checkSign(signature, timestamp, nonce);
		if (b) {
			return echostr;
		} else {
			return "failure";
		}
	}

	private boolean checkSign(String signature, String timestamp, String nonce) {
		TreeSet<String> treeSet = new TreeSet<>();
		treeSet.add(timestamp);
		treeSet.add(nonce);
		treeSet.add(officialAccountProperties.getToken());

		StringBuilder sb = new StringBuilder();
		treeSet.forEach(v -> {
			v = v == null ? "" : v;
			sb.append(v);
		});
		String sha1 = EncryptUtils.getSHA1(sb.toString());
		log.info("get encrypt sha1:{}", sha1);
		return Objects.equals(sha1, signature);
	}


}
