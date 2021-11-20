package com.nobot.plugin.dice.expressionAnalyzer;

public interface SpecialSymbol
{
	char symbol_poundSign='#';
	/*表明前面是投掷次数，后面是投掷内容*/
	char symbol_d='d';
	/*后面跟随的数字表明从各种投掷结果中取出多少最大值*/
	char symbol_k='k';
	/*需要跟在d后面 设置了骰子的最小值和最大值*/
	char symbol_t='t';
	/*后面跟随的数字表明从各种投掷结果中取出多少最小值*/
	char symbol_q='q';
	/*奖励骰*/
	char symbol_b='b';
	/*惩罚骰*/
	char symbol_p='p';

	int PUNISH_MODE=-1;
	int BONUS_MODE=1;
	int NO_PUNISH_AND_BONUS=0;

	int GET_MAX_DICE_MODE=1;
	int GET_MIN_DICE_MODE=-1;
	int GET_ALL_DICE_MODE=0;
}
