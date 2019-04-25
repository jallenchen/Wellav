package com.wellav.dolphin.imagelru;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Copyright 2015 Wellav
 * 
 * All rights reserved.
 * @date2015年12月11日
 * @author Cheng
 */
public class MD5Utils {
	/**
	 * 使用md5的算法进行加密
	 * @param plainText 要进行加密的数据
	 * @return 加密后的数据
	 */
	public static String md5(String plainText) {
		byte[] secretBytes = null;
		try {
			secretBytes = MessageDigest.getInstance("md5").digest(
					plainText.getBytes());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("no md5 this arithmetic！");
		}
		String md5code = new BigInteger(1, secretBytes).toString(16);// 16进制数字
		// 如果生成数字未满32位，需要前面补0
		for (int i = 0; i < 32 - md5code.length(); i++) {
			md5code = "0" + md5code;
		}
		return md5code;
	}

}
