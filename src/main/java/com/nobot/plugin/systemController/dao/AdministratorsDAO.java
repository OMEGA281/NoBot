package com.nobot.plugin.systemController.dao;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.nobot.system.annotation.CreateFile;
import com.nobot.tool.FileUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.*;
import java.util.ArrayList;

@CreateFile("config/Administrators.json")
@EventListener
@Slf4j
public class AdministratorsDAO
{
	@Getter
	private long SOP;
	@Getter
	private ArrayList<Long> OP;

	@Inject
	private FileUtils fileUtils;
	@Event
	public void reloadAdministrators(AppStartEvent event)
	{
		JSONObject jsonObject;
		try
		{
			jsonObject= JSON.parseObject(fileUtils.readAll("config/Administrators.json"));
		}
		catch (IOException e)
		{
			log.error("读取权限用户列表时出现致命错误");
			return;
		}
		SOP=jsonObject.getLong("SOP");
		String[] text_OP=jsonObject.getString("OP").split(",|，");
		OP=new ArrayList<>();
		for (String s : text_OP) OP.add(Long.parseLong(s));
	}

	public void deleteOP(long num)
	{
		if(OP.remove(num)) save();
	}

	public void addOP(long num)
	{
		if(OP.add(num)) save();
	}

	public void changeSOP(long num)
	{
		if(SOP!=num){SOP=num;save();}
	}

	public void save()
	{
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("SOP",SOP);
		jsonObject.put("OP",OP);
		File file=new File("config/Administrators.json");
		try
		{
			OutputStream outputStream=new FileOutputStream(file);
			JSONObject.writeJSONString(outputStream,jsonObject, SerializerFeature.WriteNullListAsEmpty);
		}
		catch (IOException e)
		{
			log.error("输出权限用户文件时出现致命错误");
		}
	}
}
