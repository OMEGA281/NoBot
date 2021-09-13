package com.nobot.plugin.draw;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.nobot.system.annotation.CreateDir;
import com.nobot.tool.XmlReader;
import org.jdom2.Document;
import org.jdom2.JDOMException;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@PrivateController
@GroupController
@EventListener
@CreateDir(ConstantPool.drawPool)
public class Controller
{
	@Inject
	XmlReader xmlReader;

	Map<String,Card> map=new HashMap<>();

	@Event
	public void init(AppStartEvent event)
	{
		File pool=new File(ConstantPool.drawPool);
		for (File file:pool.listFiles((dir, name) -> name.equals("xml")))
		{
			try
			{
				Document document=xmlReader.getDocument(file);
				Card card=new Card(document);
				map.put(file.getName().split("\\.")[0],card);
			}
			catch (JDOMException | IOException e)
			{
				continue;
			}
		}
	}


}
