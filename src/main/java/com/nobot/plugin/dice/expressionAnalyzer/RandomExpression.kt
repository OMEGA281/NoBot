package com.nobot.plugin.dice.expressionAnalyzer

import com.nobot.plugin.dice.expressionAnalyzer.SpecialSymbol.*
import java.util.*
import kotlin.jvm.Throws
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * 这是最小的随机数表达式单位
 */
class RandomExpression @Throws(ExpressionException::class) constructor(private val random: Random, private val resource: String) : SpecialSymbol, Expression
{
	private var diceNum = 1
	private var diceDownLimit = 0
	private var diceUpperLimit = 0
	private var bonusNum = 0
	private var punishNum = 0
	private var bigDiceNum = 0
	private var smallDiceNum = 0
	private var onlyNumber = false

	private var bonusOrPunishNum = 0
	private var maxOrMinNum = 0
	private lateinit var randomDiceArray: IntArray
	private lateinit var extraDiceArray: IntArray
	private lateinit var afterDiceArray: IntArray
	private lateinit var selectDiceArray: IntArray

	private var result = 0
	override fun getResult(): Int=result

	private val list: ArrayList<Any> = ArrayList()

	override fun calculation()
	{
		if (onlyNumber) return
		randomDiceArray = IntArray(diceNum) { random.nextInt(diceUpperLimit - diceDownLimit + 1) + diceDownLimit }
		if (bonusOrPunishNum != 0)
		{
			extraDiceArray = IntArray(bonusOrPunishNum) { random.nextInt(10) }
			afterDiceArray=IntArray(randomDiceArray.size)
			for (i in randomDiceArray.indices)
			{
				val num = randomDiceArray[i]
				var tenDigit = num / 10
				val singleDigit = num % 10
				tenDigit = if (bonusOrPunishNum > 0) min(tenDigit, extraDiceArray.minOf { it })
				else max(tenDigit, extraDiceArray.maxOf { it })
				afterDiceArray[i] = tenDigit * 10 + singleDigit
			}
		}
		if (maxOrMinNum != 0)
		{
			var arr = if (bonusOrPunishNum != 0) afterDiceArray.copyOf() else randomDiceArray.copyOf()
			if (maxOrMinNum < 0) arr.sort() else arr.sortDescending()
			selectDiceArray = arr.sliceArray(0 until abs(maxOrMinNum))
		}
		result = when
		{
			maxOrMinNum != 0 -> selectDiceArray.sum()
			bonusOrPunishNum != 0 -> afterDiceArray.sum()
			else -> randomDiceArray.sum()
		}
	}

	override fun getShowExpression(): String
	{
		if (onlyNumber) return result.toString()
		val builder = StringBuilder()
		if (randomDiceArray.size != 1)
		{
			builder.append('(')
			for (i in randomDiceArray) builder.append(i).append("+")
			builder.delete(builder.length - 1, builder.length).append(')')
		}
		else builder.append(randomDiceArray[0])
		if (bonusOrPunishNum!=0)
		{
			builder.append('{')
			builder.append(if(bonusOrPunishNum>0) symbol_b else symbol_p)
			builder.append(':')
			for (i in extraDiceArray) builder.append(i).append(',')
			builder.deleteCharAt(builder.length - 1).append('}')
		}
		if (maxOrMinNum!=0)
		{
			builder.append('[')
			builder.append(if (maxOrMinNum>0)symbol_k else symbol_q)
			builder.append(':')
			for (i in selectDiceArray) builder.append(i).append(',')
			builder.deleteCharAt(builder.length - 1).append(']')
		}
		return builder.toString()
	}

	override fun getTrueExpression(): String
	{
		if (onlyNumber) return result.toString()
		val array = when
		{
			maxOrMinNum!=0 -> selectDiceArray
			bonusOrPunishNum!=0 -> afterDiceArray
			else -> randomDiceArray
		}
		return if (array.size == 1) array[0].toString()
		else
		{
			val builder = StringBuilder()
			builder.append('(')
			for (i in array) builder.append(i).append('+')
			builder.deleteCharAt(builder.length-1).append(')')
			builder.toString()
		}
	}

	override fun getResourceExpression(): String= resource

	private fun getInteger(index: Int): Int?
	{
		if (index >= list.size || index < 0) return null
		val `var` = list[index]
		return if (`var` !is Int) null else `var`
	}

	init
	{
		if (symbol_d in resource)
		{
			onlyNumber = false
			val chars = resource.toCharArray()
			val sb = StringBuilder()
			//		分离数字和字符
			for (c in chars)
			{
				when (c)
				{
					'0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> sb.append(c)
					else ->
					{
						if (sb.isNotEmpty())
						{
							list.add(sb.toString().toInt())
							sb.clear()
						}
						list.add(c)
					}
				}
			}
			if (sb.isNotEmpty()) list.add(sb.toString().toInt())
			//		识别并处理控制符
			for (i in list.indices)
			{
				val `object` = list[i]
				if (`object` is Char)
				{
					when (`object`)
					{
						symbol_b -> bonusNum = getInteger(i + 1) ?: 1
						symbol_p -> punishNum = getInteger(i + 1) ?: 1
						symbol_k -> bigDiceNum = getInteger(i + 1) ?: 1
						symbol_q -> smallDiceNum = getInteger(i + 1) ?: 1
						symbol_d ->
						{
							diceNum = getInteger(i - 1) ?: 1
							diceUpperLimit = getInteger(i + 1) ?: 100
							diceDownLimit = 1
						}
						symbol_t ->
						{
							diceDownLimit = getInteger(i - 1) ?: 1
							diceUpperLimit = getInteger(i + 1) ?: 100
						}
						else -> throw ExpressionException(resource, "无法识别的控制符号：$`object`",null,null)
					}
				}
			}
			//		获得正式的奖励或者惩罚数量，两者相互抵消，检测是否是百分骰
			bonusOrPunishNum = bonusNum - punishNum
			if ((diceDownLimit != 1 || diceUpperLimit != 100 || diceNum != 1) && bonusOrPunishNum != 0)
				throw ExpressionException(resource, "非单独一个百分骰不启用b和p控制符号",null,null)
			//		获得正式的取大或者取小值，同时存在报错，检测是否取值超过了总值
			if (bigDiceNum != 0 && smallDiceNum != 0) throw ExpressionException(resource, "同时存在k和q控制符",null,null)
			when
			{
				bigDiceNum > 0 -> maxOrMinNum = bigDiceNum
				smallDiceNum > 0 -> maxOrMinNum = 0-smallDiceNum
			}
			if (abs(maxOrMinNum) > diceNum) throw ExpressionException(
				resource,
				"取最" + (if (maxOrMinNum > 0) '大' else '小') + "骰子数目超出总数目"
				,null,null)
		}
		else
		{
			onlyNumber = true
			result = try
			{
				resource.toInt()
			}
			catch (e: NumberFormatException)
			{
				throw ExpressionException(resource, "不存在D时无法使用其他修饰符",null,null)
			}
		}
	}
}