package com.nobot.plugin.girlFriend.service;

import lombok.Getter;
import lombok.var;
import net.coobird.thumbnailator.Thumbnails;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImageGenerationService
{
	private enum Layout
	{
		w2(2), w3(3), w4(4), w5(5), w6(6), w7(7), w8(8), w9(9), w10(10), w11(11), w12(12), w13(13);
		@Getter
		private int width;

		Layout(int i)
		{
			this.width = i;
		}
	}

	public BufferedImage makeImage(List<Map.Entry<String, File>> imageList) throws IOException
	{
		Layout layout;
		if (imageList.size() == 0)
			return null;
		else if (imageList.size() < 6)
			layout = Layout.w2;
		else if (imageList.size() < 12)
			layout = Layout.w3;
		else if (imageList.size() < 20)
			layout = Layout.w4;
		else if (imageList.size() < 35)
			layout = Layout.w5;
		else if (imageList.size() < 48)
			layout = Layout.w6;
		else if (imageList.size() < 63)
			layout = Layout.w7;
		else if (imageList.size() < 80)
			layout = Layout.w8;
		else if (imageList.size() < 99)
			layout = Layout.w9;
		else if (imageList.size() < 120)
			layout = Layout.w10;
		else if (imageList.size() < 143)
			layout = Layout.w11;
		else if (imageList.size() < 170)
			layout = Layout.w12;
		else
			layout = Layout.w13;

		int width = layout.width;
		int height = imageList.size() / width + 1;

		BufferedImage image = new BufferedImage(width * 300 + (width + 1) * 30
				, height * (300 + 50) + (height + 1) * 30, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = image.createGraphics();
		graphics2D.setColor(Color.WHITE);
		graphics2D.fillRect(0, 0, image.getWidth(), image.getHeight());
		int x = 1, y = 1;
		for (Map.Entry<String, File> entry : imageList)
		{
			int imageWidth = (x - 1) * 300 + x * 30;
			int imageHeight = (y - 1) * (300 + 50) + y * 30;
			int textWidth = imageWidth;
			int textHeight = imageHeight + 300;

			BufferedImage key = new BufferedImage(300, 50, BufferedImage.TYPE_INT_RGB);
			Graphics2D keyGraphics = key.createGraphics();

			Font font = new Font("宋体", Font.BOLD, 33);
			keyGraphics.setFont(font);
			keyGraphics.rotate(0f);
			keyGraphics.setColor(Color.WHITE);
			keyGraphics.setBackground(Color.WHITE);
			keyGraphics.fillRect(0, 0, 300, 50);
			keyGraphics.setColor(Color.BLACK);
			keyGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			FontMetrics fontMetrics = keyGraphics.getFontMetrics(font);
			int tW = fontMetrics.stringWidth(entry.getKey());
			int startPlace;
			while (tW > 300)
			{
				font = new Font("宋体", Font.BOLD, font.getSize() - 2);
				keyGraphics.setFont(font);
				fontMetrics = keyGraphics.getFontMetrics(font);
				tW = fontMetrics.stringWidth(entry.getKey());
			}
			startPlace = (300 - tW) / 2;
			keyGraphics.drawString(entry.getKey(), startPlace, 35);
			keyGraphics.dispose();

			try
			{
				BufferedImage value = Thumbnails.of(entry.getValue()).size(300, 300).asBufferedImage();

				graphics2D.drawImage(Thumbnails.of(value).size(300, 300)
								.sourceRegion(0, 0, 300, 300).asBufferedImage(),
						imageWidth, imageHeight, null);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			graphics2D.drawImage(key, textWidth, textHeight, null);
			x++;
			if (x > width)
			{
				x = 1;
				y++;
			}
		}
		graphics2D.dispose();
		return image;
	}

	public java.util.List<File> makeImage(Map<String, File> imageList) throws IOException
	{
		var list = new ArrayList<File>();
		var stream = imageList.entrySet().stream().sorted(Map.Entry.comparingByKey());
		var itemList = new ArrayList<Map.Entry<String, File>>();
		stream.forEach(entry -> {
			itemList.add(entry);
			if (imageList.size() >= 80)
			{
				try
				{
					var image = makeImage(itemList);
					var file = new File(image.hashCode() + ".jpg");
					Thumbnails.of(image).size(2000, 2000).outputFormat("jpg").toFile(file);
					list.add(file);
				}
				catch (IOException ignored)
				{
				}
				finally
				{
					itemList.clear();
				}
			}
		});
		if (!itemList.isEmpty())
		{
			try
			{
				var image = makeImage(itemList);
				var file = new File(image.hashCode() + ".jpg");
				Thumbnails.of(image).size(2000, 2000).outputFormat("jpg").toFile(file);
				list.add(file);
			}
			catch (IOException ignored)
			{
			}
			finally
			{
				itemList.clear();
			}
		}
		return list;
	}
}
