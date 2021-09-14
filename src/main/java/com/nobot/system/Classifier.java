package com.nobot.system;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.icecreamqaq.yuq.event.GroupMessageEvent;
import com.icecreamqaq.yuq.message.At;
import com.icecreamqaq.yuq.message.MessageItem;

import java.util.ArrayList;

/**
 * TODO:尚未确认是否能重新匹配路由，未启用功能
 */
//@EventListener
public class Classifier
{
	@Event
	public void classifier(GroupMessageEvent event)
	{
		ArrayList<At> list=new ArrayList<>();
		for (MessageItem messageItem:event.getMessage().getBody())
		{
			if(messageItem instanceof At)
				list.add((At) messageItem);
			else
				break;
		}
		if (list.isEmpty())
			return;
		boolean cancel=true;
		for (At at:list)
		{
			if(at.getUser()==-1)
			{
				cancel = true;
				break;
			}
			if(at.getUser()==MyInfo.myQQNum)
				cancel = false;
		}
		event.setCancel(cancel);
	}
}
