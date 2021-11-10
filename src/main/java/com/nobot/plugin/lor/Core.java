package com.nobot.plugin.lor;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.icecreamqaq.yuq.YuQ;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.controller.BotActionContext;
import com.icecreamqaq.yuq.controller.ContextSession;
import com.icecreamqaq.yuq.controller.QQController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.error.WaitNextMessageTimeoutException;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItem;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import com.icecreamqaq.yuq.message.MessageLineQ;
import com.nobot.plugin.lor.entity.Card;
import com.nobot.plugin.lor.entity.Game;
import com.nobot.plugin.lor.service.Service;
import lombok.NonNull;
import lombok.var;

import javax.inject.Inject;
import java.util.*;

@GroupController
public class Core extends QQController implements PermissionInfo
{
	private class LightOutOfBoundException extends Exception
	{
	}

	private final int PLAYER_AND_INITIATOR = 1;
	private final int ONLY_PLAYER = 2;
	private final int ONLY_INITIATOR = 3;

	@Inject
	private Service service;
	@Inject
	private MessageItemFactory factory;
	@Inject
	private YuQ yuQ;

	private Random random = new Random();
	private Map<Long, Map<Integer, Long>> speedMap = new HashMap<>();

	@Before
	public void addPermissionInfo(BotActionContext actionContext, Member qq)
	{
		int permissionLevel;
		if (qq.isOwner())
			permissionLevel = GROUP_OWNER;
		else if (qq.isAdmin())
			permissionLevel = GROUP_ADMIN;
		else
			permissionLevel = GROUP_MEMBER;
		actionContext.set("permissionLevel", permissionLevel);
	}

	@Action("主持游戏")
	public Message putCard(long group, long qq, ContextSession session, int permissionLevel)
	{
		Game game = service.getGame(group);
		if (game != null)
		{
			if (game.getInitiator() == qq)
			{
				if (game.getState() == Game.State.ACTIVE)
				{
					reply("本群你正在主持的游戏还在继续");
					return null;
				}
				else
				{
					reply("本群存在着由你正在主持的游戏\r\n回复“继续”来继续游戏或者“覆盖”来删除以往新建一个游戏(15s)");
					Message message;
					try
					{
						message = session.waitNextMessage(15 * 1000);
					}
					catch (WaitNextMessageTimeoutException e)
					{
						return null;
					}
					if (message.getCodeStr().equals("继续"))
					{
						service.setGameState(group, Game.State.ACTIVE);
						return new Message().plus("继续之前的游戏");
					}
					else if (message.getCodeStr().equals("覆盖"))
					{
						service.removeGame(group);
						service.initGame(group, qq);
						service.addPlayerToGame(group, qq, 0);
						return new Message().plus("清空之前的记录，重开游戏\r\n请参加人员输入”加入队伍{数字}“来加入游戏和队伍中");
					}
					else
						return null;
				}
			}
			else
			{
				reply(new Message().plus("本群").plus(factory.at(game.getInitiator())).plus("主持的游戏还存在\r\n")
						.plus("你可以要求主持人主动”删除游戏“，或者联系管理员及以上权限人员”强制删除游戏“。"));
				return null;
			}
		}
		else
		{
			service.initGame(group, qq);
			service.addPlayerToGame(group, qq, 0);
			return new Message().plus("开始新的游戏\r\n请参加人员输入”加入队伍{数字}“来加入游戏和队伍中");
		}
	}

	@Action("强制保存游戏")
	public Message forceSaveGame(long group, int permissionLevel)
	{
		if (permissionLevel < GROUP_ADMIN)
			return null;
		service.setGameState(group, Game.State.SLEEP);
		return new Message().plus("成功保存游戏");
	}

	@Action("保存游戏")
	public Message saveGame(long group, long qq)
	{
		var game = service.getGame(group);
		if (game == null)
			return null;
		if (game.getInitiator() == qq)
			return forceSaveGame(group, GROUP_OWNER);
		return null;
	}

	@Action("强制删除游戏")
	public Message forceDeleteGame(long group, int permissionLevel)
	{
		if (permissionLevel < GROUP_ADMIN)
			return null;
		service.removeGame(group);
		return new Message().plus("成功删除游戏");
	}

