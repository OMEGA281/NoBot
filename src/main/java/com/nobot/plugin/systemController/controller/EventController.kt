package com.nobot.plugin.systemController.controller

import com.IceCreamQAQ.Yu.annotation.Event
import com.IceCreamQAQ.Yu.annotation.EventListener
import com.icecreamqaq.yuq.annotation.PrivateController
import com.icecreamqaq.yuq.event.GroupMessageEvent
import com.icecreamqaq.yuq.event.MessageEvent
import com.nobot.plugin.systemController.service.BlackAndWhiteListService
import com.nobot.plugin.systemController.service.GroupSleepService
import javax.inject.Inject

@EventListener
@PrivateController
class EventController
{
	private val specialCommand = arrayOf(".bot on",".bot off","。bot on","。bot off",
		".boton",".botoff","。boton","。botoff",".bot","。bot")

	@Inject
	private val groupSleepService: GroupSleepService? = null

	@Inject
	private val blackAndWhiteListService: BlackAndWhiteListService? = null

	@Event(weight = Event.Weight.high)
	fun interceptor(event: MessageEvent)
	{
//		第一步：先检测该环境是否被设置休眠
		val groupMode = groupSleepService!!.isGroupActive(if(event is GroupMessageEvent) event.group.id else 0)
//		第二检测是否是黑名单人员
		val isBlack = blackAndWhiteListService!!.isUserInBlackList(event.sender.id, if(event is GroupMessageEvent) event.group.id else 0)
//		第三检测是否具有管理权限
		val isOP = blackAndWhiteListService.isOP(event.sender.id)
//		第四检测是否是特殊命令
		val isSpecialCommand=specialCommand.contains(event.message.codeStr)

		when
		{
			isOP->return
			isBlack->event.cancel=true
			isSpecialCommand->return
			!groupMode->event.cancel=true
		}
	}
}