package com.nobot.plugin;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.icecreamqaq.yuq.event.GroupInviteEvent;
import com.icecreamqaq.yuq.event.NewFriendRequestEvent;

@EventListener
public class AddGroupAndFriend
{
	@Event
	public void friendRequest(NewFriendRequestEvent event)
	{
		event.setAccept(true);
		event.setCancel(true);
	}

	@Event
	public void groupRequest(GroupInviteEvent event)
	{
		event.setAccept(true);
		event.setCancel(true);
	}
}
