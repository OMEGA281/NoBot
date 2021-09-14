package com.nobot.system;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.icecreamqaq.yuq.YuQ;
import com.icecreamqaq.yuq.entity.User;
import lombok.Getter;

import javax.inject.Inject;

@EventListener
public class MyInfo
{
	@Inject
	YuQ yuQ;

	public static long myQQNum;
	public static String myName;
	@Event(weight = Event.Weight.high)
	public void getInfo(AppStartEvent event)
	{
		User user=yuQ.getBotInfo();
		myQQNum=user.getId();
		myName= user.getName();
	}
}
