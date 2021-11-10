package com.nobot.plugin.girlFriend;

import com.IceCreamQAQ.Yu.annotation.*;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.event.GroupMessageEvent;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import com.nobot.plugin.girlFriend.entity.Master;
import com.nobot.plugin.girlFriend.service.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


@GroupController
@EventListener
public class BlackJack
{
	private enum MatchState
	{WAIT, ONGOING_INITIATOR,ONGOING_OPPONENT,END}
	class Match
	{
		Random random=new Random();
		long groupNum,initiator,opponent;
		MatchState matchState;
		int wager;
		boolean initiatorStop,opponentStop;

		List<Integer> cardPool=new ArrayList<>();
		List<Integer> initiatorCard=new ArrayList<>();
		List<Integer> opponentCard=new ArrayList<>();
		Match(long groupNum,long initiator,long opponent,int wager)
		{
			this.groupNum=groupNum;
			this.initiator=initiator;
			this.opponent=opponent;
			this.wager=wager;
			initiatorStop=false;opponentStop=false;
			matchState= MatchState.WAIT;
			for (int i=1;i<14;i++)
				for (int x=0;x<4;x++)
					cardPool.add(i);
			Thread waitThread=new Thread(() -> {
				try
				{
					Thread.sleep(1000*60);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				if(matchState== MatchState.WAIT)
					map.remove(groupNum);
			});
			waitThread.start();
			Thread stopThread=new Thread(() -> {
				try
				{
					Thread.sleep(1000*60*5);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				if(matchState== MatchState.END)
					map.remove(groupNum);
			});
			stopThread.start();
		}

		public int initiatorDraw()
		{
			Integer integer=cardPool.remove(random.nextInt(cardPool.size()));
			initiatorCard.add(integer);
			return initiatorSum();
		}

		public int initiatorSum()
		{
			int sum=0;
			for (int i:initiatorCard)
				sum+=i;
			return sum;
		}

		public int opponentDraw()
		{
			Integer integer=cardPool.remove(random.nextInt(cardPool.size()));
			opponentCard.add(integer);
			return opponentSum();
		}

		public int opponentSum()
		{
			int sum=0;
			for (int i:opponentCard)
				sum+=i;
			return sum;
		}

		public void end()
		{
			map.remove(groupNum);
			matchState= MatchState.END;
		}
	}

	@Inject
	private Service service;
	@Inject
	private MessageItemFactory factory;

	private Map<Long,Match> map=new ConcurrentHashMap<>();

	@Action("21点 {at} {s_gold}g")
	public Message start(Member qq, Member at,String s_gold, Group group)
	{
		int gold=Integer.parseInt(s_gold);
		if(qq.getId()==at.getId())
			return new Message().plus("你自己跟自己玩？");
		if(gold>200)
			return new Message().plus("赌注太大了吧 还是200以下吧");
		if(gold<0)
			return new Message().plus("这个赌注很有意思 但是还请正常点");
		Master initiator= service.getMaster(group.getId(), qq.getId());
		Master opponent= service.getMaster(group.getId(), at.getId());
		if(initiator==null)
			return new Message().plus("你没开户啊");
		if(opponent==null)
			return new Message().plus("对方没开户啊");
		if(initiator.getGold()<gold)
			return new Message().plus("你的金钱不够啊");
		if(opponent.getGold()<gold)
			return new Message().plus("对方的金钱不够啊");

		if(map.containsKey(group.getId()))
			return new Message().plus("本群已有等待或进行中的比赛了");
		Match match=new Match(group.getId(),qq.getId(),at.getId(),gold);
		map.put(group.getId(),match);
		return new Message().plus(factory.at(at)).plus(qq.getNameCard())
				.plus("邀请你来一场赌注为"+gold+"的21点对决，请在1分钟内发送\"同意\"，过时过期");
	}

	@Before(/*only = {"drawCard","stopDrawCard"}*/except = {"start","deal"})
	public Match check(Member qq,Group group)
	{
		Match match=map.get(group.getId());
		if(match==null)
			throw new DoNone();
		if(!((match.opponent== qq.getId()&&match.matchState== MatchState.ONGOING_OPPONENT)
				||(match.initiator== qq.getId()&&match.matchState== MatchState.ONGOING_INITIATOR)))
			throw new DoNone();
		return match;
	}

	@Event
	public void deal(GroupMessageEvent event)
	{
		if (!event.getMessage().getCodeStr().equals("同意"))
			return;
		Group group= event.getGroup();
		Member qq= event.getSender();
		Match match=map.get(group.getId());
		if(match==null)
			throw new DoNone();
		if(match.opponent!= qq.getId())
			throw new DoNone();
		match.matchState= MatchState.ONGOING_INITIATOR;
		group.sendMessage(new Message().plus("对决开始，")
				.plus(factory.at(match.initiator)).plus("的回合\r\n发送\"加牌\"或\"停止\"来控制"));
	}

	@Action("加牌")
	public Message drawCard(Match match)
	{
		Message message=new Message();
		if(match.matchState== MatchState.ONGOING_INITIATOR)
		{
			message.plus(factory.at(match.initiator)).plus("你的手牌为：");
			int i=match.initiatorDraw();
			for(int x:match.initiatorCard)
				message.plus(x+",");
			message.plus("总值："+i);
			if(i==21)
			{
				win(match.initiator, match.opponent, match.groupNum, match.wager);
				message.plus("你赢了！");
				match.end();
				return message;
			}
			if(i>21)
			{
				win(match.opponent, match.initiator, match.groupNum, match.wager);
				message.plus("你输了！");
				match.end();
				return message;
			}
			if(!match.opponentStop)
			{
				match.matchState = MatchState.ONGOING_OPPONENT;
				message.plus("\r\n下回合").plus(factory.at(match.opponent));
			}
			else
				message.plus("\r\n下回合 你自己");

		}
		else
		{
			message.plus(factory.at(match.opponent)).plus("你的手牌为：");
			int i=match.opponentDraw();
			for(int x:match.opponentCard)
				message.plus(x+",");
			message.plus("总值："+i);
			if(i==21)
			{
				win(match.opponent, match.initiator, match.groupNum, match.wager);
				message.plus("你赢了！");
				match.end();
				return message;
			}
			if(i>21)
			{
				win(match.initiator, match.opponent, match.groupNum, match.wager);
				message.plus("你输了！");
				match.end();
				return message;
			}
			if(!match.initiatorStop)
			{
				match.matchState = MatchState.ONGOING_INITIATOR;
				message.plus("\r\n下回合").plus(factory.at(match.initiator));
			}
			else
				message.plus("\r\n下回合 你自己");

		}
		return message;
	}

	@Action("停止")
	public Message stopDrawCard(Match match)
	{
		Message message=new Message();
		if (match.matchState == MatchState.ONGOING_INITIATOR)
			match.initiatorStop = true;
		else
			match.opponentStop = true;
		if(match.opponentStop&& match.initiatorStop)
		{

			message.plus(factory.at(match.initiator)).plus("的手牌为：");
			int q=match.initiatorSum();
			for(int x:match.initiatorCard)
				message.plus(x+",");
			message.plus("总值："+q+"\r\n");

			message.plus(factory.at(match.opponent)).plus("的手牌为：");
			int p=match.opponentSum();
			for(int x:match.opponentCard)
				message.plus(x+",");
			message.plus("总值："+p+"\r\n");

			if(match.initiatorSum()>match.opponentSum())
			{
				message.plus(factory.at(match.initiator)).plus("你赢了!");
				win(match.initiator, match.opponent, match.groupNum, match.wager);
			}
			else if(match.initiatorSum()<match.opponentSum())
			{
				message.plus(factory.at(match.opponent)).plus("你赢了!");
				win(match.opponent, match.initiator, match.groupNum, match.wager);
			}
			else
			{
				message.plus(factory.at(match.opponent)).plus("平局");
			}
			match.end();
			return message;
		}
		match.matchState=match.matchState== MatchState.ONGOING_INITIATOR? MatchState.ONGOING_OPPONENT: MatchState.ONGOING_INITIATOR;
		message.plus("你停止了抽牌");
		message.plus("现在你的手牌为：");
		int q=match.matchState!= MatchState.ONGOING_INITIATOR?match.initiatorSum(): match.opponentSum();
		for(int x:(match.matchState!= MatchState.ONGOING_INITIATOR?match.initiatorCard: match.opponentCard))
			message.plus(x+",");
		message.plus("总值："+q+"\r\n");
		message.plus("下回合").plus(factory.at(match.matchState== MatchState.ONGOING_INITIATOR?match.initiator: match.opponent));
		return message;
	}

	private void win(long winner,long loser,long group,int gold)
	{
		if(gold==0)
			return;
		service.addGold(winner,group,gold);
		service.addGold(loser,group,-gold);
	}
	@Catch(error = NumberFormatException.class)
	public void numError(Group group)
	{
		throw new Message().plus("你输入的数字错误了").toThrowable();
	}
}
