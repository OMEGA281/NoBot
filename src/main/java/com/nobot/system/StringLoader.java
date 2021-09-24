package com.nobot.system;

import com.IceCreamQAQ.Yu.as.ApplicationService;
import com.nobot.system.annotation.CreateDir;
import com.nobot.tool.FileUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@CreateDir("string")
public class StringLoader implements ApplicationService
{
	private static Map<String, List<String>> map= new HashMap<>();
	@Override
	public void init()
	{
		//获取包内包外字符串文件
		URL url=this.getClass().getResource("/string");
		File jarStringDir=new File(url.getPath());
		File outsideStringDir=new File("string");
		File[] jarStringFiles=jarStringDir.listFiles((dir, name) -> name.endsWith("properties"));
		File[] outsideStringFiles=outsideStringDir.listFiles((dir, name) -> name.endsWith("properties"));

		//解压未被解压的文件
		code_0:for(File jarFile:jarStringFiles)
		{
			for (File outFile:outsideStringFiles)
				if(jarFile.getName().equals(outFile.getName()))
					continue code_0;
			try
			{
				FileUtils.copy(jarFile,new File("string/"+jarFile.getName()),false);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
//		重加载外部文件
		outsideStringFiles=outsideStringDir.listFiles((dir, name) -> name.endsWith("properties"));

		for(File file:outsideStringFiles)
		{
			String fileName=file.getName().substring(0,file.getName().length()-11);
			Properties properties=new Properties();
			try
			{
				InputStreamReader inputStreamReader=new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
				properties.load(inputStreamReader);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				continue;
			}
			properties.forEach((o, o2) -> {
				String key=(String)o;
				String value=(String)o2;
				while (key.endsWith("["))
					key=key.substring(0,key.length()-1);
				key=fileName+"."+key;
				if(map.containsKey(key))
					map.get(key).add(value);
				else
				{
					List<String> list=new ArrayList<>();
					list.add(value);
					map.put(key,list);
				}
			});
		}
	}

	@Override
	public void start()
	{

	}

	@Override
	public void stop()
	{

	}

	@Override
	public int width()
	{
		return 0;
	}
}
