package com.nobot.plugin.dice.expressionAnalyzer;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class JustNum implements SingleExpression
{
	private int num;

	protected JustNum(@NonNull String s)
	{
		num=Integer.parseInt(s);
	}

	@Override
	public void calculation()
	{
	}

	@Override
	public int getResult()
	{
		return num;
	}

	@Override
	public String getShowExpression()
	{
		return String.valueOf(num);
	}

	@Override
	public String getTrueExpression()
	{
		return String.valueOf(num);
	}

	@Override
	public String getResourceExpression()
	{
		return String.valueOf(num);
	}
}
