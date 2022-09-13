package com.nobot.tool.fileHelper;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FileOrFolderHasExistException extends Exception
{
	public boolean isFile;
	public boolean isSameType;
	String fileOrFolderName;
}
