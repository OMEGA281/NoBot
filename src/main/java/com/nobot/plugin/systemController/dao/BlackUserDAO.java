package com.nobot.plugin.systemController.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import com.nobot.plugin.systemController.entity.BlackUser;

@Dao
public interface BlackUserDAO extends YuDao<BlackUser,Long>
{
	Boolean existsByUserNumAndGroupNum(long userNum,long groupNum);
	BlackUser removeByUserNumAndGroupNum(long userNum, long groupNum);
}
