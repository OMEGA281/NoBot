package com.nobot.plugin

import com.IceCreamQAQ.Yu.annotation.Action
import com.IceCreamQAQ.Yu.annotation.Before
import com.icecreamqaq.yuq.annotation.GroupController
import com.icecreamqaq.yuq.annotation.PrivateController
import com.icecreamqaq.yuq.controller.BotActionContext
import com.icecreamqaq.yuq.entity.Contact
import com.icecreamqaq.yuq.entity.Friend
import com.icecreamqaq.yuq.entity.Group
import com.icecreamqaq.yuq.entity.Member
import com.icecreamqaq.yuq.message.Message
import com.icecreamqaq.yuq.message.MessageLineQ
import com.nobot.system.annotation.UnzipFile
import com.nobot.system.stringHelper.DefaultStringFile
import com.nobot.system.stringHelper.GetString
import com.nobot.tool.MD5Utils
import com.nobot.tool.TimeUtils
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

@GroupController
@PrivateController
@UnzipFile(name = "string/TodayLuck.properties", aim = "./string/TodayLuck.properties")
@DefaultStringFile(value = "./String/TodayLuck.properties", info = "这是今日人品的相关文件")
class TodayLuck
{
	private val simpleDateFormat = SimpleDateFormat("yyyyMMdd")
	@Inject
	private lateinit var getString:GetString

	@Before
	fun getName(actionContext: BotActionContext, qq: Contact,group: Group?)
	{
		if (actionContext.source is Member)
		{
			actionContext["name"] = qq.name
			return
		}
		if (qq is Friend)
		{
			actionContext["name"] = qq.name
			return
		}
		var name = (qq as Member).nameCard
		if (name.isEmpty())
		{
			name = qq.name
		}
		actionContext["name"] = name
		actionContext["groupName"]=group?.name ?:""

	}

	@Action("今日人品")
	fun getTodayLuck(qq: Long, name: String,groupName:String): MessageLineQ
	{
		val messageLineQ = Message().lineQ()
		val nowTime = System.currentTimeMillis()
		val today = Date(nowTime)
		val yesterday: Date
		val calendar = Calendar.getInstance()
		calendar.time = today
		calendar.add(Calendar.DATE, -1)
		yesterday = calendar.time
		val todayInfo = TimeUtils.getFormatData(simpleDateFormat) + name
		val todayResult = MD5Utils.getMD5(todayInfo, 10)
		var index = todayResult.substring(1, 2).toInt()
		val todayLuck = todayResult.substring(index + 2, index + 4).toInt()
		val yesterdayInfo = simpleDateFormat.format(yesterday)
		val yesterdayResult = MD5Utils.getMD5(yesterdayInfo, 10)
		index = todayResult.substring(1, 2).toInt()
		val yesterdayLuck = yesterdayResult.substring(index + 2, index + 4).toInt()
		val different = ((todayLuck - yesterdayLuck) / 20f).roundToInt()

		val args= arrayOfNulls<String>(4)
		args[0]=name
		args[1]=groupName
		args[2]= todayLuck.toString()
		args[3]=yesterdayLuck.toString()

		messageLineQ.text(getString.formatString(getString.addressing("jrrp.start"), *args))
		var address= "jrrp.${different}."
		when (different)
		{
			3 -> address += if (yesterdayLuck < 20) '2' else '4'
			2 -> address += if (yesterdayLuck < 30) '3' else '6'
			1 -> address += when(yesterdayLuck)
			{
				in 0..20->2
				in 0..50->5
				in 0..80->8
				else ->8
			}
			0 -> address += when(yesterdayLuck)
			{
				in 0..30->3
				in 0..60->6
				in 0..100->10
				else ->10
			}
			-1 -> address += when(yesterdayLuck)
			{
				in 0..50->5
				in 0..80->8
				in 0..100->10
				else ->8
			}
			-2 -> address += if (yesterdayLuck < 70) '7' else "10"
			-3 -> address += if (yesterdayLuck < 80) '8' else "10"
			else -> address.dropLast(1)
		}
		messageLineQ.text(getString.formatString(getString.addressing(address),*args))
		messageLineQ.text("\r\n")
		messageLineQ.text("今天好方向：")
		messageLineQ.text(when ((todayResult.substring(12, 13).toInt() / 2.5).toInt())
		{
			0 -> "东"
			1 -> "西"
			2 -> "南"
			3 -> "北"
			else ->""
		})
		messageLineQ.text("\t")
		messageLineQ.text("今天好颜色：")
		messageLineQ.text(when (todayResult.substring(13, 14).toInt())
		{
			0 -> "红"
			1 -> "橙"
			2 -> "黄"
			3 -> "绿"
			4 -> "青"
			5 -> "蓝"
			6 -> "紫"
			7 -> "黑"
			8 -> "白"
			9 -> "粉"
			else -> ""
		})
		return messageLineQ
	}
}