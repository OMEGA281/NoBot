package com.nobot.plugin.girlFriend;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.JobCenter;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.IceCreamQAQ.Yu.job.JobManager;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.controller.BotActionContext;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import com.icecreamqaq.yuq.message.MessageLineQ;
import com.nobot.plugin.girlFriend.entity.Girl;
import com.nobot.plugin.girlFriend.entity.Master;
import com.nobot.plugin.girlFriend.service.ImageGenerationService;
import com.nobot.plugin.girlFriend.service.Service;
import com.nobot.system.annotation.CreateDir;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.var;
import net.coobird.thumbnailator.Thumbnails;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@GroupController
@JobCenter
@CreateDir(GirlPool.GIRL_POOL)
public class Controller
{
	private class Work
	{
		@Getter
		private long startTime;
		@Getter
		private long duration;
		private Runnable onEndRunnable;
		private Thread thread;
		@Getter
		private boolean end;
		public Work(Runnable runnable,long duration)
		{
			this.duration=duration;
			startTime=System.currentTimeMillis();
			onEndRunnable=runnable;
			thread=new Thread(new Runnable()
			{
				public boolean flag;
				@Override
				public void run()
				{
					flag=true;
					try
					{
						Thread.sleep(duration);
					}
					catch (InterruptedException e)
					{
						flag=false;
						end=true;
					}
					if(flag)
					{
						new Thread(onEndRunnable).start();
						end=true;
					}
				}
			});
			thread.start();
			end=false;
		}

		public long getEndTime()
		{
			return startTime+duration;
		}
		public long getReleaseTime()
		{
			return duration-(System.currentTimeMillis()-startTime);
		}

		public void stop()
		{
			thread.interrupt();
		}
	}

	private Map<Long,Map<Long,Work>> map=new HashMap<>();
	private Random random=new Random();
	@Inject
	private Service service;

	@Inject
	private MessageItemFactory factory;

	@Inject
	private ImageGenerationService imageGenerationService;

	@Before(except = {"open","simulateDrawGirl","groupWifeStates"})
	public void getInfo(Member qq, BotActionContext actionContext)
	{
		Master master= service.getMaster(qq.getGroup().getId(), qq.getId());
		if(master==null)
			throw new Message().plus("你尚未开户 输入\"开户\"来开户").toThrowable();
	}

	@Before(except = {"simulateDrawGirl","groupWifeStates","myInfo","market","findWife","stopWork","GMSendMoney"})
	public void isWorking(long group,long qq, BotActionContext actionContext)
	{
		if(map.containsKey(group))
		{
			var m=map.get(group);
			var s=m.get(qq);
			if(s!=null)
				throw new Message().plus("你还在打工中，还有")
						.plus(String.format("%1$3.1d小时",s.getReleaseTime() / (60L * 60))).toThrowable();
		}
	}

	@Action("开户")
	public Message open(Member qq)
	{
		if(service.getMaster(qq.getGroup().getId(), qq.getId())!=null)
			return new Message().plus("你已经在本群开过户了");
		Master master=service.creatNewMaster(qq.getId(), qq.getGroup().getId());
		service.saveMaster(master);
		return new Message().plus("你成功在本群开户，赠送80g，你可以用于\"抽老婆\"等操作");
	}

	@Action("每日签到")
	@Synonym("签到")
	public Message dailySign(long group,long qq)
	{
		if(service.checkTodaySign(group,qq))
			return new Message().plus("你今天已经签到了");
		else
		{
			int reward=35+ random.nextInt(11);
			int gold=service.getGold(group, qq)+reward;
			service.setGold(group, qq, gold);
			service.setActive(group,qq,service.getActive(group,qq)+30);
			service.signToday(group,qq);
			return new Message().plus("签到成功，获得"+reward+"g，现在你有"+gold+"g，要保持哦 长期不签到老婆会离去的");
		}
	}

	@Action("抽老婆")
	public Message drawGirl(long group,long qq)
	{
		int gold=service.getGold(group,qq);
		if(gold<30)
			return new Message().plus("你的金币不足30，可以通过每日签到来获得金币");
		var girl= service.getRandomFreeGirl(group);
		if(girl==null)
			return new Message().plus("哎呀 本群老婆都有了主了");
		service.setGirlMaster(group,qq,girl.getId());

		service.setGold(group,qq,gold-30);
		service.addDrawTime(group,qq,1);
		return new Message().plus("恭喜你获得了"+girl.getName()+"\r\n")
				.plus(factory.imageByFile(service.getGirlImage(girl.getName())).plus("要好好对待她哦"));
	}

