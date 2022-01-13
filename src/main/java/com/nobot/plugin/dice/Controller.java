package com.nobot.plugin.dice;

import com.IceCreamQAQ.Yu.annotation.*;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.controller.BotActionContext;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Friend;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.nobot.plugin.dice.expressionAnalyzer.*;
import com.nobot.plugin.dice.service.COCCardService;
import com.nobot.plugin.dice.service.StringFormatHelper;
import net.sourceforge.jeval.Evaluator;

import javax.inject.Inject;
import java.util.Random;

@GroupController
@PrivateController
@EventListener
public class Controller implements SpecialSymbol
{
	@Inject
	private COCCardService service;
	@Inject
	private StringFormatHelper stringFormatHelper;

	private Evaluator evaluator=new Evaluator();
	private Random random=new Random();

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
	public String r(Group group,Contact qq,String currentName)
	{
		return r("d",group,qq,currentName);
	}

	@Action(".r{d}")
	@Synonym({"。r{d}",".R{d}","。R{d}","。r {d}",".R {d}","。R {d}",".r {d}"})
	public String r(String d,Group group,Contact qq,String currentName)
	{
		StringBuilder builder=new StringBuilder(d.toUpperCase());
		boolean isH;
		if(builder.charAt(0)==symbol_h)
		{
			isH=true;
			builder.deleteCharAt(0);
		}
		else isH=false;
		int time=getRepeat(builder);
		deleteRepeat(builder);
		Expression expression;

		if(builder.charAt(0)==symbol_a)
		{
			builder.deleteCharAt(0);
			expression=new VerificationExpression(
					builder.toString(),random,service.getSkillMap(qq.getId(),group.getId()),group.getId());
		}
		else
			expression=new NumberExpression(evaluator,random,builder.toString());

		StringBuilder test=new StringBuilder();
		for (int i=0;i<time;i++)
		{
			expression.calculation();
			test.append(stringFormatHelper.getFormat(expression, qq.getName())).append('\n');
		}

		if(isH)
		{
			qq.sendMessage(test.toString());
			return qq.getName()+"进行了一次暗骰";
		}
		else
			return test.toString();
	}

	private StringBuilder deleteRepeat(StringBuilder builder)
	{
		int index=builder.indexOf("#");
		if(index>=0)
			builder.delete(0,index+1);
		return builder;
	}

	private int getRepeat(StringBuilder builder)
	{
		int index=builder.indexOf("#");
		if(index>0)
		{
			return Integer.parseInt(builder.substring(0,index));
		}
		else return 1;
	}

	@Catch(error = NumberFormatException.class)
	public String wrongNum()
	{
		return "不识别的数字";
	}
}
