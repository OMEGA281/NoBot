package com.nobot.tool;


import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class XmlReader
{
	SAXBuilder saxBuilder = new SAXBuilder();

	public Document getDocument(String path) throws IOException, JDOMException
	{
		File file=new File(path);
		return getDocument(file);
	}

	public Document getDocument(File file) throws JDOMException, IOException
	{
		return saxBuilder.build(file);
	}

	public Document getDocument(InputStream inputStream) throws JDOMException, IOException
	{
		return saxBuilder.build(inputStream);
	}

}
