package com.nobot.plugin.draw;

import java.util.regex.Pattern;

interface ConstantPool
{
	String drawPool="DrawPool";

	String specialWord ="\\$\\[(.+?)[:：](.+?)\\]#";
	String specialWord_image ="image";
	String specialWord_r ="r";
	String specialWord_name ="name";
	String specialWord_at ="at";
	String specialWord_split =":";

	String p_accessoryLibrary="\\{(.+?)(:(.+?))?\\}";

	String p_include="include=\\[(.*?)\\]";
	String p_exclude="exclude=\\[(.*?)\\]";
	String p_have="have=\\[(.*?)\\]";

	/**
	 * 包含范式：
	 * ①
	 */
	Pattern specialWord_pattern =Pattern.compile(specialWord);
	Pattern pattern_accessoryLibrary=Pattern.compile(p_accessoryLibrary);
	Pattern pattern_include=Pattern.compile(p_include);
	Pattern pattern_exclude=Pattern.compile(p_exclude);
	Pattern pattern_have=Pattern.compile(p_have);

	String text_author="author";
	String text_help="help";
	String text_start="start",text_main="main",text_end="end";
	String attr_max="max";
	String text_subLib ="sub";
	String attr_tag="tag";
	String attr_num="num";
}
