package com.nobot.plugin.dice.service;

import com.icecreamqaq.yudb.jpa.annotation.Transactional;
import com.nobot.plugin.dice.dao.COCCardSkillNameDao;
import com.nobot.plugin.dice.entity.COCCardSkillName;
import lombok.NonNull;

import javax.inject.Inject;

public class COCCardSkillNameService
{
	@Inject
	COCCardSkillNameDao dao;

	@Transactional
	public COCCardSkillName findBySkillName(@NonNull String name)
	{
		return dao.findBySkillName(name);
	}

	@Transactional
	public COCCardSkillName getOrCreatSkillName(@NonNull String name)
	{
		COCCardSkillName skillName=findBySkillName(name);
		if(skillName==null)
		{
			skillName=new COCCardSkillName();
			skillName.setSkillName(name);
			save(skillName);
		}
		return skillName;
	}

	@Transactional
	public void save(COCCardSkillName skillName)
	{
		dao.save(skillName);
	}
}
