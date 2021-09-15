package com.nobot.plugin.draw;

import com.IceCreamQAQ.Yu.annotation.*;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.controller.BotActionContext;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.entity.Friend;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import com.nobot.system.MyInfo;
import com.nobot.system.annotation.CreateDir;
import com.nobot.tool.XmlReader;
import lombok.Getter;
import org.jdom2.Document;
import org.jdom2.JDOMException;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@PrivateController
@GroupController
@EventListener
@CreateDir(ConstantPool.drawPool)
public class Controller
{
	@Getter
	class NoCardException extends RuntimeException
	{
		String cardName;
		NoCardException(String cardName)
		{
			this.cardName=cardName;
		}
	}

	@Inject
	XmlReader xmlReader;

	@Inject
	Draw draw;

	Map<String,Card> map=new HashMap<>();

	@Event
	public void init(AppStartEvent event)
	{
		File pool=new File(ConstantPool.drawPool);
		for (File file:pool.listFiles((dir, name) -> name.endsWith("xml")))
		{
			try
			{
				Document document=xmlReader.getDocument(file);
				Card card=new Card(document);
				map.put(file.getName().split("\\.")[0],card);
			}
			catch (JDOMException | IOException e)
			{
				continue;
			}
		}
	}

	@Before
	public void getUserInfo(BotActionContext actionContext, Group group, Friend sender,Contact qq)
	{
		if(sender!=null)
		{
			actionContext.set("isGroup", false);
			actionContext.set("userName",sender.getName());
			actionContext.set("myName", MyInfo.myName);
		}
		else if(actionContext.getSource() instanceof Member)
		{
			actionContext.set("isGroup", false);
			actionContext.set("userName",qq.getName());
			actionContext.set("myName", MyInfo.myName);
		}
		else
		{
			actionContext.set("isGroup", true);
			Member member=(Member)qq;
			actionContext.set("userName",member.getNameCard().isEmpty()?member.getName():member.getNameCard());
			actionContext.set("myName",
					group.getBot().getNameCard().isEmpty()?MyInfo.myName:group.getBot().getNameCard());
		}
	}

	@Action("抽牌 {cardName} {t_num}次")
	public Message drawCard(String cardName,String t_num,boolean isGroup,String userName,String myName)
	{
		int num=Integer.parseInt(t_num);
		Card card=map.get(cardName);
		if(card==null)
			throw new NoCardException(cardName);
		String s=draw.draw(card,num);
		return new Message().plus(s);
	}

	@Action("抽牌 {cardName}")
	public Message drawCard(String cardName,boolean isGroup,String userName,String myName)
	{
		return drawCard(cardName,"1",isGroup,userName,myName);
	}

	@Catch(error = NumberFormatException.class)
	public Message numberFormatException()
	{
		return new Message().plus("数值输入错误");
	}
}
