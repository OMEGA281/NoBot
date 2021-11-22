package com.nobot.plugin.dice.expressionAnalyzer;

import lombok.Getter;
import lombok.var;
import net.sourceforge.jeval.Evaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NumberExpressionAnalyzer implements ExpressionAnalyzer
{
	private Evaluator evaluator;
	private Random random;
	private String expression;

	@Getter
	private String showExpression;
	@Getter
	private String trueExpression;
	@Getter
	private int result;
	private	List<Object> list=new ArrayList<>();

	protected NumberExpressionAnalyzer(Evaluator evaluator,Random random, String expression)
	{
		this.evaluator=evaluator;
		this.random=random;
		this.expression=expression;
		var chars=expression.toCharArray();
		var builder=new StringBuilder();
		for (char c : chars)
		{
			switch (c)
			{
				case '+':case '-':case '*':case '/':case '\\':case '%':
				case '(':case ')':
				case '?': case ':':
				case '=':
					if(builder.length()!=0)
					{
						var part=new RandomExpression(random, builder.toString());
						list.add(part);
						builder=new StringBuilder();
					}
					list.add(c);
					break;
				default:
					builder.append(c);
					break;
			}
		}
		list.add(builder.toString());

	}

	@Override
	public void calculation()
	{
		var trueBuilder=new StringBuilder();
		var showBuilder=new StringBuilder();
		for (Object o : list)
		{
			if (o instanceof RandomExpression)
			{
				var part=(RandomExpression)o;
				part.calculation();
				trueBuilder.append(part.getTrueExpression());
				showBuilder.append(part.getShowExpression());
			}
			else
			{
				trueBuilder.append(o);
				showBuilder.append(o);
			}
		}
		this.trueExpression=trueExpression;
		this.showExpression=showExpression;
	}


	@Override
	public String getResourceExpression()
	{
		return expression;
	}
}
