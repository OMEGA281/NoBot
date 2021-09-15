package com.nobot.plugin.dice;

public class NoSuitableCardException extends Exception
{
	long qqNum,groupNum;
	NoSuitableCardException(long qqNum,long groupNum)
	{
		this.qqNum=qqNum;
		this.groupNum=groupNum;
	}
}
