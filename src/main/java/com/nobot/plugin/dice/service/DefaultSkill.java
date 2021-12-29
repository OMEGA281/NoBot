package com.nobot.plugin.dice.service;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.nobot.system.annotation.UnzipFile;
import lombok.Cleanup;
import lombok.var;

import java.io.*;
import java.util.HashMap;

@EventListener
@UnzipFile(name="DefaultSkill.txt",aim="DefaultSkill.txt")
public class DefaultSkill
{
	private static HashMap<String, Integer> map = new HashMap<>();

	@Event(weight = Event.Weight.low)
	public void reloadDefaultSkillNum(AppStartEvent event) throws IOException
	{
		map.clear();
		File file=new File("DefaultSkill.txt");
		@Cleanup var inputStream=new FileInputStream(file);
		@Cleanup var reader=new BufferedReader(new InputStreamReader(inputStream));
		String line;
		while ((line=reader.readLine())!=null)
		{
			if(line.startsWith("#"))
				continue;
			String[] strings=line.replaceAll(" ","").split("=");
			if(strings.length<2)
				continue;
			try
			{
				map.put(SkillNameTranslator.getMainSkillWord(strings[0]),Integer.parseInt(strings[1]));
			}
			catch (NumberFormatException ignored)
			{
			}
		}
	}

	/**
	 * 获得默认技能数值
	 * @param name 技能名称，本方法会自动更正成正确关键词
	 * @return 技能数值，不存在则返回0
	 */
	public static int getDefaultSkill(String name)
	{
		String key=SkillNameTranslator.getMainSkillWord(name);
		return map.getOrDefault(key, 0);
	}
}