	@Action("删除游戏")
	public Message deleteGame(long group, long qq)
	{
		var game = service.getGame(group);
		if (game == null)
			return null;
		if (game.getInitiator() == qq)
			return forceDeleteGame(group, GROUP_OWNER);
		return null;
	}

	@Action("加入队伍{num}")
	public Message addTeam(long group, long qq, String num)
	{
		if (!service.isGameActive(group)) return null;
		if (service.getGame(group).getInitiator() == qq)
			return new Message().plus("你是主持人");
		var index = 0;
		try
		{
			index = Integer.parseInt(num);
		}
		catch (NumberFormatException e)
		{
			return new Message().plus("请输入一个在1~99之间的队伍代码");
		}
		if (index <= 0 | index > 100)
			return new Message().plus("队伍代码在1~99之间");
		service.addPlayerToGame(group, qq, index);
		var list = service.listGameTeam(group, index);
		var message = new Message().plus("加入队伍" + num);
		if (list.size() > 0)
		{
			message.plus("\r\n队伍中目前还有：");
			for (long l : list)
				message.plus(factory.at(l));
		}
		return message;
	}

	@Action("录入卡片")
	@Synonym({"录入卡牌", "录入书页"})
	public Message putCard(long group, long qq, ContextSession session)
	{
		if (!checkAccess(group, qq, PLAYER_AND_INITIATOR)) return null;

		reply("请录入卡片(30s)\r\n一行一张卡\r\n新录入的卡片会清空之前的牌库包括手牌，请谨慎");
		var strings = getStringArray(session, 30 * 1000);
		if (strings == null) return null;

		service.clearCard(group, qq);
		service.addCard(group, qq, strings);

		return new Message().plus("覆盖成功");
	}

	@Action("添加卡片")
	@Synonym({"添加卡牌", "添加书页"})
	public MessageLineQ addCard(long group, long qq, ContextSession session)
	{
		if (!checkAccess(group, qq, PLAYER_AND_INITIATOR)) return null;

		reply("请录入卡片(30s)\r\n一行一张卡");
		var strings = getStringArray(session, 30 * 1000);
		if (strings == null)
			return null;

		service.addCard(group, qq, strings);

		var message = new Message().lineQ().text("你添加了：");
		for (String string : strings)
			message.plus(getRainCode(string)).text(",");
		return message;
	}

	@Action("删除卡片{num}")
	@Synonym({"删除卡牌{num}", "删除书页{num}"})
	public MessageLineQ removeCard(long group, long qq, String num, ContextSession session)
	{
		if (!checkAccess(group, qq, PLAYER_AND_INITIATOR)) return null;

		var indexArray = getIndexArray(num);
		var message = new Message().lineQ().text("你删除了:");
		var stringList = service.removeCard(group, qq, indexArray);
		if (stringList == null)
			message.text("无卡牌");
		else
			stringList.forEach(s -> message.plus(getRainCode(s)).text(","));

		return message;
	}

	@Action("替换卡片{num}")
	@Synonym({"替换卡牌{num}", "替换书页{num}"})
	public MessageLineQ replaceCard(long group, long qq, String num, ContextSession session)
	{
		if (!checkAccess(group, qq, PLAYER_AND_INITIATOR)) return null;

		reply("请录入卡片(30s)\r\n一行一张卡\r\n本来在你手牌中的卡片被替换后不会进入你的手牌");
		var strings = getStringArray(session, 30 * 1000);
		if (strings == null)
			return null;

		var indexArray = getIndexArray(num);
		var message = new Message().lineQ().text("你将");
		var stringList = service.removeCard(group, qq, indexArray);
		if (stringList == null)
			message.text("无卡牌");
		else
			stringList.forEach(s -> message.plus(getRainCode(s)).text(","));
		message.text("替换成：");
		service.addCard(group, qq, strings);
		for (String string : strings)
			message.plus(getRainCode(string)).text(",");

		return message;
	}

