package com.nobot.plugin.dice.expressionAnalyzer;

import lombok.Getter;
import lombok.var;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 这是一个投掷表达式
 */
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

	public NumberExpression(Evaluator evaluator, Random random, String expression) throws ExpressionException
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
						var part=new RandomExpression(random,builder.toString());
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
			RandomExpression part= null;
			part = new RandomExpression(random,builder.toString());
			list.add(part);
		}
	}

	@Override
	public void calculation() throws ExpressionException
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