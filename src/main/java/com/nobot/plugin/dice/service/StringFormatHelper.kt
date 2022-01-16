package com.nobot.plugin.dice.service

import com.nobot.plugin.dice.expressionAnalyzer.Expression
import com.nobot.plugin.dice.expressionAnalyzer.NumberExpression
import com.nobot.plugin.dice.expressionAnalyzer.RandomExpression
import com.nobot.plugin.dice.expressionAnalyzer.VerificationExpression

/**
 * 测试中
 */
class StringFormatHelper
{
	fun getFormat(expression: Expression, name: String): String
	{
		var text: StringBuilder = StringBuilder()
		when (expression)
		{
			is RandomExpression -> text.append("${expression.resourceExpression}=${expression.result}")
			is NumberExpression -> text.append("${name}进行了一次投掷，${expression.resourceExpression}=${expression.showExpression}=${expression.result}")
			is VerificationExpression ->
			{
				text.append("${name}对${expression.skillName}进行检定，rd100=${expression.trueExpression}")
				if (expression.bonusOrPunishDiceNum != 0)
				{
					text.append('[').append(if (expression.bonusOrPunishDiceNum > 0) "奖励" else "惩罚").append(":")
					expression.extraDice.forEach { i -> text.append(i).append(',') }
					text.append("]->${expression.result}/${expression.skillNum}")
				}
				text.append("检定")
				when (expression.successLevel)
				{
					0 -> text.append("失败")
					1 -> text.append("成功")
					2 -> text.append("困难成功")
					3 -> text.append("极难成功")
				}
				if (expression.isExSuccess)
					text.append("，大成功")
				if (expression.isExFail)
					text.append("，大失败")
			}
		}
		return text.toString()
	}
}