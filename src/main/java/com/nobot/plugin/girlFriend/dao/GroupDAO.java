package com.nobot.plugin.girlFriend.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import com.nobot.plugin.girlFriend.entity.MyGroup;

@Dao
public interface GroupDAO extends YuDao<MyGroup,String>
{
	MyGroup findById(long Id);
}
