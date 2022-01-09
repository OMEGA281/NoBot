package com.nobot.plugin.image;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.IceCreamQAQ.Yu.event.events.AppStopEvent;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.controller.BotActionContext;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Friend;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.event.GroupMessageEvent;
import com.icecreamqaq.yuq.event.MessageEvent;
import com.icecreamqaq.yuq.event.SendMessageEvent;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import com.icecreamqaq.yuq.message.MessageSource;
import com.nobot.plugin.image.service.Service;
import com.nobot.system.annotation.CreateDir;
import com.nobot.system.annotation.CreateFile;
import com.nobot.tool.ImageCompressor;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.var;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

@GroupController
@PrivateController
@EventListener
@CreateDir({"Image"})
public class ShowImage
{
	private Random random;
	private static Logger logger=LoggerFactory.getLogger(ShowImage.class);

	@Inject
	private MessageItemFactory factory;
	@Inject
	private Service service;

	@Event
	public void initList(AppStartEvent event)
	{
		random=new Random();
	}

	@Event
	public void addTag(MessageEvent event)
	{
		MessageSource messageSource=event.getMessage().getReply();
		if(messageSource!=null)
		{
			int id=messageSource.getId();
			String imageName=service.findFileNameByMessageID(id);
			if(imageName==null)
				return;
			Message message=event.getMessage();

			String string=message.getCodeStr();
			int rainCodeStartIndex=string.indexOf('<');
			if(rainCodeStartIndex>=0)
			{
				int rainCodeEndIndex=string.lastIndexOf(">");
				string=string.substring(rainCodeEndIndex+1);
			}
			string=string.trim();
			if(string.startsWith("添加标签"))
			{
				string=string.substring(4);
				string=string.trim();
				if(string.isEmpty())
					return;
				String[] tags=string.split(" ");
				service.addTag2Image(imageName,tags);
				service.record(event.getSender().getId(),imageName,true,tags);
				if(event instanceof GroupMessageEvent)
					((GroupMessageEvent) event).getGroup().sendMessage("添加成功");
				else
					event.getSender().sendMessage("添加成功");
			}
		}
	}

	@Before
	public void getSender(BotActionContext actionContext,Contact qq)
	{
		actionContext.set("requester",actionContext.getSource());
	}

	@Action(".image")
	public Message image(Contact requester) throws IOException
	{
		return image(requester,null);
	}

	@Action(".image {tag}")
	public Message image(Contact requester,String tag) throws IOException
	{
		ArrayList<File> imageList=service.getImageByTagList(tag);
		if(imageList.size()==0)
			return new Message().plus("你选择的筛选条件不存在图片");
		else
		{
			File file=imageList.get(random.nextInt(imageList.size()));
			Message message=new Message().plus(factory.imageByFile(file));
			MessageSource messageSource=requester.sendMessage(message);
			int id=messageSource.getId();
			if (id!=0)
			{
				logger.debug("id:"+id+"\tfilename:"+file.getName());
				service.putMessageIDAndFileName(id, file.getName());
			}
			return null;
		}
	}
}
