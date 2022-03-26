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
import com.nobot.system.annotation.UnzipFile;
import com.nobot.system.stringHelper.DefaultStringFile;
import com.nobot.system.stringHelper.GetString;
import net.sourceforge.jeval.Evaluator;

import javax.inject.Inject;
import java.util.Random;

@GroupController
@PrivateController
@EventListener
@UnzipFile(name="string/DiceString.properties",aim = "./string/DiceString.properties")
@DefaultStringFile(value = "./String/DiceString.properties",info = "这是检定和掷骰的类")
public class Controller implements SpecialSymbol
{
	@Inject
	private COCCardService service;
	@Inject
	private GetString getString;

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
	public String r(Group group,Contact qq,String currentName) throws ExpressionException
	{
		return r("d",group,qq,currentName);
	}

	@Action(".r{d}")
	@Synonym({"。r{d}",".R{d}","。R{d}","。r {d}",".R {d}","。R {d}",".r {d}"})
	public String r(String d,Group group,Contact qq,String currentName) throws ExpressionException
	{
		StringBuilder builder=new StringBuilder(d.toUpperCase());
		boolean isH;
		if(builder.charAt(0)==symbol_h)
		{
			isH=true;
			if(builder.length()!=1)
				builder.deleteCharAt(0);
			else
				builder=new StringBuilder().append(symbol_d);
		}
		else isH=false;

		int indexOfPound=builder.indexOf("#");
		int repeatTime;
		if(indexOfPound<=0)
			repeatTime=1;
		else
			repeatTime=Integer.parseInt(builder.substring(0,indexOfPound));
		builder.delete(0,indexOfPound+1);

		Expression expression;

		if(builder.charAt(0)==symbol_a)
		{
			if(builder.length()!=1)
				builder.deleteCharAt(0);
			else throw new ExpressionException(d,"判定表达式为空",qq.getId(),group.getId());
			expression=new VerificationExpression(
					builder.toString(),random,service.getSkillMap(qq.getId(),group.getId()),group.getId());
		}
		else
			expression=new NumberExpression(evaluator,random,builder.toString());

		StringBuilder collectionString=new StringBuilder();
		String resourceString = null,showString=null,resultString=null;
		for (int i=0;i<repeatTime;i++)
		{
			expression.calculation();
			resourceString=expression.getResourceExpression();
			showString=expression.getShowExpression();
			resultString= String.valueOf(expression.getResult());
			collectionString.append(resourceString).append('=')
					.append(showString).append('=')
					.append(resultString).append('\n');
		}
		collectionString.deleteCharAt(collectionString.length()-1);

		if(isH)
		{
			qq.sendMessage(getString.formatString(
					getString.addressing(repeatTime>1?"r.private.repeatedly.private":"r.private.single.private"),
					qq.getName(),
					group==null?"":group.getName(),
					resourceString,
					showString,
					resultString,
					collectionString.toString()));
			return getString.formatString(
					getString.addressing(repeatTime>1?"r.private.repeatedly.group":"r.private.single.group"),
					qq.getName(),
					group==null?"":group.getName(),
					resourceString,
					showString,
					resultString,
					collectionString.toString());
		}
		else
		{
			return getString.formatString(
					getString.addressing(repeatTime>1?"r.public.repeatedly":"r.public.single"),
					qq.getName(),
					group==null?"":group.getName(),
					resourceString,
					showString,
					resultString,
					collectionString.toString()
			);
		}
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
	@Catch(error = ExpressionException.class)
	public String wrongExpression(ExpressionException exception)
	{
		return getString.formatString(
				getString.formatString("r.error"),
				String.valueOf(exception.getUserNum()),
				String.valueOf(exception.getGroupNum()),
				exception.getExpression(),
				"","","",
				exception.getInfo());
	}
}
