package com.nobot.plugin.dice.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import com.nobot.plugin.dice.entity.COCGroup;
import com.nobot.plugin.dice.entity.COCGroupSetting;

@Dao
public interface COCGroupSettingDAO extends YuDao<COCGroupSetting,Long>
{
	COCGroupSetting findById(long id);
}
