package com.nobot.plugin.lor.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import com.nobot.plugin.lor.entity.Game;

@Dao
public interface GameDAO extends YuDao<Game,Long>
{
	Game findById(long id);
}
