package com.nobot.system.annotation;

import com.IceCreamQAQ.Yu.annotation.LoadBy;
import com.nobot.system.PluginRegister;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@LoadBy(PluginRegister.class)
public @interface UnzipFileOrDir
{
	/**
	 * 文件名称
	 * @return
	 */
	String name();

	/**
	 * 解压目标位置
	 * @return
	 */
	String aim();
}
