package com.tencent.wxcloudrun.util;

import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class HttpSignUtil {

	/**
	 * 参数map不作urlencode，可用于已经encode过的参数map加签
	 *
	 * @param queryMap  参数map
	 * @param useSecret 是否使用密钥
	 * @param secret    密钥
	 * @return
	 */
	public static String getSignWithOutUrlEncode(Map<String, String> queryMap, String secret) {
		Map<String, String> sortedMap = new TreeMap<>(queryMap);
		String strToEncrypt = getTextToSigned(sortedMap, false, false);
		System.out.println(strToEncrypt);
		return getSignText(strToEncrypt, StringUtils.hasText(secret), secret);
	}

	private static String getSignText(String strToEncrypt, boolean useSecret, String secret) {
		if (strToEncrypt.length() > 0) {
			strToEncrypt = strToEncrypt.substring(0, strToEncrypt.length() - 1);
		}
		String format = String.format("%s%s", strToEncrypt, useSecret ? secret : "");
		System.out.println(format);
		return md5(format);
	}

	public static String urlEncode(String source) {
		return urlEncode(source, true);
	}

	public static String getSign(Map<String, String> queryMap, String secret) {
		return getSign(queryMap, StringUtils.hasText(secret),secret,false);
	}

	/**
	 * @param queryMap          查询字符串map
	 * @param useSecret         是否使用secret
	 * @param secret            secret
	 * @param encodeBlankToPlus 如果为true 使用Java原生url encode将空格编码为加号'+' 否则encode为'%20'
	 *                          ,默认采用RFC 3986规范 将空格编码为'%20'
	 * @return sign
	 */
	public static String getSign(Map<String, String> queryMap, boolean useSecret, String secret, boolean encodeBlankToPlus) {
		Map<String, String> sortedMap = new TreeMap<>(queryMap);
		String strToEncrypt = getTextToSigned(sortedMap, encodeBlankToPlus, true);
		return getSignText(strToEncrypt, useSecret, secret);
	}

	public static String getLiveLinkSign(Map<String, String> queryMap, String secret) {
		Map<String, String> sortedMap = new TreeMap<>(queryMap);
		StringBuilder raw = new StringBuilder();
		for (Map.Entry<String, String> next : sortedMap.entrySet()) {
			raw.append(urlEncode(next.getValue(), false)).append("+");
		}
		raw.append(secret);
		return md5(raw.toString());
	}

	private static String md5(String raw) {
		return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
	}


	private static String getTextToSigned(Map<String, String> sortedMap, boolean encodeBlankToPlus, boolean useUrlEncode) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (value == null) {
				value = "";
			}
			if (useUrlEncode) {
				value = urlEncode(value, encodeBlankToPlus);

			}
			System.out.println("key["+key+"]---value["+value+"]");
			sb.append(key).append("=").append(value).append("&");
		}
		return sb.toString();
	}

	private static String urlEncode(String str, boolean encodeBlankToPlus) {
		String encodedStr = URLEncoder.encode(str, StandardCharsets.UTF_8);
		if (encodeBlankToPlus) {
			return encodedStr;
		}
		return encodedStr.replace("+", "%20");
	}



	public static void main(String[] args) {

		String encode = URLEncoder.encode(" ", StandardCharsets.UTF_8);
		System.out.println(encode);
		Map<String, String> queryMap = new HashMap<>();
		queryMap.put("game_id", "5402");
		queryMap.put("uid", "3508041");
		queryMap.put("ts", "1647851374432");
		queryMap.put("appkey", "klnMm4IwB0wVKd5M");
		queryMap.put("name", "昵称12");
		String md5Sign = getSign(queryMap, true, "NMl2lj4eJK2plFiXJ1Di3ECFW8zykCVg", true);
		System.out.println(md5Sign);
	}

}
