package com.nobot.plugin.draw;

import lombok.Data;

/*
用于储存各类信息
 */
@Data
public class Info
{
	public String botName="";
	public long userQQ;
	public String userName;
	public boolean inGroup;
	public long groupNum;
	public String groupName;

}
