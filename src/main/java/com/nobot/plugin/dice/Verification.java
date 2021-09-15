package com.nobot.plugin.dice;

import lombok.Data;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class Verification
{
	@Inject
	Dice dice;

	/**
	 * 成功状态
	 */
	enum State{SS_SUCCESS,S_SUCCESS,SUCCESS,FAIL}

	/**
	 * 额外状态
	 */
	enum EX_State{EX_SUCCESS,EX_FAIL}
	private static final int NORMAL=0;
	private static final int HARD=1;
	private static final int DIFFICULT=2;

	private static final int PUNISH=-1;
	private static final int BONUS=1;

	@Data
	private class VerificationClass
	{
		int skillNum,randomNum;
		/**
		 * 困难等级
		 */
		int level;
		State state;
		EX_State ex_state;

		int extraDice;
		List<Integer> extraDiceList;
		/**经过惩罚或者奖励之后的数字*/
		int workNum;

		/**
		 *
		 * @param skillNum 技能值
		 * @param level 难度等级
		 * @param extraDiceState 惩罚奖励
		 * @param extraDiceNum 惩罚奖励列表
		 */
		VerificationClass(int skillNum, int randomNum, int level, int extraDiceState, int...extraDiceNum)
		{
			this.skillNum=skillNum;
			this.randomNum=randomNum;
			this.level=level;
			extraDiceList=new ArrayList<>();
			for (int i:extraDiceNum)
				extraDiceList.add(i);
			this.extraDice=extraDiceState;
		}

	}

	public VerificationClass getVerificationState(int skillNum, int level, int extraDiceState, int extraDiceNum)
	{
		int[] array=new int[extraDiceNum];
		for (int i=0;i>=array.length;i++)
			array[i]=dice.getRandomInteger(0,9);
		VerificationClass verificationClass =new VerificationClass(
				skillNum,dice.getRandomInteger(0,100),level,extraDiceState,array);
		return verificationClass;
	}

	public VerificationClass getVerificationState(int skillNum, int level)
	{
		return getVerificationState(skillNum, level,NORMAL,0);
	}

	public VerificationClass getVerificationState(int skillNum)
	{
		return getVerificationState(skillNum, NORMAL);
	}

	/**
	 * 根据内部的信息进行检定，不会重新投掷
	 * @param verificationClass
	 * @return
	 */
	public VerificationClass judge(VerificationClass verificationClass)
	{
		int SS_S= verificationClass.getSkillNum()/5;
		int S_S= verificationClass.getSkillNum()/2;
		int S= verificationClass.getSkillNum();

		int workNum= verificationClass.getRandomNum();
		if(verificationClass.getExtraDice()!=NORMAL)
		{
			int x=workNum/10;
			int z=workNum%10;
			if(verificationClass.getExtraDice()==PUNISH)
			{
				for (int y : verificationClass.getExtraDiceList())
					if (y > x)
						x = y;
			}
			else if(verificationClass.getExtraDice()==BONUS)
			{
				for (int y : verificationClass.getExtraDiceList())
					if (y < x)
						x = y;
			}
			workNum=x*10+z;
		}
		verificationClass.setWorkNum(workNum);

		if(workNum<=SS_S&& verificationClass.getLevel()<=DIFFICULT)
			verificationClass.setState(State.SS_SUCCESS);
		else if(workNum<=S_S&& verificationClass.getLevel()<=HARD)
			verificationClass.setState(State.S_SUCCESS);
		else if(workNum<=S&& verificationClass.getLevel()<=NORMAL)
			verificationClass.setState(State.SUCCESS);
		else
			verificationClass.setState(State.FAIL);

		if(workNum==1&& verificationClass.getState()!= State.FAIL)
			verificationClass.setEx_state(EX_State.EX_SUCCESS);

		if(verificationClass.getState()== State.FAIL)
		{
			int limit;
			switch (verificationClass.getLevel())
			{
				case NORMAL:
					limit=S;
					break;
				case HARD:
					limit=S_S;
					break;
				case DIFFICULT:
					limit=SS_S;
					break;
				default:
					limit=0;
					break;
			}
			if(limit<50)
			{
				if (workNum >= 96)
					verificationClass.setEx_state(EX_State.EX_FAIL);
			}
			else
			{
				if (workNum >= 100)
					verificationClass.setEx_state(EX_State.EX_FAIL);
			}
		}
		return verificationClass;
	}

	public String getString(VerificationClass verificationClass,String userName,String skillName)
	{
		StringBuilder stringBuilder=new StringBuilder();
		if(userName!=null)
			stringBuilder.append("对"+userName);
		if(skillName!=null)
			stringBuilder.append("的"+skillName);
		stringBuilder.append("进行");
		switch (verificationClass.getLevel())
		{
			case NORMAL:
				break;
			case HARD:
				stringBuilder.append("困难");
				break;
			case DIFFICULT:
				stringBuilder.append("极难");
				break;
		}
		stringBuilder.append("检定：1d100=");
		stringBuilder.append(verificationClass.getWorkNum());
		if(verificationClass.getExtraDice()!=NORMAL)
		{
			stringBuilder.append("[");
			stringBuilder.append(verificationClass.getRandomNum());
			stringBuilder.append(":");
			stringBuilder.append(verificationClass.getExtraDice()==PUNISH?"惩罚骰：":"奖励骰：");
			for (int x: verificationClass.getExtraDiceList())
				stringBuilder.append(x+"，");
			stringBuilder.deleteCharAt(stringBuilder.length()-1);
			stringBuilder.append("]");

		}
		stringBuilder.append("/");
		stringBuilder.append(verificationClass.getSkillNum());
		stringBuilder.append("，检定");
		switch (verificationClass.getState())
		{
			case FAIL:
				stringBuilder.append("失败。");
				break;
			case SUCCESS:
				stringBuilder.append("成功。");
				break;
			case S_SUCCESS:
				stringBuilder.append("困难成功。");
				break;
			case SS_SUCCESS:
				stringBuilder.append("极难成功。");
				break;
		}
		if(verificationClass.getEx_state()!=null)
			switch (verificationClass.getEx_state())
			{
				case EX_SUCCESS:
					stringBuilder.append("大成功！");
					break;
				case EX_FAIL:
					stringBuilder.append("大失败！");
					break;
			}
		return stringBuilder.toString();
	}
}
