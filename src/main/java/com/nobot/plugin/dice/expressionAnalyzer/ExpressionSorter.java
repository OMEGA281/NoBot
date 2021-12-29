package com.nobot.plugin.dice.expressionAnalyzer;

import com.nobot.plugin.dice.entity.COCCard;
import lombok.NonNull;
import lombok.var;
import net.sourceforge.jeval.Evaluator;
import net.sourceforge.jeval.function.math.Exp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ExpressionSorter implements SpecialSymbol
{
	/**
	 * 这是一个分选以及制造计算表达式的方法，会自动抹除表达式前的重复次数
	 * 检定表达式
	 * @param evaluator jeval计算器
	 * @param random 随机数产生器
	 * @param s 表达式
	 * @return 未经计算的表达式
	 */
	public Expression numCalculation(@NonNull Evaluator evaluator, @NonNull Random random, String s)
	{
		var sb=new StringBuilder(s.toUpperCase(Locale.ROOT).replaceAll(" ",""));
		var index=sb.indexOf("#");
		sb.delete(0,index+1);
		return new NumberExpression(evaluator,random,sb.toString());
	}

	public Expression numVerification(@NonNull Random random, String s, @NonNull COCCard card)
	{
		var sb=new StringBuilder(s.toUpperCase(Locale.ROOT).replaceAll(" ",""));

	}

	/**
	 * 获得表达式的重复次数
	 * @param s 表达式
	 * @return 表达式重复的次数
	 * @throws ExpressionException 不合法的重复次数
	 */
	public int getRepeatTime(String s)
	{
		int index=s.indexOf('#');
		if(index==0)
			return 1;
		String s_time=s.substring(index);
		try
		{
			return Integer.parseInt(s_time);
		}
		catch (NumberFormatException e)
		{
			throw new ExpressionException(s,"重复次数出错！");
		}
	}

	public static SingleExpression getSingleExpression(@NonNull Random random,@NonNull String s)
	{
		var chars=s.toCharArray();
		var justNum=true;
		for (char c : chars)
		{
			if(c<'0'||c>'9')
			{
				justNum=false;
				break;
			}
		}
		if(justNum)
			return new JustNum(s);
		else
			return new RandomExpression(random,s);
	}
	public static List<Object> splitString(@NonNull String s)
	{
		var chars=s.toCharArray();
		var builder=new StringBuilder();
		var list= new ArrayList<>();
		//0未定，1数字，2字符
		int type=0;
		for (char c : chars)
		{
			switch (c)
			{
				case symbol_b:case symbol_d:case symbol_p:
				case symbol_k:case symbol_q:case symbol_t:
					if(builder.length()!=0)
					{
						list.add(builder.toString());
						builder=new StringBuilder();
					}
					list.add(String.valueOf(c));
					break;
				case 0:case 1:case 2:case 3:case 4:
				case 5:case 6:case 7:case 8:case 9:
					if(type==2)
					{
						list.add(Integer.parseInt(builder.toString()));
						builder=new StringBuilder();
					}
					builder.append(c);
					type=1;
					break;
				default:
					if(type==1)
					{
						list.add(builder.toString());
						builder=new StringBuilder();
					}
					builder.append(c);
					type=2;
					break;
			}
		}
		return list;
	}
}
