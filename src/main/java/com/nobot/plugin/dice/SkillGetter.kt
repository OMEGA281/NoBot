package com.nobot.plugin.dice

import com.IceCreamQAQ.Yu.annotation.Action
import com.IceCreamQAQ.Yu.annotation.Before
import com.icecreamqaq.yuq.annotation.GroupController
import com.icecreamqaq.yuq.annotation.PrivateController
import com.icecreamqaq.yuq.controller.BotActionContext
import com.icecreamqaq.yuq.entity.Friend
import com.icecreamqaq.yuq.entity.Group
import com.icecreamqaq.yuq.entity.Member
import java.util.*

@GroupController
@PrivateController
class SkillGetter
{
	private val random = Random()

	@Action(".dnd{time}")
	fun dnd(time: Int,name:String): String
	{
		val builder = StringBuilder()
		if(time !in 1..10)
			return "非法投掷次数"
		builder.append("${name}进行dnd做成：")
		for (i in 0 until time)
		{
			var sum=0
			fun takeNum():Int
			{
				val list= Array(4) { random.nextInt(5) + 1 }
				list.sort()
				val r=list.sum()-list[0]
				sum+=r
				return r
			}
			builder.append("力量:${takeNum()},敏捷:${takeNum()},体质:${takeNum()},智力:${takeNum()},感知:${takeNum()},魅力:${takeNum()},总和:${sum}\n")
		}
		return builder.toString()
	}

	@Action(".coc{time}")
	fun coc(time: Int,name:String): String
	{
		val builder = StringBuilder()
		if(time !in 1..10)
			return "非法投掷次数"
		builder.append("${name}进行coc做成：")
		for (i in 0 until time)
		{
			var sum=0
			var luck=0
			fun d3t6():Int
			{
				val k=Array(3) { random.nextInt(5) + 1 }.sum()*5
				sum+=k
				return k;
			}
			fun d3t6NoSum(): Int
			{
				luck=Array(3) { random.nextInt(5) + 1 }.sum()*5
				return luck
			}
			fun d2t6And6():Int
			{
				val k=(Array(2) { random.nextInt(5) + 1 }.sum()+6)*5
				sum+=k
				return k
			}
			builder.append("力量:${d3t6()},体质:${d3t6()},体型:${d2t6And6()},敏捷:${d3t6()},外貌:${d3t6()},智力:${d2t6And6()},意志:${d3t6()},教育:${d2t6And6()},幸运:${d3t6NoSum()},总和:${sum}/${sum+luck}\n")
		}
		return builder.toString()
	}

//	@Action(".st{string}")
//	fun st(string: String):String
//	{
//		var skillList=string
//		val name:String?
//		if('-' in string)
//		{
//			val ss=string.split('-',ignoreCase=false, limit = 2)
//			name=ss[0]
//			skillList=ss[1]
//		}
//
////		false为文字，ture为空
//		var mode=false
//		var skillName= mutableListOf<String>()
//		var skillNum= mutableListOf<String>()
//		var skillMap= mutableMapOf<String,Int>()
//
//		fun changeMode(b:Boolean)
//		{
//			if(b!=mode)
//			{
//				if(b)
//				{
//					skillMap[skillName] = skillNum.contentToString().toInt()
//				}
//			}
//		}
//		for (i in skillList.indices step 0)
//		{
//			if(skillList[i] in '0'..'9')
//
//		}
//	}

	@Before
	fun getName(actionContext: BotActionContext, member: Member?, friend: Friend?)
	{
		actionContext["name"] = if (actionContext.source is Group)
			member?.nameCardOrName() ?: ""
		else
			friend?.name ?: ""
	}
}