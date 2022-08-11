package com.nobot.plugin.systemController.service;

import com.nobot.plugin.systemController.dao.UserSettingDAO;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
public class DefaultSettingService
{
	private static final boolean GROUP_ACTIVE=true;
	private static final boolean GROUP_SLEEP=false;
	private static final String GROUP_SLEEP_MODE="GROUP_SLEEP_MODE";

	private static final int ANSWER_ALL_USER=0;
	private static final int ANSWER_EXCEPT_BLACK_USER=1;
	private static final int ANSWER_ONLY_WHITE_USER=2;
	private static final int ANSWER_ONLY_OP=3;
	private static final int ANSWER_NO_USER=4;
	private static final String STRICTNESS="STRICTNESS";

//	最最后的，存在在代码中的设置
	private final boolean DEFAULT_GROUP_SLEEP_MODE=GROUP_SLEEP;
	private final int DEFAULT_STRICTNESS=ANSWER_EXCEPT_BLACK_USER;

	@Inject
	private UserSettingDAO userSettingDAO;

	public boolean isDefaultGroupActive()
	{
		String s=userSettingDAO.getValue(GROUP_SLEEP_MODE);
		if(s==null)
			return DEFAULT_GROUP_SLEEP_MODE;
		try
		{
			return Boolean.parseBoolean(s);
		}
		catch (Exception e)
		{
			log.error("无法获得配置文件中关于群默认设置，请检查文件内容");
			return DEFAULT_GROUP_SLEEP_MODE;
		}
	}
	public int getDefaultStrictness()
	{
		String s=userSettingDAO.getValue(STRICTNESS);
		if(s==null)
			return DEFAULT_STRICTNESS;
		try
		{
			return Integer.parseInt(s);
		}
		catch (Exception e)
		{
			log.error("无法获得配置文件中关于群默认设置，请检查文件内容");
			return DEFAULT_STRICTNESS;
		}
	}
}
