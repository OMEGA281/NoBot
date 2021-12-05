package com.nobot.plugin.dice.expressionAnalyzer;

import lombok.Getter;
import lombok.var;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NumberExpression implements Expression
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

	protected NumberExpression(Evaluator evaluator, Random random, String expression)
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
						var part=ExpressionSorter.getSingleExpression(random,builder.toString());
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
		if(builder.length()!=0)
		{
			var part=ExpressionSorter.getSingleExpression(random,builder.toString());
			list.add(part);
		}
	}

	@Override
	public void calculation()
	{
		var trueBuilder=new StringBuilder();
		var showBuilder=new StringBuilder();
		for (Object o : list)
		{
			if (o instanceof SingleExpression)
			{
				var part=(SingleExpression)o;
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
		this.trueExpression=trueBuilder.toString();
		this.showExpression=showBuilder.toString();
		try
		{
			result= (int) evaluator.getNumberResult(trueExpression);
		}
		catch (EvaluationException e)
		{
			throw new ExpressionException(expression,e.getMessage());
		}
	}


	@Override
	public String getResourceExpression()
	{
		return expression;
	}
}
