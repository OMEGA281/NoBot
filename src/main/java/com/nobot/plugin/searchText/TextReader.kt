package com.nobot.plugin.searchText

import com.IceCreamQAQ.Yu.annotation.Event
import com.IceCreamQAQ.Yu.annotation.EventListener
import com.IceCreamQAQ.Yu.event.events.AppStartEvent
import com.nobot.plugin.searchText.entity.TextRecord
import com.nobot.system.annotation.CreateDir
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

@EventListener
@CreateDir("text")
class TextReader
{
	@Event
	public fun screen(event:AppStartEvent)
	{
		val dir=File("text")
		val files=dir.listFiles { _, name -> name.endsWith(".txt") }
	}
}

fun readFile(file: File): List<TextRecord>
{
	var list= mutableListOf<TextRecord>()

	var reader=BufferedReader(InputStreamReader(FileInputStream(file),"UTF-8"))
	var name:String?=null
	var synonym:List<String>?=null
	var visible:Boolean?=null
	var text:String?=null
	while (true)
	{
		var line = reader.readLine() ?: break
		if (line.startsWith("$$$"))
		else
		{
			if (line.toLowerCase().startsWith("name:"))
			{
				name = line.substring(5)
				continue
			}
			else if (line.toLowerCase().startsWith("synonym:"))
			{
				synonym = line.substring(8).split(',')
				continue
			}
			else if (line.toLowerCase().startsWith("visible:"))
			{
				visible = line.substring(8).toBoolean()
				continue
			}
			else if (line.toLowerCase().startsWith("text:"))
			{
				text = line.substring(5)
				continue
			}
			else if (line == "#")
			{
				fun clearRecord()
				{
					name = null
					synonym = null
					visible = null
					text = null
				}

				var textRecord = TextRecord(null,name ?: continue, synonym, visible ?: true, text ?: "")
				list.add(textRecord)
				clearRecord()
			}
		}
	}
	return list
}