	@Action("模拟抽老婆")
	public Message simulateDrawGirl(Member qq)
	{
		var girl= service.getRandomFreeGirl(qq.getGroup().getId());
		if(girl==null)
			return new Message().plus("哎呀 本群老婆都有了主了");
		return new Message().plus("恭喜你获得了"+girl.getName()+"\r\n")
				.plus(factory.imageByFile(service.getGirlImage(girl.getName())).plus("要好好对待她哦"));
	}

	@Action("我的状态")
	public void myInfo(Member qq) throws IOException
	{
		Master master= service.getMaster(qq.getGroup().getId(), qq.getId());
		Message message=new Message();
		message.plus("金币："+master.getGold()+"\r\n");
		message.plus("亲密度："+master.getActive()+"\r\n");
		message.plus("我的老婆："+"\r\n");
		Map<String, File> map=new HashMap<>();
		for (Girl girl:master.getGirlList())
		{
			File file=service.getGirlImage(girl.getName());
			map.put(girl.getName(),file);
		}
		List<File> image= imageGenerationService.makeImage(map);
		if(image==null||image.isEmpty())
		{
			qq.getGroup().sendMessage(message);
			return;
		}
		for (File file : image)
			message.plus(factory.imageByFile(file));
		qq.getGroup().sendMessage(message);
	}

	@Action("卖老婆 {name} {num}")
	public Message saleWife(long group,long qq, String name, String num)
	{
		int gold=Integer.parseInt(num);
		boolean b=service.saleWife(group, qq,name.replaceAll("_"," "), gold);
		if(b)
			return new Message().plus("挂载出售了哦，可以使用\"撤回出售 {名字}\"来撤回");
		else
			return new Message().plus("你在想什么呢？");
	}

	@Action("撤回出售 {name}")
	public Message saleWife(Member qq, String name)
	{
		boolean b=service.saleWife(qq.getGroup().getId(), qq.getId(),
				name.replaceAll("_"," "), -1);
		if(b)
			return new Message().plus("撤回出售了");
		else
			return new Message().plus("你在想什么呢？");
	}

	@Action("市场")
	public void market(Group group,Member qq) throws IOException
	{
		int gold=service.getMaster(group.getId(), qq.getId()).getGold();
		Map<String,Integer> map=service.listForSaleGirl(group.getId());
		MessageLineQ messageLineQ=new MessageLineQ(new Message());
		messageLineQ.text(qq.getName()).text("有"+gold).text("g\r\n市场老婆如下：");
		Map<String,File> stringFileMap=new HashMap<>();
		for (Map.Entry<String,Integer> entry:map.entrySet())
		{
			File file=service.getGirlImage(entry.getKey());
			stringFileMap.put(entry.getKey()+"[售价:"+entry.getValue()+"]",file);
		}
		List<File> image= imageGenerationService.makeImage(stringFileMap);
		if(image==null||image.isEmpty())
		{
			group.sendMessage(messageLineQ);
			return;
		}
		for (File file : image)
			messageLineQ.imageByFile(file);
		qq.getGroup().sendMessage(messageLineQ);
	}

	@Action("买老婆 {name}")
	public Message buyWife(long group,long qq,String name)
	{
		name=name.replaceAll("_"," ");
		Map<String,Integer> map=service.listForSaleGirl(group);
		if(!map.containsKey(name))
			return new Message().plus("没有人售卖她哦");
		int gold=map.get(name);
		if(service.getGold(group,qq)<gold)
			return new Message().plus("你的金钱不足");
		service.addGold(group,service.findWife(group,name).getMaster().getUserNum(),gold);
		service.addSaleTime(group,qq,1);
		service.addGold(group,qq,-gold);
		service.addBuyTime(group,qq,1);
		service.setWifeByName(qq,group,name);
		return new Message().plus("恭喜你获得了"+name+"\r\n")
				.plus(factory.imageByFile(service.getGirlImage(name)).plus("要好好对待她哦"));
	}

