package com.nobot.plugin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Config;
import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.event.MessageEvent;
import com.icecreamqaq.yuq.event.SendMessageEvent;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import com.icecreamqaq.yuq.message.MessageLineQ;
import com.nobot.system.GetResource;
import com.nobot.system.annotation.CreateFile;
import com.nobot.tool.FileUtils;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@PrivateController
@GroupController
@EventListener
@CreateFile("botInfo.txt")
public class BotInfo
{
	@Config("YuQ.Controller.RainCode.prefix")
	private String prefixStr;
	@Inject
	GetResource getResource;
	@Inject
	FileUtils fileUtils;
	@Inject
	MessageItemFactory factory;

	Instant startTime;
	String version;

	long messageReceiveTime= 0L,messageSendTime= 0L;
	@Event
	public void startTime(AppStartEvent event) throws IOException
	{
		startTime=Instant.now();
		InputStream inputStream=getResource.gerJarResource("version");
		if(inputStream==null)
			return;
		BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
		version=reader.readLine().substring(1);
		inputStream.close();
	}
	@Event
	public void addReceiveMessageNum(MessageEvent event)
	{
		messageReceiveTime++;
	}
	@Event
	public void addReceiveMessageNum(SendMessageEvent event)
	{
		messageSendTime++;
	}
	@Action(".botInfo")
	public MessageLineQ showBotInfo()
	{
		MessageLineQ messageLineQ=new MessageLineQ(new Message());
		messageLineQ.text("NoBot").text("-").text(version==null?"未知版本":version).text("\r\n")
				.text("[").text("YuQ-ArtQQ:0.0.6.10-R52").text("\r\n")
				.text("YuQ:0.1.0.0-DEV21").text("\r\n")
				.text("Yu-Core:0.2.0.0-DEV13").text("]").text("\r\n")
				.text(System.getProperty("os.name")).text("\\").text(System.getProperty("os.version")).text(" ")
				.text(System.getProperty("os.arch")).text(" ").text("jvm:").text(System.getProperty("java.version"))
				.text("\r\n");
		double totalMemory=Runtime.getRuntime().totalMemory()/(1024D*1024D*1024D);
		double freeMemory=Runtime.getRuntime().freeMemory()/(1024D*1024D*1024D);
		DecimalFormat decimalFormat=new DecimalFormat("##0.00");
		messageLineQ.text("内存：").text(decimalFormat.format(freeMemory)).text("/")
				.text(decimalFormat.format(totalMemory)).text("\r\n");
		messageLineQ.text("CPU：").text(Runtime.getRuntime().availableProcessors()+"核 ").text("\r\n");
		Instant now=Instant.now();
		LocalDateTime startDateTime=LocalDateTime.ofInstant(startTime, ZoneId.systemDefault());
		DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");
		messageLineQ.text("从").text(dateTimeFormatter.format(startDateTime)).text("运行至今，共")
				.text(Long.toString(Duration.between(startTime,now).toHours())).text("小时").text("\r\n");
		messageLineQ.text("接受"+messageReceiveTime+"条消息").text("发送"+messageSendTime+"条消息");
		return messageLineQ;
	}
	@Action(".bot")
	public Message showBotSimpleInfo()throws IOException
	{
		Message message=new Message();
		message.plus("NoBot").plus("-").plus(version==null?"未知版本":version).plus("\r\n")
				.plus(System.getProperty("os.name")).plus(System.getProperty("os.version")).plus(" ")
				.plus(System.getProperty("os.arch")).plus(" ").plus("jvm:").plus(System.getProperty("java.version"))
				.plus("\r\n");
//		读取自定义的提示语
		String myInfo=fileUtils.readAll(getResource.getOutsideResource("botInfo.txt"));
		if(myInfo!=null&&!myInfo.isEmpty())
			return message.plus(Message.Companion.toMessageByRainCode(myInfo));
		else
			return message;
	}
}
