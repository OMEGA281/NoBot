package com.nobot.plugin.draw;


import lombok.Getter;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class XmlReader
{
	@Getter
	private Document document;
	XmlReader(File file)
	{
		SAXBuilder saxBuilder=new SAXBuilder();
		try
		{
			document=saxBuilder.build(file);
		}
		catch (JDOMException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	XmlReader(InputStream inputStream)
	{
		SAXBuilder saxBuilder=new SAXBuilder();
		try
		{
			document=saxBuilder.build(inputStream);
		}
		catch (JDOMException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
