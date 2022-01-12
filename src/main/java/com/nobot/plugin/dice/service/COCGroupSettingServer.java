package com.nobot.plugin.dice.service;

import com.icecreamqaq.yudb.jpa.annotation.Transactional;
import com.nobot.plugin.dice.dao.COCGroupSettingDAO;
import com.nobot.plugin.dice.entity.COCGroupSetting;
import com.nobot.system.annotation.UnzipFile;

import javax.inject.Inject;

public class COCGroupSettingServer
{
	@Inject
	COCGroupSettingDAO dao;

	@Transactional
	public int getSuccessSetting(long groupNum)
	{
		COCGroupSetting setting=dao.findById(groupNum);
		return setting==null?-1:setting.getFailSetting();
	}

	@Transactional
	public int getFailSetting(long groupNum)
	{
		COCGroupSetting setting=dao.findById(groupNum);
		return setting==null?-1:setting.getFailSetting();
	}

	@Transactional
	public void setSuccessSetting(long groupNum,int index)
	{
		COCGroupSetting setting=dao.findById(groupNum);
		if(setting==null)
		{
			setting=new COCGroupSetting();
			setting.setId(groupNum);
		}
		setting.setSuccessSetting(index);
		dao.saveOrUpdate(setting);
	}

	@Transactional
	public void setFailSetting(long groupNum,int index)
	{
		COCGroupSetting setting=dao.findById(groupNum);
		if(setting==null)
		{
			setting=new COCGroupSetting();
			setting.setId(groupNum);
		}
		setting.setFailSetting(index);
		dao.saveOrUpdate(setting);
	}
}
