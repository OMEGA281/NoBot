package com.nobot.plugin.dice.expressionAnalyzer;

public interface ExpressionAnalyzer
{
	/**按照解析的表达式进行一次计算*/
	void calculation();
	int getResult();
	/**给用户看的表达式*/
	String getShowExpression();
	String getTrueExpression();
	String getResourceExpression();
}
