package com.nobot.plugin.botInfo.dao;

import com.nobot.plugin.botInfo.PropertiesConstant;
import com.nobot.system.annotation.UnzipFile;
import lombok.Cleanup;
import lombok.val;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@UnzipFile(name = "BotSetting.properties",aim = "BotSetting.properties")
public class GlobalSettingDAO implements PropertiesConstant
{
	private final String settingFile="BotSetting.properties";

	Properties properties;

	public GlobalSettingDAO() throws IOException
	{
		properties=new Properties();
		@Cleanup val reader=new FileReader(settingFile);
		properties.load(reader);
	}

	public List<Long> getGlobalBanUser()
	{
		val list=new ArrayList<Long>();
		val s=properties.getProperty(globalBanUser);
		if (s.isEmpty())
			return list;
		val strings=s.split(",");
		for (String string:strings)
		{
			try
			{
				list.add(Long.parseLong(string));
			}
			catch (NumberFormatException ignored)
			{
			}
		}
		return list;
	}

	public void addGlobalBanUser(long num) throws IOException
	{
		val list=getGlobalBanUser();
		if(list.contains(num))
			return;
		list.add(num);
		saveGlobalBanUser(list);
	}

	public void removeGlobalBanUser(long num) throws IOException
	{
		val list=getGlobalBanUser();
		if(!list.contains(num))
			return;
		list.remove(num);
		saveGlobalBanUser(list);
	}

	public void saveGlobalBanUser(List<Long> list) throws IOException
	{
		val s=new StringBuilder();
		for (long l:list)
			s.append(l).append(",");
		s.delete(s.length()-1,s.length());
		properties.setProperty(globalBanUser,s.toString());
		@Cleanup val writer=new FileWriter(settingFile);
		properties.store(writer,comment);
	}

	public boolean getDefaultState()
	{
		val s=properties.getProperty(defaultState);
		try
		{
			return Boolean.parseBoolean(s);
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public void setDefaultState(boolean b)throws IOException
	{
		properties.setProperty(defaultState,Boolean.toString(b));
		@Cleanup val writer=new FileWriter(settingFile);
		properties.store(writer,comment);
	}
}
