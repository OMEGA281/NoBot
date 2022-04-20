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
import javax.inject.Named;
import java.util.Arrays;
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

	private final Evaluator evaluator=new Evaluator();
	private final Random random=new Random();

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

		if(builder.charAt(0)==symbol_a)
		{
			if(builder.length()!=1)
				builder.deleteCharAt(0);
			else throw new ExpressionException(d,"判定表达式为空",qq.getId(),group.getId());
			VerificationExpression expression=new VerificationExpression(
					builder.toString(),
					random,
					service.getSkillMap(qq.getId(),group==null?0:group.getId()),
					group==null?0:group.getId());

//			FIXME:这里应该加上错误获取，但是从计算层面就没有考虑，后期加上
			expression.calculation();

			String bonusAndPunish;
			if (expression.getBonusOrPunishDiceNum()>0)
				bonusAndPunish="bonus";
			else if (expression.getBonusOrPunishDiceNum()<0)
				bonusAndPunish="punish";
			else
				bonusAndPunish="normal";

			String[] args=new String[14];
			args[0]=group==null?qq.getName():((Member)qq).getNameCard();
			args[1]=group==null?"":group.getName();
			args[2]=expression.getSkillName();
			args[3]= String.valueOf(expression.getSkillNum());
			args[4]= expression.getTrueExpression();
			args[5]=expression.getBonusOrPunishDiceNum()>0?"奖励":expression.getBonusOrPunishDiceNum()==0?"":"惩罚";
			args[6]= String.valueOf(Math.abs(expression.getBonusOrPunishDiceNum()));
			args[7]=linkArray(expression.getExtraDice());
			args[8]="";
			if(expression.getBonusOrPunishDiceNum()>0)
				args[8]= String.valueOf(Arrays.stream(expression.getExtraDice()).min());
			else if(expression.getBonusOrPunishDiceNum()<0)
				args[8]= String.valueOf(Arrays.stream(expression.getExtraDice()).max());
			args[9]= String.valueOf(expression.getResult());
			args[10]=getSuccessStateString(expression.getSuccessLevel());
			args[11]=expression.isExSuccess()?"大成功":expression.isExFail()?"大失败":"";
			StringBuilder stringBuilder=new StringBuilder();
			for (int i=0;i<repeatTime;i++)
			{
				stringBuilder.append("rd100=").append(args[4]);
				if (expression.getBonusOrPunishDiceNum() != 0)
					stringBuilder.append('{').append(args[5]).append("骰:").append(args[7]).append("}->")
							.append(args[9]).append(args[10]);
				if (expression.isExFail() || expression.isExSuccess())
					stringBuilder.append(args[11]);
				stringBuilder.append("\r\n");

				expression.calculation();
				args[4]= expression.getTrueExpression();
				args[7]=linkArray(expression.getExtraDice());
				args[9]= String.valueOf(expression.getResult());
				args[10]=getSuccessStateString(expression.getSuccessLevel());
				args[11]=expression.isExSuccess()?"大成功":expression.isExFail()?"大失败":"";
			}
			args[12]=stringBuilder.toString();

			StringBuilder templateString=new StringBuilder();

			templateString.append(getString.addressing("ra."+(repeatTime>1?"repeatedly":"single")+bonusAndPunish));
			if(repeatTime==1)
			{
				templateString.append(getString.addressing("ra.single."+bonusAndPunish))
						.append(getString.addressing("ra.main."+bonusAndPunish));
				switch (expression.getSuccessLevel())
				{
					case 0:templateString.append(getString.addressing("ra.state.fail"));break;
					case 1:templateString.append(getString.addressing("ra.state.success"));break;
					case 2:templateString.append(getString.addressing("ra.state.s_success"));break;
					case 3:templateString.append(getString.addressing("ra.state.ss_success"));break;
				}
				if(expression.isExSuccess())
					templateString.append(getString.addressing("ra.state.ex_success"));
				if(expression.isExSuccess())
					templateString.append(getString.addressing("ra.state.ex_fail"));
			}
			else
			{
				templateString.append(getString.addressing("ra.repeatedly.main"));
			}
//			发送
			if(isH)
				templateString.insert(0,getString.addressing("ra.private.user"));
			String resultString=getString.formatString(templateString.toString(),args);
			if (isH)
			{
				qq.sendMessage(resultString);
				return getString.formatString(
						getString.addressing("ra.private.group."+(repeatTime==1?"single":"repeatedly")));
			}
			else
				return resultString;
		}
		else
		{
			NumberExpression expression=new NumberExpression(evaluator,random,builder.toString());
			String[] args=new String[7];
			try
			{
				expression.calculation();
			}
			catch (ExpressionException e)
			{
				args[6]=e.getInfo();
			}


			args[0]=group==null?qq.getName():((Member)qq).getNameCard();
			args[1]=args[1]=group==null?"":group.getName();
			args[2]=expression.getResourceExpression();
			args[3]=expression.getShowExpression();
			args[4]= String.valueOf(expression.getResult());
			StringBuilder stringBuilder=new StringBuilder();
			int i=0;
			do
			{
				stringBuilder.append(args[2]).append('=').append(args[3]).append('=').append(args[4]).append('\n');
				i++;
				try
				{
					expression.calculation();
				}
				catch (ExpressionException e)
				{
					args[6]=e.getInfo();
				}
			}while (repeatTime>i);
			stringBuilder.deleteCharAt(stringBuilder.length()-1);
			args[5]=stringBuilder.toString();

			String templateString;
			if(!args[6].isEmpty())
				templateString=getString.addressing("r.error");
			else
			{
				templateString=getString.addressing(
						"r."+ (isH?"private.":"public.")+(repeatTime==1?"single":"repeatedly")+(isH?".private":""));
			}
			if (isH)
			{
				qq.sendMessage(getString.formatString(templateString,args));
				return getString.formatString(
						getString.addressing(repeatTime==1?"r.private.single.group":"r.private.repeatedly.group"),args);
			}
			else
				return getString.formatString(templateString,args);
		}
	}

	private String linkArray(int[] args)
	{
		if(args.length>0)
			return "";
		StringBuilder builder = new StringBuilder();

		StringBuilder stringBuilder=new StringBuilder();
		for (int i:args)
			builder.append(i).append(",");
		builder.deleteCharAt(builder.length()-1);
		return stringBuilder.toString();
	}

	private String getSuccessStateString(int successLevel)
	{
		switch (successLevel)
		{
			case 0:return "失败";
			case 1:return "成功";
			case 2:return "困难成功";
			case 3:return "极难成功";
			default:return "";
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