	@Action("抽取{num}张卡片")
	@Synonym({"抽取{num}张卡牌", "抽取{num}张书页", "抽取{num}次卡牌", "抽取{num}次书页"})
	public MessageLineQ drawCard(long group, Member qq, String num, ContextSession session)
	{
		if (!checkAccess(group, qq.getId(), PLAYER_AND_INITIATOR)) return null;

		var friend = yuQ.getFriends().get(qq.getId());
		if (friend == null)
			return new Message().lineQ().text("请先加我好友用于发布信息");

		var cardList = service.getFreeCard(group, qq.getId());
		if (cardList.isEmpty())
			return new MessageLineQ(new Message()).text("牌库已空");

		int time;
		try
		{
			time = Integer.parseInt(num);
		}
		catch (NumberFormatException e)
		{
			return new MessageLineQ(new Message()).text("请输入正确的数量");
		}

		if (time <= 0 || time > cardList.size())
			return new MessageLineQ(new Message()).text("请输入正确的数量");

		var message = new Message().lineQ().text("你抽到了:");
		Collections.shuffle(cardList);
		var selectCardList = cardList.subList(0, time);
		service.changeCardToPrivateById(selectCardList.stream().map(Card::getId).toArray(Long[]::new));
		selectCardList.forEach(card -> message.plus(getRainCode(card.getTitle())).text(","));
		message.text("\r\n");
		message.text("你有").text(String.valueOf(service.getLight(group, qq.getId()))).text("点光芒\r\n");

		message.text("你的手牌：\r\n");
		var myCards = service.getHeadCardList(group, qq.getId());
		myCards.entrySet().stream().sorted(Comparator.comparingLong(Map.Entry::getKey))
				.forEach(integerStringEntry ->
						message.text(String.valueOf(integerStringEntry.getKey()))
								.text(":")
								.plus(getRainCode(integerStringEntry.getValue()))
								.text("\r\n"));
		friend.sendMessage(message);
		return new MessageLineQ(new Message())
				.text(qq.getNameCard().isEmpty() ? qq.getName() : qq.getNameCard()).text("抽了" + num + "张牌");
	}

	@Action("抽取卡片")
	@Synonym({"抽取卡牌", "抽取书页"})
	public MessageLineQ drawACard(long group, Member qq, ContextSession session)
	{
		return drawCard(group, qq, "1", session);
	}

	@Action("拿取卡片{num}")
	@Synonym({"拿取卡牌{num}", "拿取书页{num}"})
	public MessageLineQ getCard(long group, Member qq, String num, ContextSession session)
	{
		if (!checkAccess(group, qq.getId(), PLAYER_AND_INITIATOR)) return null;

		var friend = yuQ.getFriends().get(qq.getId());
		if (friend == null)
			return new Message().lineQ().text("请先加我好友用于发布信息");

		var indexArray = getIndexArray(num);
		var message = new Message().lineQ().text("你拿取了:");
		var stringList = service.changeCardToPrivate(group, qq.getId(), indexArray);
		if (stringList == null)
			message.text("无卡牌");
		else
			stringList.forEach(s -> message.plus(getRainCode(s)).text(","));

		return message;
	}

	@Action("使用卡片{num}")
	@Synonym({"使用卡牌{num}", "使用书页{num}"})
	public MessageLineQ useCard(long group, Member qq, String num)
	{
		if (!checkAccess(group, qq.getId(), PLAYER_AND_INITIATOR)) return null;

		var indexArray = getIndexArray(num);
		var message = new Message().lineQ().text("你使用了:");
		var stringList = service.changeCardToPublic(group, qq.getId(), indexArray);
		if (stringList == null)
			message.text("无卡牌");
		else
			stringList.forEach(s -> message.plus(getRainCode(s)).text(","));

		return message;
	}

	@Action("查看手牌")
	public Message showCard(Group group, Member qq)
	{
		if (!checkAccess(group.getId(), qq.getId(), PLAYER_AND_INITIATOR)) return null;

		var friend = yuQ.getFriends().get(qq.getId());
		if (friend == null)
			return new Message().plus("请先加我好友用于发布信息");

		var message = new Message().lineQ();
		var myTeam = service.listGameTeam(group.getId(), service.getTeam(group.getId(), qq.getId()));
		message.text("你有").text(String.valueOf(service.getLight(group.getId(), qq.getId()))).text("点光芒\r\n");
		myTeam.forEach(aLong -> {
			var member = group.getMembers().get(aLong);
			message.text(member.getNameCard().isEmpty() ? member.getName() : member.getNameCard()).text("的手牌如下:\r\n");
			var memberCards = service.getHeadCardList(group.getId(), aLong);
			memberCards.entrySet().stream().sorted(Comparator.comparingLong(Map.Entry::getKey))
					.forEach(integerStringEntry ->
							message.text(String.valueOf(integerStringEntry.getKey()))
									.text(":")
									.plus(getRainCode(integerStringEntry.getValue()))
									.text("\r\n"));
		});
		friend.sendMessage(message);
		return null;
	}

