package com.nobot.plugin.systemController.service

import com.IceCreamQAQ.Yu.annotation.Event
import com.IceCreamQAQ.Yu.annotation.EventListener
import com.IceCreamQAQ.Yu.event.events.AppStartEvent
import com.icecreamqaq.yuq.event.MessageEvent
import com.icecreamqaq.yuq.event.SendMessageEvent
import com.icecreamqaq.yuq.yuq

@EventListener
class BotInformationService
{
	private var botStartTime: Long = 0
	var receiveMessage:Long=0
	var sendMessage:Long=0

	@Event
	private fun loadStartTime(event: AppStartEvent)
	{
		botStartTime = System.currentTimeMillis()
	}

	val runTime: Long
		get() = System.currentTimeMillis() - botStartTime

	@Event
	private fun addReceiveMessage(event: MessageEvent)=++receiveMessage
	@Event
	private fun addSendMessage(event: SendMessageEvent)=++sendMessage

	fun getBotName():String=yuq.botInfo.name
	fun getBotNameCard(groupNum:Long):String?
	{
		val group= yuq.groups[groupNum] ?:return null
		val cardName=group.bot.nameCard
		return cardName.ifEmpty { getBotName() }
	}
	fun getBotId()= yuq.botId
	fun getSystemName()=System.getProperty("os.name")
	fun getJavaVersion()=System.getProperty("java.version")
	fun getVersion()="暂未实现"
}