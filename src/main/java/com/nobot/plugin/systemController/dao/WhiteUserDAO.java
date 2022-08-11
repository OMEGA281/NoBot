package com.nobot.plugin.systemController.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import com.nobot.plugin.systemController.entity.BlackUser;
import com.nobot.plugin.systemController.entity.WhiteUser;

@Dao
public interface WhiteUserDAO extends YuDao<WhiteUser,Long>
{
	Boolean existsByUserNumAndGroupNum(long userNum,long groupNum);
	WhiteUser removeByUserNumAndGroupNum(long userNum, long groupNum);
}
