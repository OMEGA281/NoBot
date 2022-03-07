package com.nobot.system.stringHelper;

import java.util.HashMap;

public class StringFileMap
{
	private HashMap<String,String> map=new HashMap<>();

	public String put(String key, String value)
	{
		return map.put(key,value);
	}

	public String remove(String key)
	{
		return map.remove(key);
	}

	public String get(String key)
	{
		return map.get(key);
	}
}
