package com.nobot.system;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.icecreamqaq.yuq.YuQ;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.entity.User;
import lombok.Getter;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@EventListener
public class BotInfo
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
	public String getMyGroupName(long group)
	{
		Map<Long,Group> list=yuQ.getGroups();
		if(!list.containsKey(group))
			return "";
		String nameCard=list.get(group).getBot().getNameCard();
		if(nameCard!=null&&!nameCard.isEmpty())
			return nameCard;
		return myName;
	}
	public String getGroupName(long group,long num)
	{
		Map<Long,Group> list=yuQ.getGroups();
		if(!list.containsKey(group))
			return "";
		Map<Long, Member> map=list.get(group).getMembers();
		if(!map.containsKey(num))
			return "";
		String nameCard=map.get(num).getNameCard();
		if(nameCard!=null&&!nameCard.isEmpty())
			return nameCard;
		return map.get(num).getName();
	}
}
