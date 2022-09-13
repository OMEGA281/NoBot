package com.nobot.plugin.systemController.controller

import com.IceCreamQAQ.Yu.annotation.Action
import com.icecreamqaq.yuq.annotation.GroupController
import com.icecreamqaq.yuq.annotation.PrivateController
import com.nobot.plugin.systemController.dao.AdministratorsDAO
import com.nobot.plugin.systemController.service.BotInformationService
import com.nobot.plugin.systemController.service.GroupSleepService
import com.nobot.system.annotation.UnzipFile
import com.nobot.system.stringHelper.DefaultStringFile
import com.nobot.system.stringHelper.GetString
import com.nobot.tool.TimeUtils
import java.text.SimpleDateFormat
import javax.inject.Inject

@UnzipFile(name = "string/BotStatus.properties", aim = "./string/BotStatus.properties")
@DefaultStringFile(value = "./String/BotStatus.properties", info = "这是机器人控制的相关字符串文件")
@PrivateController
class PrivateController
{
	@Inject
	private lateinit var groupSleepService: GroupSleepService

	@Inject
	private lateinit var administratorsDAO: AdministratorsDAO

	@Inject
	private lateinit var botInformationService: BotInformationService

	@Inject
	private lateinit var getString: GetString

	@Action("[\\.。]bot{mode}")
	fun botSwitch(mode: String, user: Long): String?
	{
		val b =
			administratorsDAO.op.contains(user) || administratorsDAO.sop == user
		return when (mode.trim().toLowerCase())
		{
			"on" -> if (b)
			{
				if (groupSleepService.setGroupSleepMode(0, true))
					getString.addressAndFormat("bot.status.active1", null)
				else getString.addressAndFormat("bot.status.active0", null)
			}
			else null
			"off" -> if (b)
			{
				if (groupSleepService.setGroupSleepMode(0, false))
					getString.addressAndFormat("bot.status.sleep1", null)
				else getString.addressAndFormat("bot.status.sleep0", null)
			}
			else null
			else -> null
		}
	}

	@Action("[\\.。]bot")
	fun botInfo(): String?
	{
		val info= arrayOf(botInformationService.getBotName(),
			"",
			botInformationService.getBotId().toString(),
			TimeUtils.getTimeLengthString(botInformationService.runTime,false),
			botInformationService.runTime.toString(),
			TimeUtils.getFormatData(SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")),
			botInformationService.receiveMessage.toString(),
			botInformationService.sendMessage.toString(),
			botInformationService.getSystemName(),
			botInformationService.getJavaVersion(),
			botInformationService.getVersion())
		return getString.addressAndFormat("bot.info",*info)
	}
}