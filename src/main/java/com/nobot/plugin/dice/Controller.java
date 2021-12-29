package com.nobot.plugin.dice;

import com.IceCreamQAQ.Yu.annotation.*;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.controller.BotActionContext;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Friend;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import com.nobot.plugin.dice.entity.COCCard;
import com.nobot.plugin.dice.expressionAnalyzer.Expression;
import com.nobot.plugin.dice.expressionAnalyzer.ExpressionSorter;
import lombok.var;
import net.sourceforge.jeval.Evaluator;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@GroupController
@PrivateController
@EventListener
public class Controller
{
	@Inject
	private DataService dataService;
	@Inject
	private Dice dice;
	@Inject
	private Verification verification;
	@Inject
	private ExpressionSorter sorter;

	private Evaluator evaluator;
	private Random random;

	@Before
	private void getMessageType(BotActionContext actionContext, Member qq, Friend sender)
	{
		boolean isGroup;
		String currentName;
		if(actionContext.getSource() instanceof Group)
			isGroup = true;
		else
			isGroup=false;
		if(qq!=null)
			currentName=qq.getNameCard();
		else
			currentName=sender.getName();

		actionContext.set("isGroup",isGroup);
		actionContext.set("currentName",currentName);
	}

	@Action(".r")
	public String r()
	{
		return r("d");
	}

	@Action(".r{d}")
	@Synonym({"。r{d}",".R{d}","。R{d}","。r {d}",".R {d}","。R {d}",".r {d}"})
	public String r(String d)
	{
		var expressions=sorter.numCalculation(evaluator,random,d);
		int time=sorter.getRepeatTime(d);
		var sb=new StringBuilder();
		for (int i=0;i<time;i++)
		{
			expressions.calculation();
			sb.append(expressions.getResourceExpression()).append('=')
					.append(expressions.getShowExpression()).append('=')
					.append(expressions.getResult()).append("\n");
		}
		sb.delete(sb.length()-1,sb.length());
		return sb.toString();
	}

	@Action(".rh{d}")
	@Synonym({"。rh{d}",".RH{d}","。RH{d}","。rh {d}",".RH {d}","。RH {d}",".rh {d}"})
	public String rh(Contact qq,boolean isGroup,String d,String currentName)
	{
		if(!isGroup)
		{
			qq.sendMessage(new Message().plus("私聊没有必要暗骰"));
			return null;
		}
		String s=r(d);
		qq.sendMessage(new Message().plus(s));
		return currentName+"进行了一次私骰";
	}

	@Action(".ra {skill}")
	public String ra(long qq,long group,boolean isGroup,String skill,String currentName)
			throws NoSuitableCardException
	{
		COCCard card=dataService.getCard(qq,isGroup?group:0L);
		int i=card.getSkill(skill);
		return verification.getString(verification.judge(verification.getVerificationState(i)),currentName,skill);
	}

	@Action(".ra {skill} {skillNum}")
	public String ra(String skill,int skillNum,String currentName)
			throws NoSuitableCardException
	{
		return verification.getString(
				verification.judge(verification.getVerificationState(skillNum)),currentName,skill);
	}

	@Action(".rap {diceNum} {skill}")
	public String rap(long qq,long group,boolean isGroup,String skill,String currentName)
			throws NoSuitableCardException
	{
		COCCard card=dataService.getCard(qq,isGroup?group:0L);
		int i=card.getSkill(skill);
		return verification.getString(verification.judge(verification.getVerificationState(i)),currentName,skill);
	}


	@Catch(error = NoSuitableCardException.class)
	public String noCard()
	{
		return "未找到人物卡";
	}

	private Map.Entry<String,Integer> getSkillAndLevel(String skill)
	{
		if(skill.startsWith("极难"))
		{
			Map map=new HashMap();
			map.put("a",1);
		}
		return null;
	}

	@Event
	public void init(AppStartEvent event)
	{
		evaluator=new Evaluator();
		random=new Random();
	}

	private
}
