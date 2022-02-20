package com.nobot.system.stringHelper;

import com.IceCreamQAQ.Yu.annotation.LoadBy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@LoadBy(StringPointExtract.class)
public @interface DefaultStringFile
{
	String value();
	String info() default "";
}
