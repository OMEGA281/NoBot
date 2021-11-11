package com.nobot.system;

import com.IceCreamQAQ.Yu.annotation.Cron;
import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.annotation.JobCenter;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.IceCreamQAQ.Yu.job.JobManager;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.event.SendMessageEvent;
import com.icecreamqaq.yuq.message.Message;
import com.nobot.plugin.lor.entity.Game;
import lombok.var;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EventListener
@JobCenter
public class MessageSendTooFastProtector
{
	public static final long statisticalTime=3*1000;
	public static final int messageSendLimit=9;
	public static final int lockTime= 60 * 1000;

	@Inject
	private JobManager jobManager;

	Map<Long,Integer> privateMap=new ConcurrentHashMap<>();
	Map<Long,Integer> groupMap=new ConcurrentHashMap<>();

	List<Long> privateLock=new ArrayList<>();
	List<Long> groupLock=new ArrayList<>();

	private class LockThread implements Runnable
	{
		private List<Long> list;
		private long l;

		@Override
		public void run()
		{
			list.add(l);
			try
			{
				Thread.sleep(lockTime);
			}
			catch (InterruptedException e)
			{
				list.remove(l);
			}
			list.remove(l);
		}

		public LockThread(List<Long> list, long l)
		{
			this.list=list;
			this.l=l;
		}
	}

	@Event
	public void check(SendMessageEvent event)
	{
		var contact=event.getSendTo();
		if(contact instanceof Group)
		{
			if(groupLock.contains(contact.getId()))
			{
				event.cancel=true;
				return;
			}
			var time = addGroupTime(contact.getId());
			if(time>messageSendLimit)
			{
//				contact.sendMessage("在"+statisticalTime/1000+"秒内消息发送事件达到"+time+"次，锁定"+lockTime/1000+"秒");
				lockGroup(contact.getId());
			}
		}
		else
		{
			if(privateLock.contains(contact.getId()))
			{
				event.cancel=true;
				return;
			}
			var time = addPrivateTime(contact.getId());
			if(time>messageSendLimit)
			{
//				contact.sendMessage("在"+statisticalTime/1000+"秒内消息发送事件达到"+time+"次，锁定"+lockTime/1000+"秒");
				lockPrivate(contact.getId());
			}
		}
	}

	private int addPrivateTime(long user)
	{
		return privateMap.merge(user, 1, Integer::sum);
	}
	private int addGroupTime(long group)
	{
		return groupMap.merge(group, 1, Integer::sum);
	}

	private void lockPrivate(long user)
	{
		privateLock.add(user);
		jobManager.registerTimer(() -> privateLock.remove(user), 60 * 1000);
	}
	private void lockGroup(long group)
	{
		groupLock.add(group);
		jobManager.registerTimer(() -> groupLock.remove(group), 60 * 1000);
	}

	@Cron("3s")
	public void cleanList()
	{
		privateMap.clear();
		groupMap.clear();
	}
}
