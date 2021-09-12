package com.nobot.plugin.draw;

import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class xmlWriter
{
	private XMLOutputter xmlOutputter=new XMLOutputter(Format.getCompactFormat().setEncoding("UTF-8").setIndent("\t"));
	File file;
	xmlWriter(File file)
	{
		this.file=file;
	}
	public void write(Document document)
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
