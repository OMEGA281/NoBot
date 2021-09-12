package com.nobot.plugin.draw;

import java.util.regex.Pattern;

interface ConstantPool
{
	String p_read=".*&\\[(.+?):(.+?)\\]#.*";
	String p_image="img";
	String p_r="r";
	String p_split=":";

	String p_accessoryLibrary="\\{(.+?)(:(.+?))?\\}";

	String p_include="include=\\[(.*?)\\]";
	String p_exclude="exclude=\\[(.*?)\\]";

	/**
	 * 包含范式：
	 * ①
	 */
	Pattern sp_pattern =Pattern.compile(p_read);
	Pattern pattern_accessoryLibrary=Pattern.compile(p_accessoryLibrary);
	Pattern pattern_include=Pattern.compile(p_include);
	Pattern pattern_exclude=Pattern.compile(p_exclude);

	String text_author="author";
	String text_help="help";
	String text_start="start",text_main="main",text_end="end";
	String attr_max="max";
	String text_subLib ="sub";
	String attr_tag="tag";
	String attr_num="num";

	String sp_name="@name#";
	String sp_at="@at#";
	String sp_me="@me#";
}
