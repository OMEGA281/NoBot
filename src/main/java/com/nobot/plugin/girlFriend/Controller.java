package com.nobot.plugin.girlFriend;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.JobCenter;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.icecreamqaq.yuq.YuQ;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.controller.BotActionContext;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import com.nobot.plugin.girlFriend.entity.Girl;
import com.nobot.plugin.girlFriend.entity.Master;
import com.nobot.plugin.girlFriend.service.Service;

import javax.inject.Inject;
import java.util.Map;
import java.util.Random;

@GroupController
@JobCenter
public class Controller
{
	Random random=new Random();
	@Inject
	private Service service;

	@Inject
	private MessageItemFactory factory;

	@Inject
	private YuQ yuQ;

	@Before(except = {"open","simulateDrawGirl "})
	public void getInfo(Member qq, BotActionContext actionContext)
	{
		Master master= service.getMaster(qq.getId(),qq.getGroup().getId());
		if(master==null)
			throw new Message().plus("你尚未开户 输入\"开户\"来开户").toThrowable();
	}

	@Action("开户")
	public Message open(Member qq)
	{
		if(service.getMaster(qq.getId(),qq.getGroup().getId())!=null)
			return new Message().plus("你已经在本群开过户了");
		Master master=service.creatNewMaster(qq.getId(), qq.getGroup().getId());
		service.saveMaster(master);
		return new Message().plus("你成功在本群开户，赠送80g，你可以用于\"抽老婆\"等操作");
	}

	@Action("每日签到")
	@Synonym("签到")
	public Message dailySign(Member qq)
	{
		Master master= service.getMaster(qq.getId(),qq.getGroup().getId());
		if(master.getLastSignTime()>=service.getCurrentTime())
			return new Message().plus("你今天已经签到了");
		int g=35+ random.nextInt(11);
		master.setGold(master.getGold()+g);
		master.setActive(master.getActive()+30);
		master.setLastSignTime(service.getCurrentTime());
		service.saveMaster(master);
		return new Message().plus("签到成功，获得"+g+"g，现在你有"+master.getGold()+"g，要保持哦 长期不签到老婆会离去的");
	}

	@Action("抽老婆")
	public Message drawGirl(Member qq)
	{
		Master master= service.getMaster(qq.getId(),qq.getGroup().getId());
		if(master.getGold()<30)
			return new Message().plus("你的金币不足30，可以通过每日签到来获得金币");
		Girl girl= service.getRandomFreeGirl(qq.getGroup().getId(), qq.getId());
		if(girl==null)
			return new Message().plus("哎呀 本群老婆都有了主了");
		master.setGold(master.getGold()-30);
		service.saveMaster(master);
		return new Message().plus("恭喜你获得了"+girl.getName()+"\r\n")
				.plus(factory.imageByFile(service.getGirlImage(girl.getName())).plus("要好好对待她哦"));
	}

	@Action("模拟抽老婆")
	public Message simulateDrawGirl(Member qq)
	{
		Girl girl= service.simulateGetRandomFreeGirl(qq.getGroup().getId());
		if(girl==null)
			return new Message().plus("哎呀 本群老婆都有了主了");
		return new Message().plus("恭喜你获得了"+girl.getName()+"\r\n")
				.plus(factory.imageByFile(service.getGirlImage(girl.getName())).plus("要好好对待她哦"));
	}

	@Action("我的状态")
	public Message myInfo(Member qq)
	{
		Master master= service.getMaster(qq.getId(),qq.getGroup().getId());
		Message message=new Message();
		message.plus("金币："+master.getGold()+"\r\n");
		message.plus("亲密度："+master.getActive()+"\r\n");
		message.plus("我的老婆："+"\r\n");
		boolean showImage=true;
		if(master.getGirlList().size()>5)
			showImage=false;
		for (Girl girl:master.getGirlList())
		{
			message.plus(girl.getName());
			if(girl.getSaleNum()>=0)
				message.plus("（代售："+girl.getSaleNum()+"）");
			if (showImage)
				message.plus(factory.imageByFile(service.getGirlImage(girl.getName())));
			message.plus("\r\n");
		}
		return message;
	}

	@Action("卖老婆 {name} {num}")
	public Message saleWife(Member qq, String name, String num)
	{
		int gold=Integer.parseInt(num);
		boolean b=service.saleWife(qq.getId(),qq.getGroup().getId(),name, gold);
		if(b)
			return new Message().plus("挂载出售了哦，可以使用\"撤回出售 {名字}\"来撤回");
		else
			return new Message().plus("你在想什么呢？");
	}

	@Action("撤回出售 {name}")
	public Message saleWife(Member qq, String name)
	{
		boolean b=service.saleWife(qq.getId(),qq.getGroup().getId(),name, -1);
		if(b)
			return new Message().plus("撤回出售了");
		else
			return new Message().plus("你在想什么呢？");
	}

	@Action("市场")
	public Message market(Group group)
	{
		Map<String,Integer> map=service.listForSaleGirl(group.getId());
		StringBuilder builder=new StringBuilder("目前有：");
		for (Map.Entry<String,Integer> entry:map.entrySet())
		{
			builder.append(entry.getKey()+"\t"+entry.getValue()+"\r\n");
		}
		return new Message().plus(builder.toString());
	}

	@Action("买老婆 {name}")
	public Message buyWife(Member qq,String name)
	{
		Map<String,Integer> map=service.listForSaleGirl(qq.getGroup().getId());
		if(!map.containsKey(name))
			return new Message().plus("没有人售卖她哦");
		Master master=service.getMaster(qq.getId(),qq.getGroup().getId());
		int gold=map.get(name);
		if(master.getGold()<gold)
			return new Message().plus("你的金钱不足");
		service.addGold(service.findWife(qq.getGroup().getId(),name).getMaster().getUserNum(),qq.getGroup().getId(),gold);
		service.addGold(qq.getId(),qq.getGroup().getId(),-gold);
		service.setWifeByName(qq.getId(),qq.getGroup().getId(),name);
		return new Message().plus("恭喜你获得了"+name+"\r\n")
				.plus(factory.imageByFile(service.getGirlImage(name)).plus("要好好对待她哦"));
	}

	@Action("转账 {at} {t_num}")
	public Message sendMoney(Member qq,Member at,String t_num)
	{
		int num=Integer.parseInt(t_num);
		if(num<=0)
			return new Message().plus("有这样的想法是不好的");
		Master me=service.getMaster(qq.getId(), qq.getGroup().getId());
		if(num>me.getGold())
			return new Message().plus("你没有这么多钱");
		Master sb=service.getMaster(at.getId(), at.getGroup().getId());
		service.addGold(qq.getId(),qq.getGroup().getId(),-num);
		service.addGold(at.getId(),at.getGroup().getId(),num);
		return new Message().plus("成功转账"+num);
	}
	@Action("分解 {name}")
	public Message decompose(Member qq,String name)
	{
		Master master= service.getMaster(qq.getId(), qq.getGroup().getId());
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
		service.setWifeFree(qq.getId(),qq.getGroup().getId(),name);
		int gold=5+ random.nextInt(11);
		service.addGold(qq.getId(), qq.getGroup().getId(),gold);
		return new Message().plus("你残忍的分解了"+name+"获得了"+gold+"金币");
	}
}
