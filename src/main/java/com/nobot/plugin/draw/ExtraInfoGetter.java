package com.nobot.plugin.draw;

import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItem;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import com.nobot.plugin.dice.expressionAnalyzer.ExpressionException;
import com.nobot.plugin.dice.expressionAnalyzer.RandomExpression;
import com.nobot.system.BotInfo;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;

public class ExtraInfoGetter implements ConstantPool
{
	@Inject
	MessageItemFactory factory;
	@Inject
	BotInfo botInfo;

	private Random random=new Random();

	private ArrayList<String> complexMessageDecompose(String s)
	{
		Matcher matcher=specialWord_pattern.matcher(s);
		ArrayList<Integer> index=new ArrayList<>();
		index.add(0);
		while (matcher.find())
		{
			index.add(matcher.start(0));
			index.add(matcher.end(0));
		}
		index.add(s.length());
		ArrayList<String> stringList=new ArrayList<>();
		for (int i=0;i<index.size()-1;i++)
		{
			String part=s.substring(index.get(i),index.get(i+1));
			if(!part.isEmpty())
				stringList.add(part);
		}
		return stringList;
	}
	private MessageItem replaceString(String s,long groupNum,long senderNum) throws ExpressionException
	{
		if(!(s.startsWith("$")&&s.endsWith("#")))
			return factory.text(s);
		String command=s.substring(2,s.length()-2);
		String[] ps=command.split(":|：");
		String type=ps[0];
		String text=ps[1];
		switch (type)
		{
			case specialWord_image:
				return factory.imageByFile(new File(drawPool+"\\"+text));
			case specialWord_r:
				RandomExpression expression= new RandomExpression(random,text);
				int result=expression.getResult();
				return factory.text(Integer.toString(result));
			case specialWord_name:
				switch (text)
				{
					case "me":
						return factory.text(botInfo.getMyGroupName(groupNum));
					case "sender":
						return factory.text(botInfo.getGroupName(groupNum,senderNum));
					default:
						try
						{
							long num=Long.parseLong(text);
							return factory.text(botInfo.getGroupName(groupNum,num));
						}
						catch (NumberFormatException e)
						{
							return factory.text("");
						}
				}
			case specialWord_at:
				switch (text)
				{
					case "me":
						return factory.at(BotInfo.myQQNum);
					case "sender":
						return factory.at(senderNum);
					default:
						try
						{
							long num=Long.parseLong(text);
							return factory.at(num);
						}
						catch (NumberFormatException e)
						{
							return factory.text("");
						}
				}
			default:
				return null;
		}
	}

	protected Message transToMessage(String s,long groupNum,long senderNum) throws ExpressionException
	{
		ArrayList<String> list=complexMessageDecompose(s);
		ArrayList<MessageItem> messageItems=new ArrayList<>();
		for (String part:list)
			messageItems.add(replaceString(part,groupNum,senderNum));
		Message message=new Message();
		for (MessageItem messageItem:messageItems)
			message.plus(messageItem);
		return message;
	}
}
