package com.nobot.tool;

import net.coobird.thumbnailator.Thumbnails;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class ImageCompressor
{
	public static BufferedImage getSmallImage(BufferedImage image)throws IOException
	{
		return Thumbnails.of(image).size(3000,3000).asBufferedImage();
	}
	public static BufferedImage getSmallImage(File file)throws IOException
	{
		return Thumbnails.of(file).size(3000,3000).asBufferedImage();
	}
	public static void getSmallImage(File file,File outputFile)throws IOException
	{
		Thumbnails.of(file).size(3000,3000).toFile(outputFile);
	}
	public static void getSmallImage(BufferedImage image,File outputFile)throws IOException
	{
		Thumbnails.of(image).size(3000,3000).toFile(outputFile);
	}
	public static void getSmallImage(File file, OutputStream outputStream)throws IOException
	{
		Thumbnails.of(file).size(3000,3000).toOutputStream(outputStream);
	}
	public static void getSmallImage(BufferedImage image, OutputStream outputStream)throws IOException
	{
		Thumbnails.of(image).size(3000,3000).toOutputStream(outputStream);
	}
}