	@Action("转账 {at} {t_num}")
	public Message sendMoney(long group,long qq,long at,String t_num)
	{
		int num=Integer.parseInt(t_num);
		if(num<=0)
			return new Message().plus("有这样的想法是不好的");
		Master me=service.getMaster(group,qq);
		if(num>me.getGold())
			return new Message().plus("你没有这么多钱");
		service.addGold(group,qq,-num);
		service.addGold(group,at,num);
		return new Message().plus("成功转账"+num);
	}
	@Action("分解 {name}")
	public Message decompose(long group,long qq,String name)
	{
		name=name.replaceAll("_"," ");
		Master master= service.getMaster(group,qq);
		boolean exist=false;
		for(Girl girl:master.getGirlList())
		{
			if(girl.getName().equals(name))
			{
				exist=true;
				break;
			}
		}
		if(!exist)
			return new Message().plus("你没有"+name);
		service.setWifeFree(qq,group,name);
		int gold=5+ random.nextInt(11);
		service.addGold(group,qq,gold);
		service.addDecomposeTime(group,qq,1);
		return new Message().plus("你残忍的分解了"+name+"获得了"+gold+"金币");
	}
	@Action("查老婆 {name}")
	public Message findWife(Member qq,String name)
	{
		name=name.replaceAll("_"," ");
		Girl girl=service.findWife(qq.getGroup().getId(),name);
		if(girl==null)
		{
			File file=service.getGirlImage(name);
			if (file==null)
				return new Message().plus("不存在这个老婆哦");
			else
				return new Message().plus(factory.imageByFile(file)).plus(name+"还没有主人");
		}
		Master master=girl.getMaster();
		if(master==null)
			return new Message().plus(factory.imageByFile(service.getGirlImage(name))).plus(name+"还没有主人");
		else
			return new Message().plus(factory.imageByFile(service.getGirlImage(name)))
					.plus(name+"的主人是").plus(qq.getGroup().get(master.getUserNum()).getName())
					.plus("（").plus(Long.toString(master.getUserNum())).plus("）");
	}
	@Action("群老婆状态")
	public Message groupWifeStates(long group)
	{
		return new Message().plus("共有"+service.listGirl().size()+"位老婆")
				.plus("其中"+service.countGroupWife(group)+"位有了主人");
	}

	@Action("打工{num}小时")
	public Message work(Group group ,long qq,String num)
	{
		Map<Long,Work> m;
		if(map.containsKey(group.getId()))
		{
			m=map.get(group.getId());
			var s=m.get(qq);
			if(s!=null)
				return null;
		}
		int time=Integer.parseInt(num);
		if(time<=0||time>24)
			return null;
		Work token=new Work(() -> {
			long gold=0L;
			for (int i=0;i<time;i++)
			{
				gold=gold+(random.nextInt(3)+random.nextInt(3)+2)*(time/10L+1);
			}
			int love=4*time;
			service.addGold(group.getId(),qq,(int)gold);
			service.addActive(group.getId(),qq,love);
			var message= new Message().plus(factory.at(qq)).plus("工作完毕，获得")
					.plus(String.valueOf((int)gold)).plus("金币，失去").plus(String.valueOf(love)).plus("点亲密");
			var groupMap=map.get(group.getId());
			if(groupMap!=null)
				groupMap.remove(qq);
			group.sendMessage(message);
		},time*60*60*1000);
		var tokens = map.computeIfAbsent(group.getId(), k -> new HashMap<>());
		tokens.put(qq,token);
		map.put(group.getId(),tokens);
		return new Message().plus("开始工作了，工作过程中其他活动无法进行\r\n\"取消工作\"可以取消 但是没有收益\r\n（如果骰主关机了那就会被强制停止无收益）");
	}

	@Action("取消工作")
	public Message stopWork(long group,long qq)
	{
		if(map.containsKey(group))
		{
			var m=map.get(group);
			var s=m.get(qq);
			if(s==null)
				return null;
			else
			{
				s.stop();
				m.remove(qq);
			}
		}
		else
			return null;
		return new Message().plus("你取消了工作");
	}

	@Action("!GM金币给予 {at} {gold}")
	public Message GMSendMoney(long at,int gold,long SOPNum,Member qq)
	{
		if(SOPNum!=qq.getId())
			return null;
		if(at==-1)
			for (long member:qq.getGroup().getMembers().keySet())
				service.addGold(member,qq.getGroup().getId(),gold);
		service.addGold(at,qq.getGroup().getId(),gold);
		return new Message().plus("给予").plus(at==-1?"全体":Long.toString(at))
				.plus(Integer.toString(gold)).plus("金币");
	}
}
