package com.nobot.plugin.dice;

import com.nobot.plugin.dice.entity.COCCard;
import com.nobot.plugin.dice.service.COCCardService;

import javax.inject.Inject;
import java.util.Map;

public class DataService
{
	@Inject
	COCCardService service;

	/**
	 * 获取卡片
	 * @param qq 用户QQ号
	 * @param groupNum 用户卡所在群号，未分配则为0
	 * @return 返回要求的用户的所在群的群号 若在所在群中未找到，则返回最近的未分配卡并自动更改到该群
	 */
	public COCCard getCard(long qq, long groupNum) throws NoSuitableCardException
	{
		COCCard card=service.getCard(qq,groupNum);
		if(card==null)
		{
			throw new NoSuitableCardException(qq,groupNum);
		}
		return card;
	}

	public COCCard getNewCard(long qq, long groupNum, Map<String,Integer> skillMap)
	{
		COCCard card=new COCCard();
		card.setUser(qq);
		card.addGroup(groupNum);
		card.setSkillList(service.transSkill(skillMap));
		service.saveOrUpdateCard(card);
		return card;
	}
}
