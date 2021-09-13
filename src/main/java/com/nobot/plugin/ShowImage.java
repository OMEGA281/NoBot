package com.nobot.plugin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import com.nobot.system.annotation.CreateDir;
import lombok.NonNull;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@GroupController
@PrivateController
@EventListener
@CreateDir("Image")
public class ShowImage
{
	File imageFileDir=new File("Image");

	List<File> imageList;

	Random random;

	@Inject
	private MessageItemFactory factory;

	@Event
	public void initList(AppStartEvent event)
	{
		imageList=getImageFile(imageFileDir);
		random=new Random();
	}

	@Action(".image")
	public Message image(Message message, Contact qq) throws IOException
	{
		if (imageList==null||imageList.isEmpty())
			return new Message().plus("图片库不存在图片");
		return new Message().plus(factory.imageByFile(imageList.get(random.nextInt(imageList.size()))));
	}

	public List<File> getImageFile(@NonNull File dir)
	{
		ArrayList<File> list=new ArrayList<>();
		for(File file:dir.listFiles())
		{
			if(file.isDirectory())
				list.addAll(getImageFile(file));
			else if(file.getName().endsWith(".jpg")||file.getName().endsWith(".png")||file.getName().endsWith(".bpm"))
				list.add(file);
		}
		return list;
	}
}
