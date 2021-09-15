package com.nobot.plugin.dice.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import com.nobot.plugin.dice.entity.COCCard;

import java.util.List;


@Dao
public interface COCCardDAO extends YuDao<COCCard,String>
{
	public List<COCCard> findByUserAndGroup(long user, long group);
	public List<COCCard> findByUser(long user);
	public List<COCCard> findByGroup(long group);
}
