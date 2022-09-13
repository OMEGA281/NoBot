package com.nobot.system.stringHelper;

import com.IceCreamQAQ.Yu.cache.EhcacheHelp;
import lombok.var;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

public class GetString
{
	@Inject
	@Named("StringCache")
	private EhcacheHelp<Properties> ehcache;

	@Inject
	private StringFileMap stringFileMap;

	private Random random;

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
				if(!(name.equals(this.getClass().getName())||name.equals(Thread.class.getName())))
				{
					className = name;
					break;
				}
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
		ArrayList<String> stringList=new ArrayList<>();
		String mainString=properties.getProperty(textName);
		if(mainString!=null)
		{
			stringList.add(mainString);
			for(;;)
			{
				textName=textName+"[";
				String subString=properties.getProperty(textName);
				if(subString==null)
					break;
				else
					stringList.add(subString);
			}
		}
		return stringList.size()==1?stringList.get(0):stringList.get(random.nextInt(stringList.size()));
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
					for(char num=string.charAt(index);'0'<=num&&num<='9';)
					{
						builder.append(num);
						index++;
						if(index<string.length())
							num=string.charAt(index);
						else break;
					}
					endIndex=index;
					int argIndex=Integer.parseInt(builder.toString());
					String replaceText;
					if(argIndex>args.length||argIndex<0)
						replaceText="NULL";
					else
						replaceText=args[argIndex-1];
					string.replace(startIndex,endIndex,replaceText);
					index=startIndex+replaceText.length();
					continue;
				}
			}
			index++;
		}
		return string.toString();
	}

	public String addressAndFormat(String address,String...args)
	{
		String s=addressing(address);
		return formatString(s,args);
	}
}
