package com.nobot.plugin.girlFriend;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.YuQ;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.event.GroupMessageEvent;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import net.coobird.thumbnailator.Thumbnails;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@GroupController
@EventListener
public class Gobang
{
	private interface GraphicsParams
	{
		int pieceSize = 30;
		int twoPiecesInterval = 12;
		int boardBoundary = 75;
		int textToLine = 15;

		int twoLineInterval = (pieceSize + twoPiecesInterval);
		int boardSize = twoLineInterval * 15 + boardBoundary * 2;
	}

	private interface PieceType
	{
		int BLACK = -1;
		int WHITE = 1;
		int NULL = 0;
	}

	private enum MatchState
	{WAITING, ONGOING_INITIATOR, ONGOING_OPPONENT, END}

	private class Checkerboard implements PieceType
	{

		int[][] board;

		Checkerboard()
		{
			board = new int[15][15];
		}

		public int getPiece(int x, int y)
		{
			return board[x][y];
		}

		public int setPiece(int x, int y, int piece)
		{
			int i = getPiece(x, y);
			board[x][y] = piece;
			return i;
		}

		public int checkResult()
		{
			for (int x = 0; x < board.length; x++)
			{
				for (int y = 0; y < board[x].length; y++)
				{
					int piece = board[x][y];
					if (piece == 0)
						continue;

//					左查询
					if (x >= 4)
					{
						boolean flag = true;
						for (int i = 1; i < 5; i++)
						{
							if (getPiece(x - i, y) != piece)
							{
								flag = false;
								break;
							}
						}
						if (flag)
							return piece;
					}
//					左上查询
					if (x >= 4 && y >= 4)
					{
						boolean flag = true;
						for (int i = 1; i < 5; i++)
						{
							if (getPiece(x - i, y - i) != piece)
							{
								flag = false;
								break;
							}
						}
						if (flag)
							return piece;
					}
//					上查询
					if (y >= 4)
					{
						boolean flag = true;
						for (int i = 1; i < 5; i++)
						{
							if (getPiece(x, y - i) != piece)
							{
								flag = false;
								break;
							}
						}
						if (flag)
							return piece;
					}
//					右上查询
					if (x <= 10 && y >= 4)
					{
						boolean flag = true;
						for (int i = 1; i < 5; i++)
						{
							if (getPiece(x + i, y - i) != piece)
							{
								flag = false;
								break;
							}
						}
						if (flag)
							return piece;
					}
//					右查询
					if (x <= 10)
					{
						boolean flag = true;
						for (int i = 1; i < 5; i++)
						{
							if (getPiece(x + i, y) != piece)
							{
								flag = false;
								break;
							}
						}
						if (flag)
							return piece;
					}
//					右下查询
					if (x <= 10 && y <= 10)
					{
						boolean flag = true;
						for (int i = 1; i < 5; i++)
						{
							if (getPiece(x + i, y + i) != piece)
							{
								flag = false;
								break;
							}
						}
						if (flag)
							return piece;
					}
//					下查询
					if (y <= 10)
					{
						boolean flag = true;
						for (int i = 1; i < 5; i++)
						{
							if (getPiece(x, y + i) != piece)
							{
								flag = false;
								break;
							}
						}
						if (flag)
							return piece;
					}
//					左下查询
					if (x >= 4 && y <= 10)
					{
						boolean flag = true;
						for (int i = 1; i < 5; i++)
						{
							if (getPiece(x - i, y + i) != piece)
							{
								flag = false;
								break;
							}
						}
						if (flag)
							return piece;
					}
				}
			}
			return NULL;
		}
	}

	private class GobangEntry implements GraphicsParams, PieceType
	{
		//		黑方为发起者，白方为接受者
		private long groupNum, initiator, opponent;
		private Checkerboard checkerboard;
		private MatchState matchState;

		private BufferedImage bufferedImage;
		private Graphics2D graphics2D;
		private final Font font = new Font("宋体", Font.BOLD, 20);

