package com.nobot.plugin.dice.service;

import com.icecreamqaq.yudb.jpa.annotation.Transactional;
import com.nobot.plugin.dice.dao.COCCardDAO;
import com.nobot.plugin.dice.dao.COCCardSkillDAO;
import com.nobot.plugin.dice.dao.COCGroupDAO;
import com.nobot.plugin.dice.entity.COCCard;
import com.nobot.plugin.dice.entity.COCCardSkill;
import com.nobot.plugin.dice.entity.COCGroup;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class COCCardService
{
	@Inject
	private COCCardDAO cardDAO;

	@Inject
	private COCGroupDAO groupDAO;

	@Inject
	private COCCardSkillDAO skillDAO;

	/**
	 * 通过用户和群取卡
	 * @param userNum      用户QQ
	 * @param groupNum 	群号
	 * @return coc卡
	 */
	@Transactional
	public COCCard getCard(long userNum, long groupNum)
	{
		List<COCCard> cardList= cardDAO.findByUser(userNum);
		COCGroup group=groupDAO.findById(groupNum);
		if(cardList==null||cardList.isEmpty()||group==null)
			return null;
		for (COCCard card : cardList)
		{
			List<COCGroup> cardGroupList=card.getGroupList();
			for (COCGroup cocGroup : cardGroupList)
			{
				if(cocGroup.getId()==groupNum)
					return card;
			}
		}
		return null;
	}

	/**
	 * 根据用户qq号获得所有名下角色卡
	 *
	 * @param num 用户qq号
	 * @return 所有名下角色卡
	 */
	@Transactional
	public List<COCCard> getCard(long num)
	{
		return cardDAO.findByUser(num);
	}

	public Map<String, Integer> getSkillMap(long userNum, long groupNum)
	{
		COCCard card=getCard(userNum,groupNum);
		List<COCCardSkill> skillList=card.getSkillList();
		HashMap<String,Integer> map=new HashMap<>();
		for (COCCardSkill skill : skillList)
			map.put(skill.getName(),skill.getPoint());
		return map;
	}

	public int getSkill(long userNum,long groupNum,String skillName)
	{
		Map<String,Integer> map=getSkillMap(userNum,groupNum);
		Integer integer=map.get(skillName);
		return integer==null?-1:integer;
	}

	/**
	 * 设置一张卡的技能值
	 * @param cardID 卡ID
	 * @param skillName 技能名称
	 * @param skillPoint 技能数值
	 * @return 曾经的技能数值，若曾经不存在技能则返回-1
	 */
	@Transactional
	public int setSkill(long cardID,String skillName,int skillPoint)
	{
		COCCard card=cardDAO.findById(cardID);
		List<COCCardSkill> skillList=card.getSkillList();
		for (COCCardSkill skill : skillList)
		{
			if(skill.getName().equals(skillName))
			{
				int pastSkill=skill.getPoint();
				skill.setPoint(skillPoint);
				cardDAO.update(card);
				return pastSkill;
			}
		}
		COCCardSkill skill=new COCCardSkill();
		skill.setCard(card);
		skill.setName(skillName);
		skill.setPoint(skillPoint);
		skillDAO.save(skill);
		return -1;
	}
}
