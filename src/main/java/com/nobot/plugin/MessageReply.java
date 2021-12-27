package com.nobot.plugin;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Catch;
import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.entity.Contact;
import com.icecreamqaq.yuq.event.GroupMessageEvent;
import com.icecreamqaq.yuq.event.MessageEvent;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import com.nobot.system.annotation.UnzipFile;
import com.nobot.tool.XmlReader;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EventListener
@GroupController
@PrivateController
@UnzipFile(aim = "MessageReply.xml",name = "MessageReply.xml")
public class MessageReply
{
	private File file=new File("MessageReply.xml");
	private Map<Pattern,String[]> map=new ConcurrentHashMap<>();
	private Random random=new Random();
	@Inject
	private MessageItemFactory factory;

	private final String attributeKey="pattern";
	private final Pattern replaceString=Pattern.compile("%(\\d+)%");

	@Inject
	XmlReader xmlReader;

	@Event
	public void initReply(AppStartEvent event) throws JDOMException, IOException
	{
		reloadPattern();
	}
//	@Event
//	public void patternReply(ActionContextInvokeEvent.Post event)
//	{
//
//	}
	@Event
	public void patternReply(MessageEvent event)
	{
		String s=event.getMessage().getCodeStr();
		String replyString = null;
		String[] groups;
		for (Map.Entry<Pattern,String[]> entry:map.entrySet())
		{
			Matcher matcher=entry.getKey().matcher(s);
			if(matcher.matches())
			{
				if(entry.getValue().length==1)
				{
					replyString=entry.getValue()[0];
					break;
				}
				else
					replyString=entry.getValue()[random.nextInt(entry.getValue().length)];
				int x=matcher.groupCount();
				groups=new String[x+1];
				for(int i=0;i<=x;i++)
					groups[i]=matcher.group(x);
				break;
			}
		}
		if (replyString==null)
			return;
//		TODO:添加回复词的分组

		//		FIXME:将来RainCode支持voice的时候去除这里
		Message message;
		if(replyString.toLowerCase(Locale.ROOT).startsWith("<rain:voice:")
				&&replyString.toLowerCase(Locale.ROOT).endsWith(">"))
		{
			message=factory.voice(new File(replyString.substring(12,replyString.length()-1))).toMessage();
		}
		else
			message=Message.Companion.toMessageByRainCode(replyString);

		if(event instanceof GroupMessageEvent)
			((GroupMessageEvent) event).getGroup().sendMessage(message);
		else
			event.getSender().sendMessage(message);
	}
	@Action("重新加载关键词")
	public Message c_reloadPattern(long SOPNum, Contact qq) throws JDOMException, IOException
	{
		if(SOPNum==qq.getId())
			return new Message().plus("加载了"+reloadPattern()).plus("关键词");
		return null;
	}
	private int reloadPattern() throws JDOMException, IOException
	{
		map.clear();
		Document document=xmlReader.getDocument(file);
		List<Element> elements=document.getRootElement().getChildren();
		for (Element element:elements)
		{
			String s=element.getAttributeValue(attributeKey);
			if(s==null)
				continue;
			String[] strings=element.getText().split("\\|\\|\\|");
			map.put(Pattern.compile(s),strings);
		}
		return map.size();
	}
	@Catch(error = JDOMException.class)
	public void readException1()
	{
		throw new Message().plus("xml文档错误，请检查拼写").toThrowable();
	}
	@Catch(error = IOException.class)
	public void readException2()
	{
		throw new Message().plus("文件读取错误").toThrowable();
	}
}
