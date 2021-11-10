package com.nobot.plugin.girlFriend.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import com.nobot.plugin.girlFriend.entity.Girl;
import com.nobot.plugin.girlFriend.entity.MyGroup;

@Dao
public interface GirlDAO extends YuDao<Girl,Long>
{
	Girl findById(long ID);
	Girl findByGroupNumAndName(MyGroup groupNum, String name);
}
