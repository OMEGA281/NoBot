package com.nobot.plugin.dice.expressionAnalyzer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.var;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class RandomExpression implements SpecialSymbol,ExpressionAnalyzer
{
	private Random random;
	private int diceNum=1,diceDownLimit,diceUpperLimit;
	private int bonusNum,punishNum;
	private int bigDiceNum,smallDiceNum;

	@Getter(AccessLevel.PACKAGE)
	private int bonusOrPunishMode, maxOrMinMode;
	private int tureBPNum,tureMaxOrMinNum;

	private int[] randomDiceArray,extraDiceArray,afterDiceArray,selectDiceArray;
	private String expression;
	private int result;

	private String resource;
	private ArrayList<Object> list;

	protected RandomExpression(Random random,String resource)
	{
		this.random=random;
		this.resource=resource;
		list=new ArrayList<>();
		checkExpression();
		randomDiceArray=new int[diceNum];
		if(bonusOrPunishMode!=NO_PUNISH_AND_BONUS)
		{
			extraDiceArray = new int[tureBPNum];
			afterDiceArray=new int[tureBPNum];
		}
		if(maxOrMinMode!=GET_ALL_DICE_MODE)
			selectDiceArray=new int[tureMaxOrMinNum];
	}

	private void checkExpression()
	{
		var chars=resource.toCharArray();
		var sb=new StringBuilder();
//		分离数字和字符
		for (var c:chars)
		{
			switch (c)
			{
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					sb.append(c);
					break;
				default:
					list.add(Integer.parseInt(sb.toString()));
					sb=new StringBuilder();
					list.add(c);
					break;
			}
		}
//		识别并处理控制符
		for (int i=0;i<list.size();i++)
		{
			var object=list.get(i);
			if(object instanceof Character)
			{
				switch ((char)object)
				{
					case symbol_b:
						var bonus=getInteger(i+1);
						if(bonus==null)
							bonusNum=1;
						else
							bonusNum=bonus;
						break;
					case symbol_p:
						var punish=getInteger(i+1);
						if(punish==null)
							punishNum=1;
						else
							punishNum=punish;
						break;
					case symbol_k:
						var bigDice=getInteger(i+1);
						if(bigDice==null)
							bigDiceNum=1;
						else
							bigDiceNum=bigDice;
						break;
					case symbol_q:
						var smallDice=getInteger(i+1);
						if(smallDice==null)
							smallDiceNum=1;
						else
							smallDiceNum=smallDice;
						break;
					case symbol_d:
						var randomTime=getInteger(i-1);
						if(randomTime==null)
							diceNum=1;
						else
							diceNum=randomTime;
						var randomSize=getInteger(i+1);
						if(randomSize==null)
							diceUpperLimit=100;
						else
							diceUpperLimit=randomSize;
						diceDownLimit=1;
						break;
					case symbol_t:
						var downLimit=getInteger(i-1);
						if(downLimit==null)
							diceDownLimit=1;
						else
							diceDownLimit=downLimit;
						var upLimit=getInteger(i+1);
						if(upLimit==null)
							diceUpperLimit=100;
						else
							diceUpperLimit=upLimit;
						diceDownLimit=1;
						break;
					default:
						throw new ExpressionException(resource,"无法识别的控制符号："+ (char) object);
				}
			}
		}
//		获得正式的奖励或者惩罚数量，两者相互抵消，检测是否是百分骰
		tureBPNum=bonusNum-punishNum;
		if(tureBPNum>0)
			bonusOrPunishMode =BONUS_MODE;
		else if(tureBPNum<0)
			bonusOrPunishMode =NO_PUNISH_AND_BONUS;
		else
			bonusOrPunishMode =PUNISH_MODE;
		if((diceDownLimit!=1||diceUpperLimit!=100||diceNum!=1)&&bonusOrPunishMode==NO_PUNISH_AND_BONUS)
			throw new ExpressionException(resource,"非单独一个百分骰不启用b和p控制符号");
//		获得正式的取大或者取小值，同时存在报错，检测是否取值超过了总值
		if(bigDiceNum!=0&&smallDiceNum!=0)
			throw new ExpressionException(resource,"同时存在k和q控制符");
		if(bigDiceNum>0)
		{
			maxOrMinMode=GET_MAX_DICE_MODE;
			tureMaxOrMinNum=bigDiceNum;
		}
		else if(smallDiceNum>0)
		{
			maxOrMinMode=GET_MIN_DICE_MODE;
			tureMaxOrMinNum=smallDiceNum;
		}
		else
			maxOrMinMode=GET_ALL_DICE_MODE;
		if(tureMaxOrMinNum>diceNum)
			throw new ExpressionException(resource,"取最"+(maxOrMinMode==GET_MAX_DICE_MODE?'大':'小')+"骰子数目超出总数目");
	}

	@Override
	public void calculation()
	{
		for (int i=0;i<diceNum;i++)
		{
			if(diceUpperLimit==diceDownLimit)
			{
				randomDiceArray[i] = diceUpperLimit;
				continue;
			}
			randomDiceArray[i]=random.nextInt(diceUpperLimit-diceDownLimit+1)+diceDownLimit;
		}

		if (bonusOrPunishMode!=NO_PUNISH_AND_BONUS)
		{
			for (int i=0;i<tureBPNum;i++)
				extraDiceArray[i]=random.nextInt(10);
			int replaceNum=bonusOrPunishMode==BONUS_MODE?10:-1;
			for(var i:extraDiceArray)
			{
				if(bonusOrPunishMode==BONUS_MODE)
				{
					if (i<replaceNum)
						replaceNum=i;
				}
				else
				{
					if (i > replaceNum)
						replaceNum = i;
				}
			}
			for (int i = 0; i < randomDiceArray.length; i++)
			{
				int num=randomDiceArray[i];
				int tenDigit,singleDigit;
				tenDigit=num/10;
				singleDigit=num%10;
				if(bonusOrPunishMode==BONUS_MODE)
				{
					if (tenDigit>replaceNum)
					{
						tenDigit = replaceNum;
						afterDiceArray[i]=tenDigit*10+singleDigit;
					}
				}
				else
				{
					if(tenDigit<replaceNum)
					{
						tenDigit = replaceNum;
						afterDiceArray[i]=tenDigit*10+singleDigit;
					}
				}
				afterDiceArray[i]=num;
			}
		}

		if(maxOrMinMode!=GET_ALL_DICE_MODE)
		{
			if(maxOrMinMode==GET_MIN_DICE_MODE)
				selectDiceArray=Arrays.stream(afterDiceArray).sorted().limit(selectDiceArray.length).toArray();
			else
				selectDiceArray=Arrays.stream(afterDiceArray).boxed().sorted(Comparator.reverseOrder())
						.limit(selectDiceArray.length).mapToInt(value -> value).toArray();
			result= Arrays.stream(selectDiceArray).sum();
		}
		if(bonusOrPunishMode==NO_PUNISH_AND_BONUS)
			result= Arrays.stream(randomDiceArray).sum();
		result= Arrays.stream(afterDiceArray).sum();
	}

	private Integer getInteger(int index)
	{
		if(index>=list.size()||index<0)
			return null;
		var var=list.get(index);
		if(!(var instanceof Integer))
			return null;
		return (Integer) var;
	}
}
