package com.nobot.plugin.dice.expressionAnalyzer;

import lombok.var;

public class NumberExpressionAnalyzer implements ExpressionAnalyzer
{
	@Override
	public void read(String s)
	{
		var chars=s.toCharArray();
		char[] repeatTime_chars,trueExpression_chars;
		for (int i = 0; i < chars.length; i++)
		{
			if(chars[i]==symbol_poundSign)
				repeatTime_chars=new char[].
		}
	}
}
