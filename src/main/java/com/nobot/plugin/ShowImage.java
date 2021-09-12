package com.nobot.plugin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.message.Image;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import com.nobot.system.RegisterWindow;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@EventListener
@GroupController
@PrivateController
public class ShowImage implements RegisterWindow
{
	File imageFileDir=new File("Image");

	@Inject
	private MessageItemFactory factory;

	@Action(".image")
	public Object image(Message message, Contact qq) throws IOException
	{
//		List<File> list=new ArrayList<>();
//		if(!imageFileDir.exists()||imageFileDir.isFile())
//			return "无法寻找到图片库";
//		for(String dir:dirList)
//		{
//			File file=new File(imageFileDir.getAbsolutePath()+"\\"+dir);
//			for (File f:file.listFiles())
//				list.add(f);
//		}
//
//		Random random=new Random();
//		File file=list.get(random.nextInt(list.size()));
//		Image image=factory.imageByFile(file);
//
//		return image;
		return null;
	}

	@Override
	public void onStart()
	{

	}
}
