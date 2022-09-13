package com.nobot.plugin.systemController.controller

import com.IceCreamQAQ.Yu.annotation.Action
import com.icecreamqaq.yuq.annotation.GroupController
import com.icecreamqaq.yuq.entity.Group
import com.icecreamqaq.yuq.entity.Member
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
@GroupController
class GroupController
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
	fun botSwitch(mode: String, group: Group, user: Member): String?
	{
		val b =
			administratorsDAO.op.contains(user.id) || administratorsDAO.sop == user.id || user.isAdmin() || user.isOwner()
		return when (mode.trim().toLowerCase())
		{
			"on" -> if (b)
			{
				if (groupSleepService.setGroupSleepMode(group.id, true))
					getString.addressAndFormat("bot.status.active1", null)
				else getString.addressAndFormat("bot.status.active0", null)
			}
			else null
			"off" -> if (b)
			{
				if (groupSleepService.setGroupSleepMode(group.id, false))
					getString.addressAndFormat("bot.status.sleep1", null)
				else getString.addressAndFormat("bot.status.sleep0", null)
			}
			else null
			else -> null
		}
	}

	@Action("[\\.。]bot")
	fun botInfo(group: Group): String?
	{
		val info= arrayOf(botInformationService.getBotName(),
			botInformationService.getBotNameCard(group.id),
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