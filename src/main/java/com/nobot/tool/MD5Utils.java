package com.nobot.tool;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils
{
	public static BigInteger getMD5(String s)
	{
		try
		{
			MessageDigest md5=MessageDigest.getInstance("MD5");
			md5.update(s.getBytes(StandardCharsets.UTF_8));
			byte[] bytes=md5.digest();
			BigInteger bigInteger=new BigInteger(1,bytes);
			return bigInteger;
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	public static String getMD5(String s,int radix)
	{
		BigInteger bigInteger=getMD5(s);
		return bigInteger.toString(radix);
	}
}
