package com.nobot.plugin.dice.expressionAnalyzer;

public interface Expression
{
	/**按照解析的表达式进行一次计算*/
	void calculation() throws ExpressionException;
	int getResult();
	/**给用户看的表达式*/
	String getShowExpression();
	/**用于计算的表达式*/
	String getTrueExpression();
	/**汇入时候的表达式*/
	String getResourceExpression();
}
