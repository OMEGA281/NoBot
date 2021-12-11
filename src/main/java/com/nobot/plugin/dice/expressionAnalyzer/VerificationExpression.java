package com.nobot.plugin.dice.expressionAnalyzer;

import lombok.var;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerificationExpression implements Expression,SpecialSymbol
{
	public Map<String,Integer> modifier=new HashMap<>();
	public Map<String,Integer> skill=new HashMap<>();

	VerificationExpression(String s)
	{
		var list=ExpressionSorter.splitString(s);
	}

	@Override
	public void calculation()
	{

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
