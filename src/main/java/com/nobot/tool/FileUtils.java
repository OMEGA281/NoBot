package com.nobot.tool;

import java.io.*;
import java.nio.file.Files;

public class FileUtils
{
	public void write(File file, InputStream inputStream)throws IOException
	{
		if(!file.exists())
			file.createNewFile();
		if(file.isDirectory())
			throw new IOException("指定的为一个文件夹");
		FileOutputStream outputStream=new FileOutputStream(file);
		byte[] bytes=new byte[1024];
		int len=0;
		while ((len=inputStream.read(bytes))!=-1)
		{
			outputStream.write(bytes,0,len);
		}
		outputStream.close();
		inputStream.close();
	}

	public static boolean copy(File from,File to,boolean cover) throws IOException
	{
		if(to.exists())
		{
			if (to.isDirectory())
				return false;
			else if (!cover)
				return false;
		}
		Files.copy(from.toPath(),to.toPath());
		return true;
	}
}
