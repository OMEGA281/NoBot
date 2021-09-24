package com.nobot.plugin.girlFriend.service;

import lombok.Getter;
import net.coobird.thumbnailator.Thumbnails;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImageGenerationService
{
	private enum Layout
	{
		w2(2),w3(3), w4(4), w5(5), w6(6),w7(7),w8(8);
		@Getter
		private int width;

		Layout(int i)
		{
			this.width = i;
		}
	}

	public BufferedImage makeImage(Map<String, File> imageList) throws IOException
	{
		HashMap<BufferedImage, BufferedImage> map = getImage(imageList);
		Layout layout;
		if (map.size() == 0)
			return null;
		else if (map.size()<6)
			layout= Layout.w2;
		else if (map.size() < 12)
			layout = Layout.w3;
		else if (map.size() < 20)
			layout = Layout.w4;
		else if (map.size() < 35)
			layout = Layout.w5;
		else if(map.size()<48)
			layout = Layout.w6;
		else if(map.size()<63)
			layout = Layout.w7;
		else
			layout = Layout.w8;

		int width= layout.width;
		int height=imageList.size()/width+1;

		BufferedImage image=new BufferedImage(
				width*300+(width+1)*30,height*(300+50)+(height+1)*30,BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D=image.createGraphics();
		graphics2D.setColor(Color.WHITE);
		graphics2D.fillRect(0,0,image.getWidth(),image.getHeight());
		int x=1,y=1;
		for (Map.Entry<BufferedImage,BufferedImage> entry:map.entrySet())
		{
			int imageWidth=(x-1)*300+x*30;
			int imageHeight=(y-1)*(300+50)+y*30;
			int textWidth=imageWidth;
			int textHeight=imageHeight+300;

			graphics2D.drawImage(entry.getValue(),imageWidth,imageHeight,null);
			graphics2D.drawImage(entry.getKey(),textWidth,textHeight,null);
			x++;
			if(x>width)
			{
				x=1;
				y++;
			}
		}
		graphics2D.dispose();
		return image;
	}

	private HashMap<BufferedImage, BufferedImage> getImage(Map<String, File> list)
	{
		HashMap<BufferedImage, BufferedImage> bufferedImages = new HashMap<>();
		for (Map.Entry<String, File> entry : list.entrySet())
		{
			File file = entry.getValue();
			try
			{
				BufferedImage key=new BufferedImage(300,50,BufferedImage.TYPE_INT_RGB);
				Graphics2D graphics2D= key.createGraphics();
//				key=graphics2D.getDeviceConfiguration().createCompatibleImage(300,100, Transparency.TRANSLUCENT);
//				graphics2D.dispose();
//				graphics2D= key.createGraphics();
				Font font=new Font("宋体",Font.BOLD,33);
				graphics2D.setFont(font);
				graphics2D.rotate(0f);
				graphics2D.setColor(Color.WHITE);
				graphics2D.setBackground(Color.WHITE);
				graphics2D.fillRect(0,0,300,50);
				graphics2D.setColor(Color.BLACK);
				graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				FontMetrics fontMetrics=graphics2D.getFontMetrics(font);
				int textWidth=fontMetrics.stringWidth(entry.getKey());
				int startPlace;
				while (textWidth>300)
				{
					font=new Font("宋体",Font.BOLD,font.getSize()-2);
					graphics2D.setFont(font);
					fontMetrics=graphics2D.getFontMetrics(font);
					textWidth=fontMetrics.stringWidth(entry.getKey());
				}
				startPlace=(300-textWidth)/2;
				graphics2D.drawString(entry.getKey(),startPlace,35);
				graphics2D.dispose();

				BufferedImage value=Thumbnails.of(file).size(300, 300).asBufferedImage();
				bufferedImages.put(key,value);
			}
			catch (IOException ignored)
			{
			}
		}
		return bufferedImages;
	}
}
