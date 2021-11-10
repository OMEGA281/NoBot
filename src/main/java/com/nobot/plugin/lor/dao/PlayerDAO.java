package com.nobot.plugin.lor.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import com.nobot.plugin.lor.entity.Player;

import java.util.List;

@Dao
public interface PlayerDAO extends YuDao<Player, Long>
{
	List<Player> findByUserId(long userId);
	Player findByUserIdAndGameId(long userId, long gameId);
	List<Player> findByGameIdAndTeam(long groupId, int team);
}
