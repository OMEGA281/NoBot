package com.nobot.plugin.dice.service;

import com.icecreamqaq.yudb.jpa.annotation.Transactional;
import com.nobot.plugin.dice.dao.COCCardDAO;
import com.nobot.plugin.dice.entity.COCCard;
import com.nobot.plugin.dice.entity.COCCardSkill;
import com.nobot.plugin.dice.entity.COCCardSkillName;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class COCCardService
{
	@Inject
	private COCCardDAO cardDAO;
	@Inject
	private COCCardSkillNameService skillNameService;

	/**
	 * 通过用户和群取卡
	 *
	 * @param user      用户QQ
	 * @param group 	群号，若值为0则从未分配卡中取出修改时间最近的卡
	 * @return 卡，没有对应记录会返回私聊的返回null
	 */
	@Transactional
	public COCCard getCard(long user, long group)
	{
		List<COCCard> list= cardDAO.findByUserAndGroup(user,group);
		if(list==null||list.isEmpty())
			return null;
		if(group==0)
		{
			COCCard r=list.get(0);
			for(int i=1;i<list.size();i++)
			{
				COCCard x=list.get(i);
				if(x.getUpdateTime().compareTo(r.getUpdateTime())>0)
					r=x;
			}
			return r;
		}
		if(list.size()>1)
			new Exception("在同一个群内一个用户出现了两张卡 请处理\r\nCOCCard\r\nUSER:"+user+"GROUP:"+group)
					.printStackTrace();
		return list.get(0);
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

	/**
	 * 保存或更新角色卡
	 *
	 * @param card 角色卡
	 */
	@Transactional
	public void saveOrUpdateCard(COCCard card)
	{
		cardDAO.saveOrUpdate(card);
	}

	@Transactional
	public COCCardSkillName transSkillName(String skillName)
	{
		return skillNameService.getOrCreatSkillName(skillName);
	}

	@Transactional
	public List<COCCardSkill> transSkill(Map<String,Integer> map)
	{
		List<COCCardSkill> list=new ArrayList<>();
		for (Map.Entry<String,Integer> entry:map.entrySet())
		{
			COCCardSkill skill=new COCCardSkill();
			skill.setSkillName(transSkillName(entry.getKey()));
			skill.setPoint(entry.getValue());
			list.add(skill);
		}
		return list;
	}
}