		GobangEntry(long groupNum, long initiator, long opponent)
		{
			this.groupNum = groupNum;
			this.initiator = initiator;
			this.opponent = opponent;
			checkerboard = new Checkerboard();
			matchState = MatchState.WAITING;

			bufferedImage = new BufferedImage(boardSize, boardSize, BufferedImage.TYPE_INT_RGB);
			graphics2D = bufferedImage.createGraphics();
			graphics2D.setColor(Color.WHITE);
			graphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
			graphics2D.setColor(Color.BLACK);
			graphics2D.setFont(font);
			FontMetrics fontMetrics = graphics2D.getFontMetrics(font);
			for (int i = 0; i < 15; i++)
			{
				int x = boardBoundary + twoLineInterval * (i)+twoLineInterval/2;
				graphics2D.drawLine(x, boardBoundary, x , boardSize - boardBoundary);
				String c = Character.toString((char) ('A' + i));
				int textWidth = fontMetrics.stringWidth(c);
				int textHeight = fontMetrics.getHeight();
				int text_x = x - textWidth / 2;
				int text_y = boardBoundary - textHeight - textToLine;
				graphics2D.drawString(c, text_x, text_y);
			}
			for (int i = 0; i < 15; i++)
			{
				int y = boardBoundary + twoLineInterval * (i)+twoLineInterval/2;
				graphics2D.drawLine(boardBoundary, y , boardSize - boardBoundary, y);
				String c = Integer.toString(1 + i);
				int textWidth = fontMetrics.stringWidth(c);
				int textHeight = fontMetrics.getHeight();
				int text_x = boardBoundary - textWidth - textToLine;
				int text_y = y + textHeight / 2;
				graphics2D.drawString(c, text_x, text_y);
			}

			Thread waitThread = new Thread(() -> {
				try
				{
					Thread.sleep(1000 * 60);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				if (matchState == MatchState.WAITING)
				{
					graphics2D.dispose();
					map.remove(groupNum);
				}
			});
			waitThread.start();
			Thread stopThread = new Thread(() -> {
				try
				{
					Thread.sleep(1000 * 60 * 20);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				if (matchState != MatchState.END)
				{
					if (matchState == MatchState.ONGOING_INITIATOR)
						timeout(groupNum, initiator);
					else if (matchState == MatchState.ONGOING_OPPONENT)
						timeout(groupNum, opponent);
				}
				graphics2D.dispose();
				map.remove(groupNum);
			});
			stopThread.start();
		}

		private int getPieceXY(int xy)
		{
			return boardBoundary + (twoLineInterval) * (xy - 1)+twoLineInterval/2-pieceSize/2;
		}

		public boolean setPiece(int x, int y)
		{
			if (matchState == MatchState.ONGOING_INITIATOR)
				return setPiece(x, y, BLACK);
			else if (matchState == MatchState.ONGOING_OPPONENT)
				return setPiece(x, y, WHITE);
			return false;
		}

		public boolean setPiece(int x, int y, int pieceType)
		{
			if (checkerboard.getPiece(x-1, y-1) != 0)
				return false;
			checkerboard.setPiece(x-1, y-1, pieceType);
			if (pieceType == BLACK)
				graphics2D.fillOval(getPieceXY(x), getPieceXY(y), pieceSize, pieceSize);
			else if (pieceType == WHITE)
				graphics2D.drawOval(getPieceXY(x), getPieceXY(y), pieceSize, pieceSize);
			return true;
		}

		public BufferedImage getBufferedImage()
		{
			return bufferedImage;
		}

		public MatchState turnState()
		{
			if (matchState == MatchState.ONGOING_INITIATOR)
				return matchState = MatchState.ONGOING_OPPONENT;
			if (matchState == MatchState.ONGOING_OPPONENT)
				return matchState = MatchState.ONGOING_INITIATOR;
			return matchState;
		}

		public int checkResult()
		{
			int i = checkerboard.checkResult();
			if (i == BLACK || i == WHITE)
			{
				matchState = MatchState.END;
				graphics2D.dispose();
				map.remove(groupNum);
			}
			return i;
		}

		public void end()
		{
			graphics2D.dispose();
			map.remove(groupNum);
		}
	}

	@Inject
	private MessageItemFactory factory;
	@Inject
	private YuQ yuQ;

	private ConcurrentHashMap<Long, GobangEntry> map = new ConcurrentHashMap<>();

	@Action("五子棋 {at}")
	public Message start(Member qq, Member at, Group group)
	{
		if (qq.getId() == at.getId())
			return new Message().plus("你自己跟自己玩？");

		if (map.containsKey(group.getId()))
			return new Message().plus("本群已有等待或进行中的比赛了");
		GobangEntry entry = new GobangEntry(group.getId(), qq.getId(), at.getId());
		map.put(group.getId(), entry);
		return new Message().plus(factory.at(at)).plus(qq.getNameCard())
				.plus("邀请你来一场五子棋，请在1分钟内发送\"同意\"，过时过期");
	}

	@Event(weight = Event.Weight.low)
	public void deal(GroupMessageEvent event) throws IOException
	{
		if (!event.getMessage().getCodeStr().equals("同意"))
			return;
		Group group= event.getGroup();
		Member qq= event.getSender();
		GobangEntry entry = map.get(group.getId());
		if (entry == null)
			throw new DoNone();
		if (entry.opponent != qq.getId())
			throw new DoNone();
		entry.matchState = MatchState.ONGOING_INITIATOR;

		BufferedImage image = map.get(group.getId()).getBufferedImage();
		File tmpFile = new File("tmp" + qq.getGroup().getId() + qq.getId() + ".jpg");
		Thumbnails.of(image).size(3000, 3000).outputFormat("jpg").toFile(tmpFile);
		Message message=new Message().plus("棋局开始，").plus(factory.at(entry.initiator))
				.plus("的回合\r\n发送\"放 {位置}\"如\"放 C12\"来下棋").plus(factory.imageByFile(tmpFile));

		group.sendMessage(message);
		tmpFile.delete();
	}

	@Before(/*only = {"drawCard","stopDrawCard"}*/except = {"start", "deal"})
	public GobangEntry check(Member qq, Group group)
	{
		GobangEntry entry = map.get(group.getId());
		if (entry == null)
			throw new DoNone();
		if (!((entry.opponent == qq.getId() && entry.matchState == MatchState.ONGOING_OPPONENT)
				|| (entry.initiator == qq.getId() && entry.matchState == MatchState.ONGOING_INITIATOR)))
			throw new DoNone();
		return entry;
	}

	@Action("放 {place}")
	public Message placePiece(Member qq, String place, GobangEntry gobangEntry) throws IOException
	{
		place = place.toUpperCase(Locale.ROOT);
		char x = place.charAt(0);
		String y = place.substring(1);
		if (!('A' <= x && x <= 'Z'))
			throw new NumberFormatException();
		int x_num = x - 'A' + 1;
		int y_num = Integer.parseInt(y);
		if (gobangEntry.setPiece(x_num, y_num))
		{
			BufferedImage image = gobangEntry.getBufferedImage();
			File tmpFile = new File("tmp" + qq.getGroup().getId() + qq.getId() + ".jpg");
			Thumbnails.of(image).size(3000, 3000).outputFormat("jpg").toFile(tmpFile);
			Message message = new Message().plus(factory.imageByFile(tmpFile));

			int i = gobangEntry.checkResult();
			if (i == PieceType.BLACK)
			{
				message.plus(factory.at(gobangEntry.initiator));
				message.plus("你赢了");
			}
			else if (i == PieceType.WHITE)
			{
				message.plus(factory.at(gobangEntry.opponent));
				message.plus("你赢了");
			}
			else
			{
				gobangEntry.turnState();
				message.plus("下一位");
				if (gobangEntry.matchState == MatchState.ONGOING_INITIATOR)
					message.plus(factory.at(gobangEntry.initiator));
				else if (gobangEntry.matchState == MatchState.ONGOING_OPPONENT)
					message.plus(factory.at(gobangEntry.opponent));
			}
			qq.getGroup().sendMessage(message);
			tmpFile.delete();
			return null;
		}
		else
			return new Message().plus("该处已有棋子了");
	}

	@Action("认输")
	public Message admitDefeat(Member qq, GobangEntry gobangEntry)
	{
		if (gobangEntry.initiator == qq.getId())
		{
			gobangEntry.end();
			return new Message().plus(factory.at(gobangEntry.opponent)).plus("你赢了");
		}
		else if (gobangEntry.opponent == qq.getId())
		{
			gobangEntry.end();
			return new Message().plus(factory.at(gobangEntry.initiator)).plus("你赢了");
		}
		return null;
	}

	public void timeout(long groupNum, long userNum)
	{
		Group group = yuQ.getGroups().get(groupNum);
		if (group == null)
			return;
		group.sendMessage(new Message().plus("超20分钟未回应"));
	}
}
