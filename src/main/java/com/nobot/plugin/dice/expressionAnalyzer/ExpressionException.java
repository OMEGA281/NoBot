package com.nobot.plugin.dice.expressionAnalyzer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExpressionException extends RuntimeException
{
	private String expression;
	private String info;
}
