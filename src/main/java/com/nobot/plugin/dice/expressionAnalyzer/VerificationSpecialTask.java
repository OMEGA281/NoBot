package com.nobot.plugin.dice.expressionAnalyzer;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.nobot.plugin.dice.service.COCGroupSettingServer;
import com.nobot.system.annotation.UnzipFile;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Getter;

import javax.inject.Inject;
import java.io.*;
import java.util.Properties;

@UnzipFile(name = "COCGroupSetting.properties",aim = "COCGroupSetting.properties")
@EventListener
public class VerificationSpecialTask
{
	private interface Task
	{
		/**
		 * 检测是否符合条件
		 * @param skillNum 技能数值
		 * @param randomNum 投掷数值
		 * @param successStatus 成功情况：<br>0：失败；1：成功；2：困难成功；3：极难成功
		 * @return 是否通过：<code>true</code>：大成功或大失败<br><code>false</code>：不通过
		 */
		boolean isPass(int skillNum,int randomNum,int successStatus);
	}

	@Getter
	@AllArgsConstructor
	private enum SuccessTask
	{
		mode_0(0,"当数值为1且检定成功时大成功",
				(skillNum, randomNum, successStatus) -> successStatus > 0 && randomNum == 1),
		mode_1(1,"关闭大成功",
				(skillNum, randomNum, successStatus) -> false),
		mode_2(2,"当数值为1时必定大成功",
				(skillNum, randomNum, successStatus) -> randomNum == 1),
		mode_3(3,"当数值为1~5且检定成功时大成功",
				(skillNum, randomNum, successStatus) -> successStatus>0&&randomNum>=1&&randomNum<=5),
		mode_4(4,"当数值为1~5时必定大成功",
				(skillNum, randomNum, successStatus) -> successStatus>0&&randomNum>=1&&randomNum<=5);

		private int modeIndex;
		private String help;
		private Task task;

		public static SuccessTask getTask(int modeIndex)
		{
			for (SuccessTask value : SuccessTask.values())
			{
				if(value.getModeIndex()==modeIndex)
					return value;
			}
			return null;
		}
	}

	@Getter
	@AllArgsConstructor
	private enum FailTask
	{
		mode_0(0,"当技能数值小于50时，掷出96~100或者当技能数值大于等于50时，掷出100且发生了失败时大失败",
				(skillNum, randomNum, successStatus) ->
						successStatus == 0 && (skillNum < 50 ? randomNum >= 96 && randomNum <= 100 : randomNum >= 100)),
		mode_1(1,"关闭大失败",(skillNum, randomNum, successStatus) -> false),
		mode_2(2,"当技能数值小于50时，掷出96~100或者当技能数值大于等于50时，掷出100时必定大失败",
				(skillNum, randomNum, successStatus) ->
						skillNum < 50 ? randomNum >= 96 && randomNum <= 100 : randomNum >= 100),
		mode_3(3,"掷出96~100且发生了失败时大失败",
				(skillNum, randomNum, successStatus) -> successStatus == 0 && randomNum >= 96 && randomNum <= 100),
		mode_4(4,"掷出96~100时必定大失败",
				(skillNum, randomNum, successStatus) -> randomNum >= 96 && randomNum <= 100),
		mode_5(5,"掷出100且发生了失败时大失败",
				(skillNum, randomNum, successStatus) -> successStatus == 0 && randomNum >= 100),
		mode_6(6,"掷出100时必定大失败",
				(skillNum, randomNum, successStatus) -> randomNum >= 100);

		private int modeIndex;
		private String help;
		private Task task;

		public static FailTask getTask(int modeIndex)
		{
			for (FailTask value : FailTask.values())
			{
				if(value.getModeIndex()==modeIndex)
					return value;
			}
			return null;
		}
	}

	@Inject
	private COCGroupSettingServer server;
	private int defaultSuccessSetting,defaultFailSetting;

	public boolean isExSuccess(long groupNum,int skillNum,int randomNum,int successStatue)
	{
		int index=server.getSuccessSetting(groupNum);
		if(index<0)
			index=defaultSuccessSetting;
		return SuccessTask.getTask(index).getTask().isPass(skillNum,randomNum,successStatue);
	}

	public boolean isExFail(long groupNum,int skillNum,int randomNum,int successStatue)
	{
		int index=server.getFailSetting(groupNum);
		if(index<0)
			index=defaultFailSetting;
		return SuccessTask.getTask(index).getTask().isPass(skillNum,randomNum,successStatue);
	}

	@Event
	private void reloadDefaultGroupSetting(AppStartEvent event) throws IOException
	{
		File file=new File("COCGroupSetting.properties");
		@Cleanup FileInputStream inputStream=new FileInputStream(file);
		Properties properties=new Properties();
		properties.load(inputStream);
		defaultSuccessSetting= Integer.parseInt(properties.getProperty("setting.ex.success"));
		defaultFailSetting= Integer.parseInt(properties.getProperty("setting.ex.fail"));
	}
}
