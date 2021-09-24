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
import com.icecreamqaq.yuq.message.MessageLineQ;
import com.nobot.system.BotInfo;
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
			this.cardName = cardName;
		}
	}

	@Inject
	XmlReader xmlReader;

	@Inject
	Draw draw;

	@Inject
	ExtraInfoGetter extraInfoGetter;

	Map<String, Card> map = new HashMap<>();

	@Event
	public void startLoadDrawPool(AppStartEvent event)
	{
		refreshDrawPool();
	}

	public void refreshDrawPool()
	{
		File pool = new File(ConstantPool.drawPool);
		for (File file : pool.listFiles((dir, name) -> name.endsWith("xml")))
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
			actionContext.set("userName", sender.getName());
			actionContext.set("myName", BotInfo.myName);
		}
		else if(actionContext.getSource() instanceof Member)
		{
			actionContext.set("isGroup", false);
			actionContext.set("userName", qq.getName());
			actionContext.set("myName", BotInfo.myName);
		}
		else
		{
			actionContext.set("isGroup", true);
			Member member=(Member)qq;
			actionContext.set("userName", member.getNameCard().isEmpty() ? member.getName() : member.getNameCard());
			actionContext.set("myName",
					group.getBot().getNameCard().isEmpty() ? BotInfo.myName : group.getBot().getNameCard());
		}
	}

	@Action(value = "{t_num}次抽{cardName}")
	public Message drawCard(String cardName, String t_num, boolean isGroup, String userName, String myName,
							Group group, long qq)
	{
		int num = Integer.parseInt(t_num);
		Card card = map.get(cardName);
		if (card == null)
			throw new NoCardException(cardName);
		String s = draw.draw(card, num);
		Message message = extraInfoGetter.transToMessage(s, isGroup?group.getId():0, qq);
		return message;
	}

	@Action(value = "抽{cardName}")
	public Message drawCard(String cardName, boolean isGroup, String userName, String myName, Group group, long qq)
	{
		return drawCard(cardName, "1", isGroup, userName, myName, group, qq);
	}

	@Action("更新抽牌库")
	public Message hotRenewalDrawPool()
	{
		map.clear();
		refreshDrawPool();
		return new Message().plus("更新完毕，共获得"+map.size()+"个牌库");
	}

	@Catch(error = NumberFormatException.class)
	public Message numberFormatException()
	{
		return new Message().plus("数值输入错误");
	}
}
