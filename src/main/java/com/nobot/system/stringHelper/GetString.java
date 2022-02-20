package com.nobot.system.stringHelper;

import com.IceCreamQAQ.Yu.cache.EhcacheHelp;
import lombok.var;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.Properties;

public class GetString
{
	@Inject
	@Named("StringCache")
	private EhcacheHelp<Properties> ehcache;

	@Inject
	private StringFileMap stringFileMap;

	public String addressing(String address)
	{
		String fileName,textName;
		if(address.contains(":"))
		{
			var var=address.split(":");
			fileName=var[0];
			textName=var[1];
		}
		else
		{
			String className=null;
			for(StackTraceElement stackTraceElement:Thread.currentThread().getStackTrace())
			{
				String name=stackTraceElement.getClassName();
				if(!name.equals(this.getClass().getName()))
					className=name;
			}
			fileName=stringFileMap.get(className);
			textName=address;
		}

		Properties properties=ehcache.get(fileName);
		if(properties==null)
		{
			try
			{
				properties=StringPointExtract.readProperties(fileName);
			}
			catch (IOException e)
			{
				return "字符串文件映射表读取错误！";
			}
			ehcache.set(fileName,properties);
		}
		return properties.getProperty(textName,"不存在的字符串索引！");
	}

	public String formatString(String text,String...args)
	{
		StringBuilder string=new StringBuilder(text);
		for(int index=0;index<string.length();)
		{
			char c=string.charAt(index);
			if(c=='$')
			{
				if(index<=0||string.charAt(index-1)!='\\')
				{
					int startIndex,endIndex;
					startIndex=index;
					StringBuilder builder=new StringBuilder();
					index++;
					for (char num=string.charAt(index);'0'<=num&&num<='9';num=string.charAt(index),index++)
						builder.append(num);
					endIndex=index;
					int argIndex=Integer.parseInt(builder.toString());
					String replaceText;
					if(argIndex>=args.length||argIndex<0)
						replaceText="NULL";
					else
						replaceText=args[argIndex];
					string.replace(startIndex,endIndex,replaceText);
					index=startIndex+replaceText.length();
					continue;
				}
			}
			index++;
		}
		return string.toString();
	}
}
