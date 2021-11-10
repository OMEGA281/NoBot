package com.nobot.plugin.lor.dao;

import com.icecreamqaq.yudb.YuDao;
import com.icecreamqaq.yudb.jpa.annotation.Dao;
import com.nobot.plugin.lor.entity.Card;

import java.util.List;

@Dao
public interface CardDAO extends YuDao<Card,Long>
{
	Card findById(long id);
	List<Card> findByGameId(long gameId);
	List<Card> findByTitle(String title);
	List<Card> findByGameIdAndUserId(long gameId, long userId);
	List<Card> findByGameIdAndTitle(long gameId,String title);
}
