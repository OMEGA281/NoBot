package com.nobot.plugin.draw;


import lombok.NonNull;

import java.util.*;
import java.util.regex.Matcher;

public class Draw implements ConstantPool
{
	private final int indexSize = 0;
	private final Map<Integer, Integer> index = new HashMap<>();

	public String doLine(Card card, String line, int iterationTime)
	{
		iterationTime++;
		if (iterationTime >= 100)
			throw new StackOverflowError();
		String result = line;
		Matcher matcher = pattern_accessoryLibrary.matcher(result);
		while (matcher.find())
		{
			String libListLine = matcher.group(1);
			String info = matcher.group(3);
			if (libListLine == null)
				continue;
			String[] libList = libListLine.split("，|,");

			ArrayList<String> includeTagList = new ArrayList<>();
			ArrayList<String> excludeTagList = new ArrayList<>();

			if (info != null)
			{
				Matcher include = pattern_include.matcher(info);
				Matcher exclude = pattern_exclude.matcher(info);
				Matcher have=pattern_have.matcher(info);

				while (include.find())
				{
					String s = include.group(1);
					if (s != null && !s.isEmpty())
						Collections.addAll(includeTagList, s.split(",|，"));
				}
				while (exclude.find())
				{
					String s = exclude.group(1);
					if (s != null && !s.isEmpty())
						Collections.addAll(excludeTagList, s.split(",|，"));
				}
			}

			String sub = getSubLib(card, libList, includeTagList, excludeTagList);
			String deal = doLine(card, sub, iterationTime).replaceAll("\\$","RDS_CHAR_DOLLAR");


			result = matcher.replaceFirst(deal).replaceAll("RDS_CHAR_DOLLAR","\\$");
			matcher = pattern_accessoryLibrary.matcher(result);
		}
		return result;
	}

	/**
	 * 从所选择的卡中的子牌库中，根据要求的包含和不包含的tag，随机选出一条合适的
	 *
	 * @param card
	 * @param subLibName
	 * @param include
	 * @param exclude
	 * @return
	 */
	public String getSubLib(Card card, @NonNull String[] subLibName, List<String> include, List<String> exclude)
	{
		List<String> list = new ArrayList<>();
		for (String s : subLibName)
		{
			Card.SubLib subLib = card.getSubLib(s);
			if (subLib != null)
				list.addAll(subLib.getLine(include, exclude));
		}
		if (list.isEmpty())
			return "";
		Random random = new Random();
		return list.get(random.nextInt(list.size()));
	}

	public String draw(Card card, int time)
	{
		if (card.getMaxTime() < time)
			time = card.getMaxTime();
		StringBuilder stringBuilder = new StringBuilder();
		try
		{
			stringBuilder.append(doLine(card, card.getStart(), 1));
			for (int i = 0; i < time; i++)
				stringBuilder.append(doLine(card, card.getMain(), 1));
			stringBuilder.append(doLine(card, card.getEnd(), 1));
		}
		catch (StackOverflowError e)
		{
			stringBuilder = new StringBuilder("牌库出现溢出错误，请检查是否产生无限互相套用");
		}
		return stringBuilder.toString();
	}
}
