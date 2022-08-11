package com.nobot.plugin.systemController.dao;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.nobot.system.annotation.UnzipFile;
import com.nobot.tool.XmlReader;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
@EventListener
@UnzipFile(name = "BotSetting.xml",aim="config/SystemController")
public class UserSettingDAO
{
	private HashMap<String,String> settingMap=new HashMap<>();
	@Event
	public void onAppStartEventListener(AppStartEvent appStartEvent)
	{
		reloadSettingFile();
	}

	public void reloadSettingFile()
	{
		settingMap.clear();
		XmlReader xmlReader=new XmlReader();
		Document document=new Document();
		try
		{
			document=xmlReader.getDocument("config/SystemController");
		}
		catch (IOException e)
		{
			log.error("无法读取文件",e);
		}
		catch (JDOMException e)
		{
			log.error("读取XML错误",e);
		}
		for (Element element:document.getRootElement().getChildren())
		{
			String name=element.getAttributeValue("name");
			if(name==null)
				continue;
			settingMap.put(name,element.getValue());
		}
		log.info("加载了"+settingMap.size()+"条设置");
	}

	public String getValue(String key)
	{
		return settingMap.get(key);
	}
}
