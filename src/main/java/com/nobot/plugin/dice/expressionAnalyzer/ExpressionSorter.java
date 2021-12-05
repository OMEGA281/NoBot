package com.nobot.plugin.dice.expressionAnalyzer;

import lombok.NonNull;
import lombok.var;
import net.sourceforge.jeval.Evaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExpressionSorter implements SpecialSymbol
{
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
		var list=new ArrayList<String>();
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
						list.add(builder.toString());
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
