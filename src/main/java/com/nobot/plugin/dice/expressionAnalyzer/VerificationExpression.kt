package com.nobot.plugin.dice.expressionAnalyzer

import com.nobot.plugin.dice.expressionAnalyzer.SpecialSymbol.symbol_b
import com.nobot.plugin.dice.expressionAnalyzer.SpecialSymbol.symbol_p
import com.nobot.plugin.dice.service.SkillNameTranslator
import java.util.*
import kotlin.math.abs

class VerificationExpression(
	private val resourceExpression: String,
	val random: Random,
	private val skillMap: Map<String?, Int?>,
	private val groupNum: Long
) : Expression, SpecialSymbol
{
	/**表示惩罚或者奖励骰的数目，正数为奖励，负数为惩罚 */
	val bonusOrPunishDiceNum: Int

	val extraDice: IntArray

	var skillName: String? = null

	/**0：正常；1：困难；2：极难 */
	private var difficult = 0
	var skillNum = -1

	/**随机d100获得的数字 */
	private var originalValue = 0

	/**经过（或者没有）奖励或者惩罚修正后的数字 */
	private var resultValue = 0

	/**0：失败；1：成功；2：困难成功；3：极难成功 */
	var successLevel = 0

	var isExSuccess = false

	var isExFail = false
	override fun calculation()
	{
		originalValue = random.nextInt(100) + 1
		for (i in extraDice.indices) extraDice[i] = random.nextInt(10)
		when
		{
			bonusOrPunishDiceNum < 0 ->
			{
				var i = 0
				for (i1 in extraDice)
				{
					if (i1 > i) i = i1
				}
				var tens = originalValue / 10
				val ones = originalValue % 10
				if (tens < i) tens = i
				resultValue = tens * 10 + ones
			}
			bonusOrPunishDiceNum > 0 ->
			{
				var i = 10
				for (i1 in extraDice)
				{
					if (i1 < i) i = i1
				}
				var tens = originalValue / 10
				val ones = originalValue % 10
				if (tens > i) tens = i
				resultValue = tens * 10 + ones
			}
			else -> resultValue = originalValue
		}

		val normalSuccess = skillNum
		val hardSuccess = normalSuccess / 2
		val extremeSuccess = normalSuccess / 5
		successLevel=when(resultValue)
		{
			in 0..extremeSuccess -> if (difficult == 2) 1 else 3
			in 0..hardSuccess -> when (difficult)
				{
					0 -> 2
					1 -> 1
					2 -> 0
					else ->0
				}
			in 0..normalSuccess -> if (difficult == 0) 1 else 0
			else -> 0
		}
		isExSuccess = VerificationSpecialTask.isExSuccess(groupNum, skillNum, resultValue, successLevel)
		isExFail = VerificationSpecialTask.isExFail(groupNum, skillNum, resultValue, successLevel)
	}

	override fun getResult(): Int=resultValue
	override fun getShowExpression(): String?=null
	/**
	 * 本方法返回投掷原始值
	 * @return
	 */
	override fun getTrueExpression(): String=originalValue.toString()
	override fun getResourceExpression(): String= resourceExpression

	init
	{
		var bonusDiceNum = 0
		var punishDiceNum = 0
		var skillNum:Int? = null
		var skillName: String? = null

//		分离表达式
		val chars = resourceExpression.toCharArray()
		var index = 0
		while (index < chars.size)
		{
			when (chars[index])
			{
				symbol_b ->
				{
					val builder = StringBuilder()
					index += 1
					while (index < chars.size)
					{
						if (chars[index] in '0'..'9')
						{
							builder.append(chars[index])
							index += 1
						}
						else break
					}
					bonusDiceNum = if (builder.isNotEmpty()) builder.toString().toInt() else 1
				}
				symbol_p ->
				{
					val builder = StringBuilder()
					index += 1
					while (index < chars.size)
					{
						if (chars[index] in '0'..'9')
						{
							builder.append(chars[index])
							index += 1
						}
						else break
					}
					punishDiceNum = if (builder.isNotEmpty()) builder.toString().toInt() else 1
				}
				in '0'..'9' ->
				{
					val builder = StringBuilder()
					while (index < chars.size)
					{
						if (chars[index] in '0'..'9')
						{
							builder.append(chars[index])
							index += 1
						}
						else break
					}
					skillNum = builder.toString().toInt()
				}
				else ->
				{
					val builder = StringBuilder()
					while (index < chars.size)
					{
						if (chars[index] !in '0'..'9')
						{
							builder.append(chars[index])
							index += 1
						}
						else break
					}
					skillName = builder.toString()
				}
			}
		}
		if (skillName == null) skillName = "匿名技能"

		bonusOrPunishDiceNum = bonusDiceNum - punishDiceNum
		extraDice = IntArray(abs(bonusOrPunishDiceNum))
		when
		{
			skillName.startsWith("困难") ->
			{
				difficult = 1
				this.skillName = skillName.substring(2)
			}
			skillName.startsWith("极难") ->
			{
				difficult = 2
				this.skillName = skillName.substring(2)
			}
			else -> this.skillName = skillName
		}
		this.skillNum=skillNum?:(getSkillNum(skillName)?:throw ExpressionException(resourceExpression,"未在任何地方检测到技能数值",null,null))
	}

	private fun getSkillNum(skillName: String):Int? =
		skillMap[SkillNameTranslator.getMainSkillWord(skillName)]
}