	@Action("查看牌库")
	public Message showAllCard(Group group, Member qq)
	{
		if (!checkAccess(group.getId(), qq.getId(), PLAYER_AND_INITIATOR)) return null;

		var friend = yuQ.getFriends().get(qq.getId());
		if (friend == null)
			return new Message().plus("请先加我好友用于发布信息");

		var message = new Message().lineQ();
		var myTeam = service.listGameTeam(group.getId(), service.getTeam(group.getId(), qq.getId()));
		myTeam.forEach(aLong -> {
			var member = group.getMembers().get(aLong);
			message.text(member.getNameCard().isEmpty() ? member.getName() : member.getNameCard()).text("的牌库如下:\r\n");
			var memberCards = service.getAllCardList(group.getId(), aLong);
			memberCards.entrySet().stream().sorted(Comparator.comparingLong(Map.Entry::getKey))
					.forEach(integerStringEntry ->
							message.text(String.valueOf(integerStringEntry.getKey()))
									.text(":")
									.plus(getRainCode(integerStringEntry.getValue()))
									.text("\r\n"));
		});
		friend.sendMessage(message);
		return null;
	}

	@Action("图书馆指令")
	public MessageLineQ showMyHelp()
	{
		return new MessageLineQ(new Message())
				.text("主持游戏：以你为主持人开始或者继续一局游戏").text("\r\n")
				.text("[强制]删除游戏：关闭本局游戏，数据资料将一并删除。管理员及以上权限可以强制删除。").text("\r\n")
				.text("[强制]保存游戏：暂停本局游戏，数据资料将保存，大部分命令将静默。管理员及以上权限可以强制保存。").text("\r\n")
				.text("加入队伍{队伍号码}：加入到该队伍中，同时也代表着加入游戏。").text("\r\n")
				.text("录入书页：一行一个，录入你的书页，列表将会覆盖之前你的信息。（支持图片等奇怪东西）").text("\r\n")
				.text("添加书页：一行一个，向你的牌库添加你的书页。（支持图片等奇怪东西）").text("\r\n")
				.text("删除书页{书页序号}：多个序号使用逗号分割，删除你的书页。").text("\r\n")
				.text("替换书页{要删除的书页序号}：序号使用逗号分割，删除你输入的书页，然后将你添加的新书页送入牌库。被替换的书页在手牌的时候，替换的卡片不会进入手牌。").text("\r\n")
				.text("抽取[{抽取张数}张]书页：从牌库中随机抽取制定的卡片张数。").text("\r\n")
				.text("拿取书页{书页序号}：多个序号使用逗号分割，直接从牌库中拿取指定卡片").text("\r\n")
				.text("使用书页{书页序号}：多个序号使用逗号分割，使用你手牌中的卡片到牌库中。").text("\r\n")
				.text("查看手牌：显示你和你队友的手牌。").text("\r\n")
				.text("查看牌库：显示你的牌库。").text("\r\n")
				.text("[速度]r{num1}t{num2}：获得num1~num2之间的随机数，若加上速度前缀则自动计入速度列表中").text("\r\n")
				.text("{光芒|生命|架势}{+-}{num}：光芒或者生命或者架势增加或减少。").text("\r\n")
				.text("设置{光芒|生命|架势}{num}：直接设置光芒或者生命或者架势。").text("\r\n")
				.text("重置速度列表：清空速度列表").text("\r\n")
				.text("查看速度列表：将当前的速度列表显示出来");
	}

