package com.nobot.plugin.dice.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import com.nobot.plugin.dice.entity.COCCardSkill;

@Dao
public interface COCCardSkillNumDao extends YuDao<COCCardSkill,String>
{
	public COCCardSkill findByCardIdAndSkillNameId(int cardId,int skillNameId);
}
