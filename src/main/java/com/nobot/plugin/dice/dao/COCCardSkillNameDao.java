package com.nobot.plugin.dice.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import com.nobot.plugin.dice.entity.COCCardSkillName;

@Dao
public interface COCCardSkillNameDao extends YuDao<COCCardSkillName,String>
{
	COCCardSkillName findBySkillName(String skillName);
	COCCardSkillName findById(Integer skillNum);
}