	@Action("r{num1}t{num2}")
	public MessageLineQ getRandom(String num1, String num2)
	{
		int result = 0;
		try
		{
			result = specialRandom(num1, num2);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
		return new MessageLineQ(new Message()).text("r").text(num1).text("~").text(num2).text("=").text(String.valueOf(result));
	}

	@Action("光芒{symbol:[＋+-－]}{num}")
	public MessageLineQ changeLight(long group, long qq, String symbol, String num)
	{
		if (!checkAccess(group, qq, PLAYER_AND_INITIATOR)) return null;
		int number;
		try
		{
			number = Integer.parseInt(num);

		}
		catch (NumberFormatException e)
		{
			return null;
		}
		switch (symbol)
		{
			case "＋":
				symbol = "+";
			case "+":
				break;
			case "－":
				symbol = "-";
			case "-":
				number = -number;
				break;
			default:
				return null;
		}
		int now;
		try
		{
			now = addLight(group, qq, number, false);
		}
		catch (LightOutOfBoundException e)
		{
			return new MessageLineQ(new Message()).text("使用后光芒值将小于0");
		}
		return new MessageLineQ(new Message()).text("你").text(symbol.equals("+") ? "获得" : "失去")
				.text("了").text(num).text("点光芒，现在有" + now + "点光芒");
	}

	@Action("设置光芒{num}")
	public MessageLineQ setLight(long group, long qq, String num)
	{
		if (!checkAccess(group, qq, PLAYER_AND_INITIATOR)) return null;
		int number;
		try
		{
			number = Integer.parseInt(num);

		}
		catch (NumberFormatException e)
		{
			return null;
		}
				service.setLight(group, qq, number);
		return new MessageLineQ(new Message()).text("你将光芒设置为" + num);
	}

	@Action("生命{symbol:[＋+-－]}{num}")
	public MessageLineQ changeHp(long group, long qq, String symbol, String num)
	{
		if (!checkAccess(group, qq, PLAYER_AND_INITIATOR)) return null;
		int number;
		try
		{
			number = Integer.parseInt(num);

		}
		catch (NumberFormatException e)
		{
			return null;
		}
		switch (symbol)
		{
			case "＋":
				symbol = "+";
			case "+":
				break;
			case "－":
				symbol = "-";
			case "-":
				number = -number;
				break;
			default:
				return null;
		}
		int now= addHp(group, qq, number);
		return new MessageLineQ(new Message()).text("你").text(symbol.equals("+") ? "获得" : "失去")
				.text("了").text(num).text("点生命，现在有" + now + "点生命");
	}

	@Action("设置生命{num}")
	public MessageLineQ setHp(long group, long qq, String num)
	{
		if (!checkAccess(group, qq, PLAYER_AND_INITIATOR)) return null;
		int number;
		try
		{
			number = Integer.parseInt(num);

		}
		catch (NumberFormatException e)
		{
			return null;
		}
		service.setHp(group, qq, number);
		return new MessageLineQ(new Message()).text("你将生命设置为" + num);
	}

	@Action("架势{symbol:[＋+-－]}{num}")
	public MessageLineQ changeDefense(long group, long qq, String symbol, String num)
	{
		if (!checkAccess(group, qq, PLAYER_AND_INITIATOR)) return null;
		int number;
		try
		{
			number = Integer.parseInt(num);

		}
		catch (NumberFormatException e)
		{
			return null;
		}
		switch (symbol)
		{
			case "＋":
				symbol = "+";
			case "+":
				break;
			case "－":
				symbol = "-";
			case "-":
				number = -number;
				break;
			default:
				return null;
		}
		int now= addDefense(group, qq, number);
		return new MessageLineQ(new Message()).text("你").text(symbol.equals("+") ? "获得" : "失去")
				.text("了").text(num).text("点架势，现在有" + now + "点架势");
	}

	@Action("设置架势{num}")
	public MessageLineQ setDefense(long group, long qq, String num)
	{
		if (!checkAccess(group, qq, PLAYER_AND_INITIATOR)) return null;
		int number;
		try
		{
			number = Integer.parseInt(num);

		}
		catch (NumberFormatException e)
		{
			return null;
		}
		service.setDefense(group, qq, number);
		return new MessageLineQ(new Message()).text("你将架势设置为" + num);
	}

	@Action("重置速度列表")
	public MessageLineQ resetSpeedList(long group, long qq)
	{
		if (!checkAccess(group, qq, ONLY_INITIATOR)) return null;
		speedMap.remove(group);
		return new MessageLineQ(new Message()).text("删除了目前速度列表");
	}

	@Action("速度r{num1}t{num2}")
	public MessageLineQ speedRandom(long group, long qq, String num1, String num2)
	{
		if (!checkAccess(group, qq, PLAYER_AND_INITIATOR)) return null;
		int result;
		try
		{
			result = specialRandom(num1, num2);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
		var map = speedMap.computeIfAbsent(group, k -> new HashMap<>());
		map.put(result, qq);
		return new MessageLineQ(new Message()).text("记录速度")
				.text("r").text(num1).text("~").text(num2).text("=").text(String.valueOf(result));
	}

	@Action("查看速度列表")
	public MessageLineQ showSpeedList(long group, long qq)
	{
		if (!checkAccess(group, qq, PLAYER_AND_INITIATOR)) return null;
		var map = speedMap.get(group);
		if (map == null)
			return new MessageLineQ(new Message()).text("不存在速度列表");
		var message = new MessageLineQ(new Message());
		message.text("速度列表如下：\r\n");
		map.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey)).forEach(integerLongEntry ->
				message.text(String.valueOf(integerLongEntry.getKey())).text(" ")
						.plus(factory.at(integerLongEntry.getValue())).text("\r\n"));
		return message;
	}

