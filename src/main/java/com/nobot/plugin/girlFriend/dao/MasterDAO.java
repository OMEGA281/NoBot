package com.nobot.plugin.girlFriend.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import com.nobot.plugin.girlFriend.entity.Master;

@Dao
public interface MasterDAO extends YuDao<Master,String>
{
	Master findByGroupNumAndUserNum(long groupNum, long userNum);
}
