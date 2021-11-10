package com.nobot.plugin.draw;

import lombok.Getter;
import lombok.NonNull;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

public class Card implements ConstantPool
{
	@Getter
	class SubLib
	{
		class Line
		{
			String[] tagList;
			String text;
			Line(String text,String...tagList)
			{
				this.tagList=tagList;
				this.text=text;
			}
		}
		String name;
		List<Line> list=new ArrayList<>();
		SubLib(Element element)
		{
			name=element.getName();
			for(Element subElement:element.getChildren())
			{
				String t_num=subElement.getAttributeValue(attr_num);
				String t_tag=subElement.getAttributeValue(attr_tag);
				String text=subElement.getText();
				int num;
				try
				{
					num=Integer.parseInt(t_num);
				}
				catch (NumberFormatException e)
				{
					num=1;
				}
				String[] tag=null;
				if(t_tag!=null)
					tag=t_tag.split(",|，");
				for(int i=0;i<num;i++)
					list.add(new Line(text,tag));
			}
		}

		/**
		 * 根据提供的tag列表，返回可以随机的列表
		 * @param include
		 * @param exclude
		 * @return
		 */
		protected List<String> getLine(List<String> include,List<String> exclude)
		{
			ArrayList<String> aimList=new ArrayList<>();
			for(Line line:list)
			{
				if(passBlack(exclude, line.tagList)&&passWhite(include, line.tagList))
					aimList.add(line.text);
			}
			return aimList;
		}

		/**
		 * 检测师傅通过黑名单校验
		 * @param list 黑名单
		 * @param tag 待校验单
		 * @return
		 */
		private boolean passBlack(List<String> list,String[] tag)
		{
			if (list==null||list.isEmpty()||tag==null||tag.length==0)
				return true;
			for(String a:list)
			{
				for (String b : tag)
					if (a.equals(b))
						return false;
			}
			return true;
		}

		/**
		 * 检测师傅通过白名单校验
		 * @param list 白名单
		 * @param tag 待校验单
		 * @return
		 */
		private boolean passWhite(List<String> list,String[] tag)
		{
			if(list==null||list.isEmpty())
				return true;
			code_0:for(String a:list)
			{
				for (String b : tag)
				{
					if (a.equals(b))
						continue code_0;
				}
				return false;
			}
			return true;
		}
	}

	private Element root;

	@Getter
	private String author,help;
	@Getter
	private String start,main,end;
	@Getter
	private int maxTime=1;
	@Getter
	private List<SubLib> subLibs=new ArrayList<>();
	Card(@NonNull Document document)
	{
		root=document.getRootElement();
		Element author=root.getChild(text_author);
		this.author=author==null?"":author.getText();
		Element help=root.getChild(text_help);
		this.help=help==null?"":help.getText();
		Element start=root.getChild(text_start);
		this.start=start==null?"":start.getText();
		Element main=root.getChild(text_main);
		if(main==null)
			this.main="";
		else
		{
			this.main=main.getText();
			String max=main.getAttributeValue(attr_max);
			if(max!=null)
			{
				int maxTime=Integer.parseInt(max);
				if(0>maxTime)
					this.maxTime=1;
				else if(maxTime>globalMaxTime)
					this.maxTime=globalMaxTime;
				else
					this.maxTime=maxTime;
			}
		}
		Element end=root.getChild(text_end);
		this.end=end==null?"":end.getText();

		Element element=root.getChild(text_subLib);
		if (element!=null)
			for (Element subElement:element.getChildren())
				subLibs.add(new SubLib(subElement));
	}

	/**
	 * 根据名称获得子牌库
	 * @param name 牌库名称
	 * @return 不存在则返回null
	 */
	protected SubLib getSubLib(String name)
	{
		for (SubLib subLib:subLibs)
		{
			if(subLib.getName().equals(name))
				return subLib;
		}
		return null;
	}
}
