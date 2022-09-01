package com.tencent.wxcloudrun.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

public class EncryptUtils {

	private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'a', 'b', 'c', 'd', 'e', 'f'};

	public static MessageDigest messagedigest = null;

	private static final String MD5 = "MD5";
	private static final String SHA_1 = "SHA-1";
	private static final String SHA_256 = "SHA-256";
	private static final String AES = "AES";

	/**
	 * 随机数生成器（RNG）算法名称
	 */
	private static final String RNG_ALGORITHM = "SHA1PRNG";
	/**
	 * 密钥位数
	 */
	private static final int KEY_SIZE = 128;


	public static String getMD5(String str) {
		try {
			return getByAlgorithmAndByte(MD5, str.getBytes(StandardCharsets.UTF_8));
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	/**
	 * 生成密钥对象
	 */
	private static SecretKey generateKey(byte[] key) throws Exception {
		// 创建安全随机数生成器
		// 设置 密钥key的字节数组 作为安全随机数生成器的种子


		// 创建 AES算法生成器
		// KeyGenerator gen = KeyGenerator.getInstance(AES);
		// // 初始化算法生成器
		// gen.init(KEY_SIZE,  new SecureRandom(key));
		return new SecretKeySpec(key, AES);
		// 生成 AES密钥对象, 也可以直接创建密钥对象: return new SecretKeySpec(key, ALGORITHM);
		// return gen.generateKey();
	}


	public static byte[] encryptWithAES(String msg, String secretKey) {
		try {
			SecretKey secretKeyByte = generateKey(secretKey.getBytes(StandardCharsets.UTF_8));
			Cipher instance = Cipher.getInstance(AES);
			instance.init(Cipher.ENCRYPT_MODE, secretKeyByte);
			return instance.doFinal(msg.getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String getSHA1(String str) {
		try {
			return getByAlgorithmAndByte(SHA_1, str.getBytes(StandardCharsets.UTF_8));
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}


	/**
	 * 对文件进行MD5
	 *
	 * @param file 文件
	 * @return 加密code
	 */
	public static String getMD5(File file) throws IOException {
		try {
			return getByAlgorithmAndFile(MD5, file);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	public static String getSHA1(File file) throws IOException {
		try {
			return getByAlgorithmAndFile(SHA_1, file);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	public static String getSHA256(File file) throws IOException {
		try {
			return getByAlgorithmAndFile(SHA_256, file);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	public static String getCRC32(File file) {
		CRC32 crc32 = new CRC32();
		// MessageDigest.get
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
			byte[] buffer = new byte[8192];
			int length;
			while ((length = fileInputStream.read(buffer)) != -1) {
				crc32.update(buffer, 0, length);
			}
			return crc32.getValue() + "";
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (fileInputStream != null)
					fileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static String getByAlgorithmAndByte(String algorithm, byte[] bytes) throws NoSuchAlgorithmException {
		messagedigest = MessageDigest.getInstance(algorithm);
		messagedigest.update(bytes);
		return bufferToHex(messagedigest.digest());
	}

	private static String getByAlgorithmAndFile(String algorithm, File file) throws NoSuchAlgorithmException, IOException {
		messagedigest = MessageDigest.getInstance(algorithm);
		FileInputStream in = new FileInputStream(file);
		FileChannel ch = in.getChannel();
		MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
		messagedigest.update(byteBuffer);
		return bufferToHex(messagedigest.digest());
	}


	private static String bufferToHex(byte[] bytes) {
		StringBuffer sb = new StringBuffer(bytes.length * 2);
		for (byte x : bytes) {
			appendHexPair(x, sb);
		}
		return sb.toString();
	}

	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		//高4位
		char c0 = hexDigits[(bt & 0xf0) >> 4];
		//低4位
		char c1 = hexDigits[bt & 0xf];
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}

	public static void main(String[] args) throws Exception {
		// File file = new File("/Users/lantin/Desktop/test.log");
		// String md5 = getMD5(file);
		// String sha1 = getSHA1(file);
		// String sha256 = getSHA256(file);
		// String crc32 = getCRC32(file);
		// System.out.println("code:"+md5);
		// System.out.println("code:"+sha1);
		// System.out.println("code:"+sha256);
		// System.out.println("code:"+crc32);


		String uid = "3c5c30ac26f643e0bb3a6109b8cc2551";
		String key = "5cf8c22d98f7b475";
		JSONObject userid = new JSONObject() {{
			put("userid", uid);
		}};
		byte[] encrypt = encryptWithAES(userid.toJSONString(), key);

		String s = Base64Utils.encodeToString(encrypt);
		// String s1 = Base64Utils.encodeToUrlSafeString(encrypt);
		System.out.println(s);
		// System.out.println(s1);
		String s1 = HttpSignUtil.urlEncode(s);
		System.out.println(s1);
		String code = "orHO%2F%2BI3bcF3M1HUn7btRLV4ctGv2R%2BnZwfiMqCV%2Fa5LyEE%2FUO0oQ7K4hL3tTHiW";

		Assert.isTrue(s1.equals(code),"应该相等");

	}
}
