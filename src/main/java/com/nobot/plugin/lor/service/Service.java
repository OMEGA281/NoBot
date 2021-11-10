package com.nobot.plugin.lor.service;

import com.icecreamqaq.yudb.jpa.annotation.Transactional;
import com.nobot.plugin.lor.dao.CardDAO;
import com.nobot.plugin.lor.dao.GameDAO;
import com.nobot.plugin.lor.dao.PlayerDAO;
import com.nobot.plugin.lor.entity.Card;
import com.nobot.plugin.lor.entity.Game;
import com.nobot.plugin.lor.entity.Player;
import lombok.NonNull;
import lombok.var;

import javax.inject.Inject;
import java.util.*;

public class Service
{
	@Inject
	public PlayerDAO playerDAO;
	@Inject
	public GameDAO gameDAO;
	@Inject
	public CardDAO cardDAO;

	@Transactional
	public List<String> getAllCard(long group)
	{
		Game game = gameDAO.findById(group);
		if (game == null)
			return null;
		List<String> list = new ArrayList<>();
		for (Card card : game.getCardList())
			list.add(card.getTitle());
		return list;
	}

	@Transactional
	public List<Card> getFreeCard(long group,long user)
	{
		Game game = gameDAO.findById(group);
		List<Player> list=game.getPlayerList();
		for (Player player:list)
		{
			if(player.getUserId()==user)
			{
				List<Card> result=new ArrayList<>();
				List<Card> cardList=player.getCardList();
				for (Card card:cardList)
				{
					if(!card.getHasGet())
						result.add(card);
				}
				return result;
			}
		}
		return null;
	}

	@Transactional
	public void addPlayerToGame(long group, long user, int team)
	{
		Game game = gameDAO.findById(group);
		if (game == null)
			return;
		List<Player> playerList = game.getPlayerList();
		for (Player player : playerList)
		{
			if (player.getUserId() == user)
			{
				player.setTeam(team);
				playerDAO.update(player);
				return;
			}
		}
		Player player = new Player();
		player.setGameId(game);
		player.setUserId(user);
		player.setTeam(team);
		player.setLight(3);
		player.setHp(0);
		player.setDefense(0);
		playerDAO.saveOrUpdate(player);
	}

	@Transactional
	public List<Long> listGameTeam(long group, int team)
	{
		List<Player> playerList = gameDAO.findById(group).getPlayerList();
		List<Long> list = new ArrayList<>();
		if (playerList == null)
			return list;
		for (Player player : playerList)
		{
			if(player.getTeam()==team)
				list.add(player.getUserId());
		}
		return list;
	}

	@Transactional
	public boolean setGameState(long group, int state)
	{
		Game game = gameDAO.findById(group);
		if (game == null)
			return false;
		game.setState(state);
		gameDAO.update(game);
		return true;
	}

	@Transactional
	public boolean isGameActive(long group)
	{
		Game game = gameDAO.findById(group);
		if (game == null)
			return false;
		return game.getState() == Game.State.ACTIVE;
	}

	@Transactional
	public boolean initGame(long group, long user)
	{
		Game game = gameDAO.findById(group);
		if (game != null)
			return false;
		game = new Game();
		game.setId(group);
		game.setState(Game.State.ACTIVE);
		game.setInitiator(user);
		gameDAO.save(game);
		return true;
	}

	@Transactional
	public Game getGame(long group)
	{
		return gameDAO.findById(group);
	}

	@Transactional
	public void removeGame(long group)
	{
		Game game=gameDAO.findById(group);
		for (Card card:game.getCardList())
		{
			card.setGameId(null);
			card.setPlayerId(null);
			cardDAO.update(card);
			cardDAO.delete(card.getId());
		}
		for (Player player:game.getPlayerList())
		{
			player.setGameId(null);
			playerDAO.delete(player.getId());
		}
		gameDAO.delete(group);
	}


