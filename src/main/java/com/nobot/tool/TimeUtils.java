package com.nobot.tool;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils
{
	public static long getNowTimeStamp()
	{
		return System.currentTimeMillis();
	}

	public static String getFormatData(SimpleDateFormat simpleDateFormat)
	{
		return simpleDateFormat.format(new Date(getNowTimeStamp()));
	}
}
