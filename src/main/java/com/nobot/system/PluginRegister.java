package com.nobot.system;

import com.IceCreamQAQ.Yu.loader.LoadItem;
import com.IceCreamQAQ.Yu.loader.Loader;
import com.nobot.system.annotation.CreateDir;
import com.nobot.system.annotation.CreateFile;
import com.nobot.system.annotation.UnzipFile;
import com.nobot.system.annotation.UnzipFileList;
import com.nobot.tool.fileHelper.FileOrFolderHasExistException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

@Slf4j
public class PluginRegister implements Loader
{
	@Inject
	GetResource getResource;

	@Override
	public void load(@NonNull Map<String, LoadItem> map)
	{
		log.debug("抽出"+map.size()+"条需加载注解");
		for (Map.Entry<String,LoadItem> entry:map.entrySet())
		{
			Class<?> clazz=entry.getValue().getType();
			CreateDir createDir=clazz.getAnnotation(CreateDir.class);
			if(createDir!=null)
				for(String file:createDir.value())
				{
					try
					{
						createDir(file);
					}
					catch (FileOrFolderHasExistException e)
					{
						if (e.isSameType)
							log.debug("已经存在文件夹，不创建");
						else
							log.error("文件夹创建失败，存在同名文件");
					}
				}
			CreateFile createFile= clazz.getAnnotation(CreateFile.class);
			if(createFile!=null)
				for (String file:createFile.value())
				{
					try
					{
						createFile(file);
					}
					catch (FileOrFolderHasExistException e)
					{
						if (e.isFile)
						{
							if (e.isSameType)
							{
								log.debug("已经存在文件，不创建");
							}
							else
							{
								log.error("文件创建失败，存在同名文件夹");
							}
						}
						else
						{
							if (!e.isSameType)
								log.error("文件夹创建失败，存在同名文件");
						}
					}
					catch (IOException e)
					{
						log.error("创建文件时候出现系统问题");
					}
				}
			ArrayList<UnzipFile> unzipFiles =new ArrayList<>();
			UnzipFileList unzipFileList = clazz.getAnnotation(UnzipFileList.class);
			if(unzipFileList !=null)
				Collections.addAll(unzipFiles, unzipFileList.value());
			UnzipFile unzipFile = clazz.getAnnotation(UnzipFile.class);
			if(unzipFile !=null)
				unzipFiles.add(unzipFile);
			for (UnzipFile s: unzipFiles)
			{
				try
				{
					getResource.extractFile(s.name(),s.aim(),false);
				}
				catch (FileNotFoundException e)
				{
					log.error("不存在文件"+s.name()+"，无法抽出");
				}
				catch (FileOrFolderHasExistException e)
				{
//					FIXME:将来如果重构了文件体系，这里要加上更多条件
					if(e.isFile&&e.isSameType)
						log.error(s.name()+"文件已经存在，且被要求不得覆盖！");
					else if(e.isFile)
						log.error(s.name()+"文件无法抽出，本地有同名文件夹");
				}
			}
		}
	}

	@Override
	public int width()
	{
		return 0;
	}

	public boolean createFile(String s) throws FileOrFolderHasExistException, IOException
	{
		File file=new File(s);
		if(file.exists())
			throw new FileOrFolderHasExistException(true,file.isFile(),s);
		String fileName;
//		检测是否是需要先行创建文件夹
		if (s.contains("/")||s.contains("\\"))
		{
			s=s.replaceAll("\\|/",File.separator);
			int index=s.lastIndexOf(File.separator);
			String folder=s.substring(0,index);
			createDir(folder);
		}
		return file.createNewFile();
	}
	public boolean createDir(String s) throws FileOrFolderHasExistException
	{
		File file=new File(s);
		if(file.exists())
			throw new FileOrFolderHasExistException(false,!file.isFile(),s);
		return file.mkdirs();
	}


}
