package com.nobot.system;

import com.IceCreamQAQ.Yu.loader.LoadItem;
import com.IceCreamQAQ.Yu.loader.Loader;
import com.nobot.system.annotation.CreateDir;
import com.nobot.system.annotation.CreateFile;
import com.nobot.system.annotation.UnzipFile;
import com.nobot.system.annotation.UnzipFileList;
import lombok.NonNull;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class PluginRegister implements Loader
{
	@Inject
	GetResource getResource;

	@Override
	public void load(@NonNull Map<String, LoadItem> map)
	{
		for (Map.Entry<String,LoadItem> entry:map.entrySet())
		{
			Class<?> clazz=entry.getValue().getType();
			CreateDir createDir=clazz.getAnnotation(CreateDir.class);
			if(createDir!=null)
				for(String file:createDir.value())
					createFile(file);
			CreateFile createFile= clazz.getAnnotation(CreateFile.class);
			if(createFile!=null)
				for (String file:createFile.value())
					createDir(file);
			ArrayList<UnzipFile> unzipFiles =new ArrayList<>();
			UnzipFileList unzipFileList = clazz.getAnnotation(UnzipFileList.class);
			if(unzipFileList !=null)
				Collections.addAll(unzipFiles, unzipFileList.value());
			UnzipFile unzipFile = clazz.getAnnotation(UnzipFile.class);
			if(unzipFile !=null)
				unzipFiles.add(unzipFile);
			for (UnzipFile s: unzipFiles)
			{
				getResource.extractFile(s.name(),s.aim(),false);
			}
		}
	}

	@Override
	public int width()
	{
		return 0;
	}

	public boolean createFile(String s)
	{
		File file=new File(s);
		if(file.exists())
		{
			return file.isFile();
		}
		try
		{
			return file.createNewFile();
		}
		catch (IOException e)
		{
			return false;
		}
	}
	public boolean createDir(String s)
	{
		File file=new File(s);
		if(file.exists())
		{
			return file.isDirectory();
		}
		return file.mkdirs();
	}
}
