package com.nobot.plugin.dice.expressionAnalyzer;

import com.nobot.plugin.dice.service.DefaultSkill;
import com.nobot.plugin.dice.service.SkillNameTranslator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class VerificationExpression implements Expression,SpecialSymbol
{
	private int bonusOrPunishDiceNum;
	private int[] extraDice;
	private String skillName;
	private int skillNum=-1;
	private Random random;

	private int originalValue;
	private int resultValue;

	VerificationExpression(String expression, Random random, Map<String,Integer> skillMap)
	{
		int bonusDiceNum=0,punishDiceNum=0,skillNum=0;
		StringBuilder skillName = new StringBuilder();

		int bMode=1,pMode=2,skillMode=3;
		int mode=0;

		char[] chars=expression.toCharArray();
		int index=0;

		StringBuilder builder=new StringBuilder();
		while (index<chars.length)
		{
			if(chars[index]>='0'||chars[index]<='9')
				builder.append(chars[index]);
			else
			{
				if(mode==bMode)
				{
					if(builder.length()==0)
						bonusDiceNum=1;
					else
					{
						bonusDiceNum = Integer.parseInt(builder.toString());
						builder.delete(0, builder.length());
					}
				}
				else if(mode==pMode)
				{
					if(builder.length()==0)
						punishDiceNum=1;
					else
					{
						punishDiceNum = Integer.parseInt(builder.toString());
						builder.delete(0, builder.length());
					}
				}
				else if(mode==skillMode)
				{
					if(builder.length()==0)
						skillNum=0;
					else
					{
						skillNum = Integer.parseInt(builder.toString());
						builder.delete(0, builder.length());
					}
				}

				switch (chars[index])
				{
					case symbol_b:
						mode=bMode;
						break;
					case symbol_p:
						mode=pMode;
						break;
					default:
						while (chars[index]<'0'||chars[index]>'9')
						{
							skillName.append(chars[index]);
							index++;
						}
						mode=skillMode;
				}
			}
			index++;
		}

		this.random=random;
		bonusOrPunishDiceNum=bonusDiceNum-punishDiceNum;
		extraDice=new int[Math.abs(bonusDiceNum)];
		if(skillName.length()==0)
			throw new ExpressionException(expression,"未检测到技能名称");
		this.skillName=skillName.toString();
		if(skillNum!=0)
			this.skillNum=skillNum;
		else
		{
			Integer integer=skillMap.get(SkillNameTranslator.getMainSkillWord(this.skillName));
			if(integer==null)
				integer=DefaultSkill.getDefaultSkill(this.skillName);
		}
	}

	@Override
	public void calculation()
	{
		originalValue=random.nextInt(100)+1;
		for(int i=0;i<extraDice.length;i++)
		{
			extraDice[i]=random.nextInt(10);
		}
		if(bonusOrPunishDiceNum<0)
		{
			int i=0;
			for (int i1 : extraDice)
			{
				if(i1>i)
					i=i1;
			}
			int tens=originalValue/10;
			int ones=originalValue%10;
			if(tens<i)
				tens=i;
			resultValue=tens*10+ones;
		}
		else if(bonusOrPunishDiceNum>0)
		{
			int i=10;
			for (int i1 : extraDice)
			{
				if(i1<i)
					i=i1;
			}
			int tens=originalValue/10;
			int ones=originalValue%10;
			if(tens>i)
				tens=i;
			resultValue=tens*10+ones;
		}
	}

	@Override
	public int getResult()
	{
		return 0;
	}

	@Override
	public String getShowExpression()
	{
		return null;
	}

	@Override
	public String getTrueExpression()
	{
		return null;
	}

	@Override
	public String getResourceExpression()
	{
		return null;
	}
}
