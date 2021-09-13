package com.nobot.system.annotation;

import com.IceCreamQAQ.Yu.annotation.LoadBy;
import com.nobot.system.PluginRegister;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@LoadBy(PluginRegister.class)
public @interface UnzipFileList
{
	UnzipFile[] value();
}
