package com.nobot;


import com.IceCreamQAQ.Yu.hook.HookItem;
import com.IceCreamQAQ.Yu.hook.YuHook;
import com.icecreamqaq.yuq.artqq.YuQArtQQStarter;

public class Starter
{
	/**
	 * 请不要往本类里面添加任何项目代码，也不要在本类里面引用任何类，以防止增强失败。
	 * 这个问题在之后会解决。
	 * @param args 启动参数
	 */
	public static void main(String[] args)
	{
		YuHook.put(new HookItem("org.hibernate.Version", "initVersion", "com.icecreamqaq.yudb.HibernateVersionHook"));
//        YuQMiraiStart.start(args);
		YuQArtQQStarter.start(args);
	}
}
