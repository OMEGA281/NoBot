package com.nobot.plugin.draw;


import org.jdom2.Document;

import java.io.File;

public class DefaultStarter
{
	public static void main(String[] args)
	{
		Document document=new XmlReader(new File("幻想入.xml")).getDocument();
		Card card=new Card(document);
		Draw draw=new Draw();
		String s=draw.draw(card,5);
		System.out.print(s);
	}
}
