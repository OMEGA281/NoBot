package com.nobot.tool;

import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class XmlWriter
{
	private final XMLOutputter xmlOutputter=new XMLOutputter(Format.getCompactFormat().setEncoding("UTF-8").setIndent("\t"));
	public void write(File file,Document document)
	{
		try
		{
			FileWriter fileWriter=new FileWriter(file);
			xmlOutputter.output(document,fileWriter);
			fileWriter.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
