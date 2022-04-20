package com.nobot.plugin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.controller.BotActionContext;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Friend;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageLineQ;
import com.nobot.tool.MD5Utils;
import com.nobot.tool.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@GroupController
@PrivateController
public class TodayLuck
{
	private final SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd");
	@Before
	public void getName(BotActionContext actionContext, Contact qq)
	{
		if(actionContext.getSource() instanceof Member)
		{
			actionContext.set("name",qq.getName());
			return;
		}
		if(qq instanceof Friend)
		{
			actionContext.set("name",qq.getName());
			return;
		}
		String name=((Member) qq).getNameCard();
		if(name.isEmpty())
		{
			name=qq.getName();
		}
		actionContext.set("name",name);
	}

	@Action("今日人品")
	public MessageLineQ getTodayLuck(long qq,String name)
	{
		MessageLineQ messageLineQ=new Message().lineQ();
		long nowTime=System.currentTimeMillis();
		Date today=new Date(nowTime);
		Date yesterday;
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(today);
		calendar.add(Calendar.DATE,-1);
		yesterday=calendar.getTime();
		String todayInfo= TimeUtils.getFormatData(simpleDateFormat)+name;
		String todayResult=MD5Utils.getMD5(todayInfo,10);

		int index=Integer.parseInt(todayResult.substring(1,2));
		int todayLuck=Integer.parseInt(todayResult.substring(index+2,index+4));
		String yesterdayInfo=simpleDateFormat.format(yesterday);
		String yesterdayResult=MD5Utils.getMD5(yesterdayInfo,10);

		index=Integer.parseInt(todayResult.substring(1,2));
		int yesterdayLuck=Integer.parseInt(yesterdayResult.substring(index+2,index+4));
		int different=Math.round((todayLuck-yesterdayLuck)/20f);
		messageLineQ.text("今天"+name+"的运气是"+todayLuck+"\r\n");
		switch (different)
		{
			case 5:
				messageLineQ.text("时来运转！运气的惊天逆转，从昨日低谷瞬间冲顶。昨日的枷锁今天一甩而开吧。");
				break;
			case 4:
				messageLineQ.text("幸运upup！相比昨天可是地下天上。全新的生活等着你！");
				break;
			case 3:
				if(yesterdayLuck<30)
					messageLineQ.text("运气大幅提升，想必昨日的困难今天将会结束。");
				else
					messageLineQ.text("很棒哦！一部登顶的感觉很爽，就这这个劲头继续吧。");
				break;
			case 2:
				if (yesterdayLuck<30)
					messageLineQ.text("虽然昨天不太舒服，不过今天有了很大的起色哦。");
				else
					messageLineQ.text("哇 今天比昨天要顺利的多呢。");
				break;
			case 1:
				if (yesterdayLuck<30)
					messageLineQ.text("缓慢的进展也是好的。");
				else if(yesterdayLuck<60)
					messageLineQ.text("运气在上升哦！");
				else
					messageLineQ.text("更进一步！势头很棒，就着这种势头前进吧。");
				break;
			case 0:
				if (yesterdayLuck<30)
					messageLineQ.text("似乎在低谷徘徊呢，不要气馁，相信自己。");
				else if(yesterdayLuck<60)
					messageLineQ.text("不以物喜，不以己悲，依靠自己的力量。");
				else
					messageLineQ.text("一鼓作气，干柴烈火，趁机多干点自己的宏伟计划吧。");
				break;
			case -1:
				if (todayLuck<30)
					messageLineQ.text("不要太难过了……运由天注定，命靠自己打拼。");
				else if(todayLuck<60)
					messageLineQ.text("微微的下降不要担心，相信运气会跟着你的努力回来的。");
				else
					messageLineQ.text("盛极必衰，建议韬光养晦。");
				break;
			case -2:
				if (todayLuck<30)
					messageLineQ.text("加油啊！运气不会决定一切。");
				else
					messageLineQ.text("运气掉了不少吗？别总是依赖运气啊。");
				break;
			case -3:
				if(todayLuck<30)
					messageLineQ.text("啊 有时确实会有这样的情况嘛，毕竟运气不会久留。");
				else
					messageLineQ.text("还没触底，还没触底，还有机会，还有机会。");
				break;
			case -4:
				messageLineQ.text("不要太难过了啊，事情不会那么糟的。");
				break;
			case -5:
				messageLineQ.text("有一说一 你这样运气过山车很罕见啊，从某种情况来说算不算是一种运气？");
				break;
		}
		messageLineQ.text("\r\n");
		messageLineQ.text("今天好方向：");
		int place= (int) (Integer.parseInt(todayResult.substring(12,13))/2.5);
		switch (place)
		{
			case 0:
				messageLineQ.text("东");
				break;
			case 1:
				messageLineQ.text("西");
				break;
			case 2:
				messageLineQ.text("南");
				break;
			case 3:
				messageLineQ.text("北");
				break;
		}
		messageLineQ.text("\t");
		messageLineQ.text("今天好颜色：");
		int color=Integer.parseInt(todayResult.substring(13,14));
		switch (place)
		{
			case 0:
				messageLineQ.text("红");
				break;
			case 1:
				messageLineQ.text("橙");
				break;
			case 2:
				messageLineQ.text("黄");
				break;
			case 3:
				messageLineQ.text("绿");
				break;
			case 4:
				messageLineQ.text("青");
				break;
			case 5:
				messageLineQ.text("蓝");
				break;
			case 6:
				messageLineQ.text("紫");
				break;
			case 7:
				messageLineQ.text("黑");
				break;
			case 8:
				messageLineQ.text("白");
				break;
			case 9:
				messageLineQ.text("粉");
				break;
		}

		return messageLineQ;
	}
}
