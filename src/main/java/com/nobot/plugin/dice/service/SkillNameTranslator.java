package com.nobot.plugin.dice.service;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.nobot.system.annotation.UnzipFile;
import lombok.Cleanup;
import lombok.var;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@UnzipFile(name = "COCSkillNameTranslator.txt",aim = "COCSkillNameTranslator.txt")
@EventListener
public class SkillNameTranslator
{
	private static HashMap<String,String[]> map=new HashMap<>();
	@Event
	public void reloadSkillName(AppStartEvent event) throws IOException
	{
		File file=new File("COCSkillNameTranslator.txt");
		@Cleanup var inputStream=new FileInputStream(file);
		@Cleanup var reader=new BufferedReader(new InputStreamReader(inputStream));
		String line;
		while ((line=reader.readLine())!=null)
		{
			if(line.startsWith("#"))
				continue;
			String[] strings=line.split("\\|");
			String[] subWords=Arrays.copyOfRange(strings,1,strings.length);
//			TODO:这里没有做词语的重叠检查
			map.put(strings[0],subWords);
		}
	}

	public static String getMainSkillWord(String string)
	{
		if (map.containsKey(string))
			return string;
		for (Map.Entry<String,String[]> entry:map.entrySet())
		{
			for (String s : entry.getValue())
			{
				if(s.equals(string))
					return entry.getKey();
			}
		}
		return string;
	}
}