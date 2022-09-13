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
	public static String getTimeLengthString(long time,boolean reservedMillisecond)
	{
		long surplus=time;
		long millisecond=surplus%1000;
		surplus=surplus-millisecond;
		long second=surplus%60;
		surplus=surplus-second*1000;
		long minute=surplus%60;
		surplus=surplus-minute*60*1000;
		long hour=surplus%24;
		surplus=surplus-hour*60*60*1000;
		long day=surplus%365;
		surplus=surplus-day*24*60*60*1000;
		long year=surplus;
		StringBuilder stringBuilder=new StringBuilder();
		if(year!=0)
			stringBuilder.append(year).append("年");
		if(day!=0)
			stringBuilder.append(day).append("天");
		if(hour!=0)
			stringBuilder.append(hour).append("小时");
		if(minute!=0)
			stringBuilder.append(minute).append("分钟");
		if(second!=0)
			stringBuilder.append(second).append("秒");
		if(millisecond!=0&&reservedMillisecond)
			stringBuilder.append(year).append("毫秒");
		return stringBuilder.toString();
	}
}
