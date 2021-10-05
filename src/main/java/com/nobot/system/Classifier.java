package com.nobot.system;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.icecreamqaq.yuq.event.GroupMessageEvent;
import com.icecreamqaq.yuq.message.At;
import com.icecreamqaq.yuq.message.MessageItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TODO:尚未确认是否能重新匹配路由，未启用功能
 */
@EventListener
public class Classifier
{
	@Event(weight = Event.Weight.high)
	public void classifier(GroupMessageEvent event)
	{
		ArrayList<At> list=new ArrayList<>();
		Iterator<MessageItem> iterator= event.getMessage().getPath().iterator();
		while (iterator.hasNext())
		{
			MessageItem messageItem=iterator.next();
			if(messageItem instanceof At)
			{
				list.add((At) messageItem);
				iterator.remove();
			}
			else
				break;
		}
		if (list.isEmpty())
			return;
		boolean cancel=true;
		for (At at:list)
		{
			if(at.getUser()==-1)
				break;
			if(at.getUser()== BotInfo.myQQNum)
			{
				cancel = false;
				break;
			}
		}
		event.setCancel(cancel);
	}
}