	/**
	 * 添加一张新卡
	 * @param group
	 * @param title
	 */
	@Transactional
	public void addCard(long group,long user,@NonNull String[] title)
	{
		Game game=gameDAO.findById(group);
		if(game==null)
			return;
		for(Player player:game.getPlayerList())
		{
			if(player.getUserId()==user)
			{
				for (String s : title)
				{
					Card card = new Card();
					card.setGameId(game);
					card.setPlayerId(player);
					card.setTitle(s);
					card.setHasGet(false);
					cardDAO.save(card);
				}
			}
		}
	}

	/**
	 * 清除所有牌
	 * @param group
	 * @param user
	 */
	@Transactional
	public void clearCard(long group, long user)
	{
		Game game=gameDAO.findById(group);
		if(game==null)
			return;
		for(Player player:game.getPlayerList())
		{
			if(player.getUserId()==user)
			{
				List<Card> cardList=player.getCardList();
				for (Card c:cardList)
				{
					c.setPlayerId(null);
					c.setGameId(null);
					cardDAO.delete(c.getId());
				}
			}
		}
	}

	/**
	 * 获取手牌
	 * @param groupNum
	 * @param userNum
	 * @return
	 */
	@Transactional
	public Map<Integer,String> getHeadCardList(long groupNum, long userNum)
	{
		Map<Integer,String> map = new HashMap<>();
		Game game=gameDAO.findById(groupNum);
		if(game==null)
			return map;
		for(Player player:game.getPlayerList())
		{
			if(player.getUserId()==userNum)
			{
				List<Card> cardList=player.getCardList();
				if(cardList==null||cardList.isEmpty())
					return map;
				cardList.sort(Comparator.comparingLong(Card::getId));
				for (int i=0;i<cardList.size();i++)
				{
					Card card=cardList.get(i);
					if(card.getHasGet())
						map.put(i,card.getTitle());
				}
				return map;
			}
		}
		return map;
	}

	/**
	 * 获取牌库
	 * @param groupNum
	 * @param userNum
	 * @return
	 */
	@Transactional
	public Map<Integer,String> getAllCardList(long groupNum, long userNum)
	{
		Map<Integer,String> map = new HashMap<>();
		Game game=gameDAO.findById(groupNum);
		if(game==null)
			return map;
		for(Player player:game.getPlayerList())
		{
			if(player.getUserId()==userNum)
			{
				List<Card> cardList=player.getCardList();
				if(cardList==null||cardList.isEmpty())
					return map;
				cardList.sort(Comparator.comparingLong(Card::getId));
				for (int i=0;i<cardList.size();i++)
				{
					Card card=cardList.get(i);
					map.put(i,card.getTitle());
				}
				return map;
			}
		}
		return map;
	}

	@Transactional
	public List<String> changeCardToPublic(long groupNum, long userNum, Integer[] cardIndex)
	{
		Game game=gameDAO.findById(groupNum);
		if(game==null)
			return null;
		for(Player player:game.getPlayerList())
		{
			if(player.getUserId()==userNum)
			{
				List<String> resultList=new ArrayList<>();
				List<Card> cardList=player.getCardList();
				if(cardList==null||cardList.isEmpty())
					return null;
				cardList.sort(Comparator.comparingLong(Card::getId));
				List<Card> selectList=new ArrayList<>();
				for (int index : cardIndex)
				{
					Card card=cardList.get(index);
					if(card!=null)
						selectList.add(card);
				}
				selectList.forEach(card -> {
					if(card.getHasGet())
					{
						card.setHasGet(false);
						cardDAO.save(card);
						resultList.add(card.getTitle());
					}
				});
				return resultList;
			}
		}
		return null;
	}

	@Transactional
	public List<String> changeCardToPrivateById(Long[] cardId)
	{
		var resultString=new ArrayList<String>();
		var selectList=new ArrayList<Card>();
		for (long l : cardId)
		{
			var card=cardDAO.findById(l);
			if(card!=null)
				selectList.add(card);
		}
		selectList.forEach(card -> {
			if(!card.getHasGet())
			{
				card.setHasGet(true);
				cardDAO.update(card);
				resultString.add(card.getTitle());
			}
		});
		return resultString;
	}

