package com.nobot.system;

import com.IceCreamQAQ.Yu.loader.LoadItem;
import com.IceCreamQAQ.Yu.loader.Loader;
import com.nobot.system.annotation.CreateDir;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;

public class PluginRegister implements Loader
{
	@Override
	public void load(@NotNull Map<String, LoadItem> map)
	{
		for (Map.Entry<String,LoadItem> entry:map.entrySet())
		{
			Class clazz=entry.getValue().getType();
			CreateDir createDir=(CreateDir) clazz.getAnnotation(CreateDir.class);
			if(createDir!=null)
			{
				for(String file:createDir.value())
				{
					creatFile(file);
				}
			}
		}
	}

	@Override
	public int width()
	{
		return 0;
	}

	public boolean creatFile(String s)
	{
		File file=new File(s);
		if(file.exists())
		{
			if(file.isFile())
				return true;
			else
				return false;
		}
		return file.mkdirs();
	}
}