	@Action("状态显示")
	public MessageLineQ showMyInfo(long group,long qq)
	{
		if(!checkAccess(group,qq,PLAYER_AND_INITIATOR)) return null;
		var message=new MessageLineQ(new Message()).at(qq);
		message.text("光芒:").text(String.valueOf(service.getLight(group,qq))).text("\t")
				.text("生命:").text(String.valueOf(service.getHp(group,qq))).text("\t")
				.text("架势:").text(String.valueOf(service.getDefense(group,qq))).text("\t")
				.text("手牌:").text(String.valueOf(service.getHeadCardList(group,qq).size())).text("张\t")
				.text("牌库:").text(String.valueOf(service.getAllCardList(group,qq).size())).text("张");
		return message;
	}

	private int specialRandom(String num1, String num2) throws NumberFormatException
	{
		int top, floor;
		top = Integer.parseInt(num2);
		floor = Integer.parseInt(num1);
		if (top < floor)
			throw new NumberFormatException();
		int result;
		if (top == floor)
			result = top;
		else
			result = random.nextInt(top - floor + 1) + floor;
		return result;
	}

	private int addLight(long group, long user, int num, boolean force) throws LightOutOfBoundException
	{
		var light = service.getLight(group, user);
		light = light + num;
		if (light < 0 && !force)
			throw new LightOutOfBoundException();
		service.setLight(group, user, light);
		return light;
	}

	private int addHp(long group, long user, int num)
	{
		var hp = service.getHp(group, user);
		hp = hp + num;
		if (hp < 0)
			hp = 0;
		service.setHp(group, user, hp);
		return hp;
	}

	private int addDefense(long group, long user, int num)
	{
		var defense = service.getDefense(group, user);
		defense = defense + num;
		if (defense < 0)
			defense = 0;
		service.setDefense(group, user, defense);
		return defense;
	}

	private Integer[] getIndexArray(@NonNull String s)
	{
		var ss = s.split(",|，");
		var list = new ArrayList<Integer>();
		for (String s1 : ss)
		{
			try
			{
				list.add(Integer.parseInt(s1));
			}
			catch (NumberFormatException ignored)
			{
			}
		}
		return list.toArray(new Integer[0]);
	}

	private String[] getStringArray(ContextSession session, long time)
	{
		Message message;
		try
		{
			message = session.waitNextMessage(time);
		}
		catch (WaitNextMessageTimeoutException e)
		{
			return null;
		}
		var strings = new ArrayList<String>();
		message.getBody().forEach(messageItem -> {
			var part = messageItem.toMessage().getCodeStr().split("\r\n|\r|\n");
			strings.addAll(Arrays.asList(part));
		});
		return strings.toArray(new String[0]);
	}

	private boolean checkAccess(long group, long user, int type)
	{
		if (!service.isGameActive(group)) return false;
		var game = service.getGame(group);
		if (game == null) return false;
		var initiator = game.getInitiator();
		if (initiator == user && (type == PLAYER_AND_INITIATOR || type == ONLY_INITIATOR)) return true;

		if (type == ONLY_INITIATOR) return false;
		var players = game.getPlayerList();
		for (var player : players)
			if (player.getUserId() == user) return true;
		return false;
	}

	/**
	 * FIXME:老旧的损耗极大的算法
	 *
	 * @param s
	 * @return
	 */
	private MessageItem getRainCode(String s)
	{
		if (s.startsWith("<"))
			return Message.Companion.toMessageByRainCode(s).getBody().get(0);
		return factory.text(s);
	}
}
