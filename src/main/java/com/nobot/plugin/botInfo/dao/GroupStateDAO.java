package com.nobot.plugin.botInfo.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import com.nobot.plugin.botInfo.entity.GroupState;

@Dao
public interface GroupStateDAO extends YuDao<GroupState,String>
{
	GroupState findByGroupNum(long groupNum);
}
