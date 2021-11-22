package com.nobot.plugin.dice.expressionAnalyzer;

import net.sourceforge.jeval.Evaluator;

import java.util.Random;

public class ExpressionSorter
{
	public static ExpressionAnalyzer test(Evaluator evaluator,Random random, String s)
	{
		return new NumberExpressionAnalyzer(evaluator,random,s);
	}
}
