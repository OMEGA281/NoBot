package com.nobot.system.stringHelper;

import com.IceCreamQAQ.Yu.loader.LoadItem;
import com.IceCreamQAQ.Yu.loader.Loader;
import com.IceCreamQAQ.Yu.util.IO;
import com.nobot.system.annotation.CreateFile;
import lombok.Cleanup;
import lombok.var;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

@CreateFile("StringTable.properties")
public class StringPointExtract implements Loader
{
	@Inject
	private StringFileMap stringFileMap;

	@Override
	public void load(@NotNull Map<String, LoadItem> map)
	{
		Properties properties;
		String listName="StringTable.properties";
		try
		{
			properties=readProperties(listName);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}

		StringBuilder builder=new StringBuilder();
		for (var entry:map.entrySet())
		{
			Class<?> clazz=entry.getValue().getType();
			DefaultStringFile annotation=clazz.getAnnotation(DefaultStringFile.class);
			String info= annotation.info();
			String fileName= annotation.value();

			if(!properties.containsKey(entry.getKey()))
				properties.setProperty(entry.getKey(),fileName);
			builder.append(entry.getKey()).append(':').append(info).append('\n');
		}
		try
		{
			storeProperties(listName,properties, builder.toString());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		for(var var: properties.entrySet())
			stringFileMap.put((String) var.getKey(),(String) var.getValue());
	}

	@Override
	public int width()
	{
		return 2;
	}

	protected static Properties readProperties(String file)throws IOException
	{
		Properties properties=new Properties();
		@Cleanup var reader=new FileReader(file);
		properties.load(reader);
		return properties;
	}

	protected static void storeProperties(String file,Properties properties,String comments)throws IOException
	{
		@Cleanup var writer=new FileWriter(file);
		properties.store(writer,comments);
	}

	protected static void storeProperties(String file,Properties properties)throws IOException
	{
		storeProperties(file,properties,"");
	}
}
