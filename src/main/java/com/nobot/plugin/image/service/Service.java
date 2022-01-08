package com.nobot.plugin.image.service;

import com.icecreamqaq.yudb.jpa.annotation.Transactional;
import com.nobot.plugin.image.dao.ImageDAO;
import com.nobot.plugin.image.dao.RecorderDAO;
import com.nobot.plugin.image.dao.TagDAO;
import com.nobot.plugin.image.entity.ImageEntity;
import com.nobot.plugin.image.entity.OperationRecord;
import com.nobot.plugin.image.entity.TagEntity;
import com.nobot.system.annotation.CreateDir;
import com.nobot.system.annotation.CreateFile;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.var;

import javax.inject.Inject;
import java.io.File;
import java.util.*;

@CreateDir({"Image", "Image/setting"})
@CreateFile({"Image/setting/ImageTag.txt", "Image/setting/Record.txt"})
public class Service
{
	@Inject
	private ImageDAO imageDAO;
	@Inject
	private TagDAO tagDAO;
	@Inject
	private RecorderDAO recorderDAO;

	private LRU<Integer,String> map=new LRU<>(100);

	private class LRU<K,V> extends LinkedHashMap<K,V>
	{
		@Getter
		@Setter
		private int maxSize;

		public LRU(int maxSize)
		{
			this.maxSize=maxSize;
		}

		@Override
		protected boolean removeEldestEntry(Map.Entry<K, V> eldest)
		{
			return size()>maxSize;
		}
	}

	private final File imageRootDir = new File("Image");

	private File searchImage(String name, @NonNull File dir)
	{
		if (dir.isFile())
			return dir.getName().equals(name) ? dir : null;
		else
		{
			for (File file : dir.listFiles())
			{
				File aim = searchImage(name, file);
				if (aim != null)
					return aim;
			}
			return null;
		}
	}

	private ArrayList<File> getAllImage(@NonNull File dir)
	{
		ArrayList<File> list = new ArrayList<>();
		for (var file : dir.listFiles())
		{
			if (file.isFile())
			{
				String imageName = file.getName().toLowerCase(Locale.ROOT);
				if (imageName.endsWith(".jpg") || imageName.endsWith(".png") || imageName.endsWith(".bpm"))
					list.add(file);
			}
			else
			{
				ArrayList<File> subList = getAllImage(file);
				list.addAll(subList);
			}
		}
		return list;
	}

	public ArrayList<File> getImageByTagList(String tag)
	{
		if (tag == null || tag.isEmpty())
			return getAllImage(imageRootDir);

		char[] chars = tag.toCharArray();
		ArrayList<String> tagList = new ArrayList<>();
		ArrayList<Character> symbolList = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		/*0：刚启动，1：tag，2：符号*/
		int type = 0;
		for (char c : chars)
		{
			switch (c)
			{
				case '|':
				case '&':
					if (type != 2)
					{
						if (builder.length() != 0)
						{
							tagList.add(builder.toString());
							builder = new StringBuilder();
						}
						symbolList.add(c);
					}
					type = 2;
					break;
				default:
					builder.append(c);
					type=1;
					break;
			}
		}
		if(builder.length()!=0)
			tagList.add(builder.toString());

		ArrayList<File> fileList;
		if (tagList.size() < 1)
			return getAllImage(imageRootDir);
		else if (tagList.size() == 1)
			fileList = getImageByTag(tagList.get(0));
		else
		{
			fileList = getImageByTag(tagList.get(0));
			for (int i = 1; i < tagList.size(); i++)
			{
				ArrayList<File> list = getImageByTag(tagList.get(i));
				switch (symbolList.get(i - 1))
				{
					case '&':
						fileList.retainAll(list);
						break;
					case '|':
						fileList.removeAll(list);
						break;
				}
			}
		}
		return fileList;
	}

	@Transactional
	private ArrayList<File> getImageByTag(String tag)
	{
		List<TagEntity> tagList = tagDAO.findByName(tag);
		ArrayList<File> imageList = new ArrayList<>();
		tagList.forEach(tagEntity -> {
			File file = searchImage(tagEntity.getImage().getName(), imageRootDir);
			if (file != null)
				imageList.add(file);
		});
		return imageList;
	}

	@Transactional
	public void addTag2Image(String name, String...tags)
	{
		var image = imageDAO.findByName(name);
		if (image == null)
		{
			image = new ImageEntity();
			image.setName(name);
			image.setTags(new ArrayList<>());
			imageDAO.save(image);
		}
		var tagList = image.getTags();
		for (String tag : tags)
		{
			for (var t : tagList)
				if (t.getName().equals(tag))
					return;
			var newTag = new TagEntity();
			newTag.setImage(image);
			newTag.setName(tag);
			tagDAO.save(newTag);
		}
	}

	@Transactional
	public void removeTagFromImage(String name, String...tags)
	{
		var image = imageDAO.findByName(name);
		if (image == null)
			return;
		var tagList = image.getTags();
		for (String tag : tags)
		{
			for (var t : tagList)
				if (t.getName().equals(tag))
				{
					t.setImage(null);
					tagDAO.update(t);
					tagDAO.delete(t.getId());
					return;
				}
		}
	}

	public void putMessageIDAndFileName(int messageId,String fileName)
	{
		map.put(messageId,fileName);
	}

	public String findFileNameByMessageID(int messageID)
	{
		return map.get(messageID);
	}

	@Transactional
	public void record(long operator,String imageName,boolean isAdd,String...tags)
	{
		StringBuilder stringBuilder=new StringBuilder();
		for (String tag : tags)
			stringBuilder.append(tag).append('|');
		OperationRecord operationRecord=new OperationRecord();
		operationRecord.setOperator(operator);
		operationRecord.setAdd(isAdd);
		operationRecord.setImageName(imageName);
		operationRecord.setTagName(stringBuilder.toString());
		recorderDAO.save(operationRecord);
	}
}
