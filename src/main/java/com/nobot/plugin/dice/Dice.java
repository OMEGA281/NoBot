package com.nobot.plugin.dice;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import javax.inject.Inject;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dice
{
	private Random random;
	private Evaluator evaluator;

	public Dice()
	{
		random=new Random();
		evaluator=new Evaluator();
	}

	/**
	 * 随机获得一个位于两个参数之间（包括两个参数本身）的整数
	 * @param lowLimit 下限（包含）
	 * @param upLimit 上限（包含）
	 * @return 随机整数
	 */
	protected int getRandomInteger(int lowLimit,int upLimit)
	{
		int bound=upLimit-lowLimit+1;
		return random.nextInt(bound)+lowLimit;
	}

	/**
	 * 将投掷表达式子（如：3d8）转换成运算式（如：（5+8+1））
	 * @param d 投掷随机数表达式
	 * @return
	 */
	private String transRandomExpressionToExpression(String d)
	{
		String[] split=d.split("[dD]");
		int time,limit;
		switch (split.length)
		{
			case 0:
				time=1;
				limit=100;
				break;
			case 1:
				time=Integer.parseInt(split[0]);
				limit=100;
				break;
			case 2:
			default:
				if (split[0]==null||split[0].isEmpty())
					time=1;
				else
					time=Integer.parseInt(split[0]);
				limit=Integer.parseInt(split[1]);
				break;
		}
		StringBuilder stringBuilder=new StringBuilder("(");
		for (int i=0;i<time;i++)
		{
			stringBuilder.append(getRandomInteger(1,limit));
			stringBuilder.append("+");
		}
		stringBuilder.deleteCharAt(stringBuilder.length()-1);
		stringBuilder.append(")");
		return stringBuilder.toString();
	}

	/**
	 * 将投掷表达式子（如：2d2+3+3d8）转换成运算式（如：(1+2)+3+（5+8+1））
	 * @param s 投掷随机数表达式
	 * @return
	 */
	public String getTrueExpression(String s)
	{
		Pattern pattern=Pattern.compile("\\d*[dD]\\d*");
		Matcher matcher=pattern.matcher(s);
		while (matcher.find())
		{
			String part=matcher.group();
			s=matcher.replaceFirst(transRandomExpressionToExpression(part));
			matcher=pattern.matcher(s);
		}
		return s;
	}

	/**
	 * 将计算式子（如：（2+2）+3+（5+2+1））得出结果
	 * @param expression 式子
	 * @return
	 */
	public int getResult(String expression)
	{
		String s=getTrueExpression(expression);
		int result=-1000;
		try
		{
			result=(int)evaluator.getNumberResult(expression);
		}
		catch (EvaluationException e)
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 将输入的表达式表现出完全的运算结果
	 * 例如：2+3d6+3*2d3=2+(2+6+3)+3*(2+2)=25
	 * @param expression 表达式
	 * @param mode 表现形势：<br>
	 *             1、完全表现：2+3d6+3*2d3=2+(2+6+3)+3*(2+2)=25<br>
	 *             2、略去原始表达式：2+(2+6+3)+3*(2+2)=25<br>
	 *             3、略去计算式：2+3d6+3*2d3=25<br>
	 *             4、只保留结果：25
	 * @return
	 */
	public String getFullResultExpression(String expression,int mode)
	{
		String numExpression=getTrueExpression(expression);
		int result=getResult(numExpression);
		StringBuilder stringBuilder=new StringBuilder();
		switch (mode)
		{
			case 1:
				stringBuilder.append(expression);
				stringBuilder.append("=");
			case 2:
				stringBuilder.append(numExpression);
				stringBuilder.append("=");
			case 4:
				stringBuilder.append(result);
				break;
			case 3:
				stringBuilder.append(expression);
				stringBuilder.append("=");
				stringBuilder.append(result);
				break;
		}
		return stringBuilder.toString();
	}

}
