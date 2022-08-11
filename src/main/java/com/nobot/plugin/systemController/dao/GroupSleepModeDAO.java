package com.nobot.plugin.systemController.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import com.nobot.plugin.systemController.entity.GroupSleepMode;

@Dao
public interface GroupSleepModeDAO extends YuDao<GroupSleepMode,Long>
{
	GroupSleepMode findByID(long ID);
}