	@Transactional
	public List<String> changeCardToPrivate(long groupNum,long userNum,Integer[] index)
	{
		Game game=gameDAO.findById(groupNum);
		if(game==null)
			return null;
		for(Player player:game.getPlayerList())
		{
			if(player.getUserId()==userNum)
			{
				var stringList=new ArrayList<String>();
				List<Card> cardList=player.getCardList();
				if(cardList==null||cardList.isEmpty())
					return null;
				cardList.sort(Comparator.comparingLong(Card::getId));
				var selectList=new ArrayList<Card>();
				for (int i : index)
				{
					var card=cardList.get(i);
					if(card!=null)
						selectList.add(card);
				}
				selectList.forEach(card -> {
					if(!card.getHasGet())
					{
						card.setHasGet(true);
						cardDAO.update(card);
						stringList.add(card.getTitle());
					}
				});
				return stringList;
			}
		}
		return null;
	}

	/**
	 * 删除一张卡
	 * @param groupNum
	 * @param userNum
	 * @param cardIndex 卡片序号
	 */
	@Transactional
	public List<String> removeCard(long groupNum,long userNum,Integer[] cardIndex)
	{
		Game game=gameDAO.findById(groupNum);
		if(game==null)
			return null;
		for (Player player:game.getPlayerList())
		{
			if(player.getUserId()==userNum)
			{
				var stringList=new ArrayList<String>();
				List<Card> cardList=player.getCardList();
				if(cardList==null||cardList.isEmpty())
					return null;
				cardList.sort(Comparator.comparingLong(Card::getId));
				var selectList=new ArrayList<Card>();
				for (int index : cardIndex)
				{
					var card=cardList.get(index);
					if(card!=null)
						selectList.add(card);
				}
				selectList.forEach(card -> {
					card.setPlayerId(null);
					card.setGameId(null);
					cardDAO.delete(card.getId());
					stringList.add(card.getTitle());
				});
				return stringList;
			}
		}
		return null;
	}

	@Transactional
	public int getCardNum(long group, String cardName)
	{
		int sum=0;
		List<Card> cardList = cardDAO.findByTitle(cardName);
		if (cardList == null)
			return 0;
		for(Card card:cardList)
			if (card.getGameId().getId()==group)
				sum++;
		return sum;
	}

	@Transactional
	public int getTeam(long group,long user)
	{
		Game game=gameDAO.findById(group);
		for(Player player:game.getPlayerList())
		{
			if(player.getUserId()==user)
				return player.getTeam();
		}
		return -1;
	}

	@Transactional
	public boolean setLight(long group,long user,int light)
	{
		Game game=gameDAO.findById(group);
		for(Player player:game.getPlayerList())
		{
			if(player.getUserId()==user)
			{
				player.setLight(light);
				playerDAO.update(player);
				return true;
			}
		}
		return false;
	}

	@Transactional
	public int getLight(long group,long user)
	{
		Game game=gameDAO.findById(group);
		for(Player player:game.getPlayerList())
		{
			if(player.getUserId()==user)
				return player.getLight();
		}
		return -1;
	}

	@Transactional
	public boolean setHp(long group,long user,int hp)
	{
		Game game=gameDAO.findById(group);
		for(Player player:game.getPlayerList())
		{
			if(player.getUserId()==user)
			{
				player.setHp(hp);
				playerDAO.update(player);
				return true;
			}
		}
		return false;
	}

	@Transactional
	public int getHp(long group,long user)
	{
		Game game=gameDAO.findById(group);
		for(Player player:game.getPlayerList())
		{
			if(player.getUserId()==user)
				return player.getHp();
		}
		return -1;
	}

	@Transactional
	public boolean setDefense(long group,long user,int defense)
	{
		Game game=gameDAO.findById(group);
		for(Player player:game.getPlayerList())
		{
			if(player.getUserId()==user)
			{
				player.setDefense(defense);
				playerDAO.update(player);
				return true;
			}
		}
		return false;
	}

	@Transactional
	public int getDefense(long group,long user)
	{
		Game game=gameDAO.findById(group);
		for(Player player:game.getPlayerList())
		{
			if(player.getUserId()==user)
				return player.getDefense();
		}
		return -1;
	}
}
