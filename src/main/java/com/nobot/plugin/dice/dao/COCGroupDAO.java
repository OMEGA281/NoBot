package com.nobot.plugin.dice.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import com.nobot.plugin.dice.entity.COCGroup;

@Dao
public interface COCGroupDAO extends YuDao<COCGroup,Long>
{
	COCGroup findById(long id);
}
