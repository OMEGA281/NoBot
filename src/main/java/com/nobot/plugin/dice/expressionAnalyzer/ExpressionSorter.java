package com.nobot.plugin.dice.expressionAnalyzer;

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

	public Expression[] sort(Evaluator evaluator,Random random,String s)
	{
		var sb=new StringBuilder(s.toUpperCase(Locale.ROOT).replaceAll(" ",""));

		if(sb.charAt(0)==symbol_a)
		{
			sb.delete(0,1);
//			FIXME:这个地方引导进入检定表达式中
			throw new ExpressionException(s,"尚未支持检定表达式");
		}
		else
		{
			int time;
			var index=sb.indexOf("#");
			if(index==-1)
				time=1;
			else
			{
				String timeString=sb.substring(0,index);
				if(timeString==null||timeString.isEmpty())
					time=1;
				else
				{
					try
					{
						time=Integer.parseInt(timeString);
						if(time>10||time<1)
							throw new ExpressionException(s,"重复次数越界");
					}
					catch (NumberFormatException e)
					{
						throw new ExpressionException(s,"重复次数非法");
					}
				}
			}
			sb.delete(0,index+1);

//			FIXME:就目前来看这是个脱裤子放屁专门占用内存的设计 将来可能修改一下
			var expressions=new Expression[time];
			for (int i=0;i<time;i++)
			{
				expressions[i]=new NumberExpression(evaluator,random,sb.toString());
			}
			return expressions;
		}
	}
	public static Expression test(Evaluator evaluator, Random random, String s)
	{
		return new NumberExpression(evaluator,random,s);
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
