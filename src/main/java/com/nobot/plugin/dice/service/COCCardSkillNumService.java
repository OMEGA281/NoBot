package com.nobot.plugin.dice.service;


import com.nobot.plugin.dice.dao.COCCardSkillNumDao;
import com.nobot.plugin.dice.entity.COCCardSkill;

import javax.inject.Inject;

public class COCCardSkillNumService
{
	@Inject
	private COCCardSkillNumDao dao;

	public COCCardSkill getCOCCardSkill(int cardId, int skillNameId)
	{
		return dao.findByCardIdAndSkillNameId(cardId,skillNameId);
	}
}
