package com.tencent.wxcloudrun.controller;


import com.tencent.wxcloudrun.config.WeChatOfficialAccountProperties;
import com.tencent.wxcloudrun.util.EncryptUtils;
import com.tencent.wxcloudrun.util.HttpSignUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller("/wechat")
@Slf4j
public class WeChatController {

	@Autowired
	WeChatOfficialAccountProperties officialAccountProperties;

	@GetMapping("/connection/test")
	@ResponseBody
	public String testServer(String signature,
	                         String timestamp,
	                         String nonce,
	                         String echostr) {

		log.info("get message for wechat,signature:{},timestamp:{},nonce:{},echostr:{}",
				signature, timestamp, nonce, echostr);
		boolean b = checkSign(signature, timestamp, nonce);
		if (b) {
			return echostr;
		} else {
			return "failure";
		}
	}
	private boolean checkSign(String signature,String timestamp,String nonce ){
		TreeSet<String> treeSet = new TreeSet<>();
		treeSet.add(timestamp);
		treeSet.add(nonce);
		treeSet.add(officialAccountProperties.getToken());

		StringBuilder sb = new StringBuilder();
		treeSet.forEach(v->{
			v = v==null?"":v;
			sb.append(v);
		});
		String sha1 = EncryptUtils.getSHA1(sb.toString());

		return Objects.equals(sha1, signature);
	}



}
