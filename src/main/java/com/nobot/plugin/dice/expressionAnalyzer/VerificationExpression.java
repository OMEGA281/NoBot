package com.nobot.plugin.dice.expressionAnalyzer;

import com.nobot.plugin.dice.service.DefaultSkill;
import com.nobot.plugin.dice.service.SkillNameTranslator;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class VerificationExpression implements Expression,SpecialSymbol
{
	private int bonusOrPunishDiceNum;
	private int[] extraDice;
	private String skillName;
	/**0：正常；1：困难；2：极难*/
	private int difficult=0;
	private int skillNum=-1;
	private Random random;
	private long groupNum;

	private int originalValue;
	private int resultValue;
	/**0：失败；1：成功；2：困难成功；3：极难成功*/
	private int successLevel;

	VerificationExpression(String expression, @NonNull Random random, Map<String,Integer> skillMap,long groupNum)
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
		if(skillName.length()<2)
			this.skillName=skillName.toString();
		else
		{
			if (skillName.charAt(0)=='困'&&skillName.charAt(1)=='难')
			{
				this.skillName = skillName.delete(0, 2).toString();
				difficult=1;
			}
			else if (skillName.charAt(0)=='极'&&skillName.charAt(1)=='难')
			{
				this.skillName = skillName.delete(0, 2).toString();
				difficult=2;
			}
			else this.skillName=skillName.toString();
		}
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
		else resultValue=originalValue;

		int normalSuccess=skillNum;
		int hardSuccess=normalSuccess/2;
		int extremeSuccess=normalSuccess/5;

		if(resultValue<=extremeSuccess)
			successLevel=difficult==2?1:3;
		else if(resultValue<=hardSuccess)
		{
			switch (difficult)
			{
				case 0:
					successLevel=2;
					break;
				case 1:
					successLevel=1;
					break;
				case 2:
					successLevel=0;
					break;
			}
		}
		else if (resultValue<=normalSuccess)
			successLevel=difficult==0?1:0;


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
