package com.nobot.system;

import com.IceCreamQAQ.Yu.annotation.Config;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.*;
import java.nio.file.Files;

/**
 * 从资源文件夹中获取文件
 */
public class GetResource
{
	/**
	 * 从外界资源目录中寻找文件
	 *
	 * @return 文件，若不存在则返回null
	 */
	public File getOutsideResource(String fileName)
	{
		File file = new File(fileName);
		if (file.exists() && file.isFile())
			return file;
		else
			return null;
	}

	/**
	 * 从包内资源目录中寻找文件
	 *
	 * @return 流，若不存在则返回null
	 */
	public InputStream gerJarResource(String fileName)
	{
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
		return inputStream;
	}

	/**
	 * 将包内资源解压到外部
	 *
	 * @return 是否发生替换。未覆盖、错误、源文件不存在都会返回false
	 */
	public boolean extractFile(String insideFile,String outSideFile,boolean cover)
	{
		InputStream inputStream = gerJarResource(insideFile);
		if (inputStream == null)
			return false;

		File file = getOutsideResource(outSideFile);
		if(file!=null&&!cover)
			return false;
		file=new File(outSideFile);
		try
		{
			file.createNewFile();
			byte[] bytes=new byte[1024];
			FileOutputStream fileOutputStream=new FileOutputStream(file);
			int len;
			while ((len=inputStream.read(bytes))!=-1)
			{
				fileOutputStream.write(bytes,0,len);
			}
			inputStream.close();
			fileOutputStream.close();
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}

	}

}
