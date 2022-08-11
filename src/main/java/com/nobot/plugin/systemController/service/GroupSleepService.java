package com.nobot.plugin.systemController.service;

import com.icecreamqaq.yudb.jpa.annotation.Transactional;
import com.nobot.plugin.systemController.dao.GroupSleepModeDAO;
import com.nobot.plugin.systemController.entity.GroupSleepMode;

import javax.inject.Inject;

public class GroupSleepService
{
	@Inject
	private GroupSleepModeDAO groupSleepModeDAO;
	@Inject
	private DefaultSettingService defaultSettingService;

	@Transactional
	public boolean isGroupActive(long groupNum)
	{
		GroupSleepMode groupSleepMode=groupSleepModeDAO.findByID(groupNum);
		if(groupSleepMode==null)
		{
			boolean defaultMode=defaultSettingService.isDefaultGroupActive();
			groupSleepMode=new GroupSleepMode();
			groupSleepMode.setId(groupNum);
			groupSleepMode.setActive(defaultMode);
			groupSleepModeDAO.save(groupSleepMode);
			return defaultMode;
		}
		return groupSleepMode.isActive();
	}

	@Transactional
	public void setGroupSleepMode(long groupNum,boolean isActive)
	{
		GroupSleepMode groupSleepMode=groupSleepModeDAO.findByID(groupNum);
		if(groupSleepMode==null)
		{
			groupSleepMode=new GroupSleepMode();
			groupSleepMode.setId(groupNum);
			groupSleepMode.setActive(defaultSettingService.isDefaultGroupActive());
		}
		else
		{
			if(groupSleepMode.isActive!=isActive)
				groupSleepMode.setActive(isActive);
			else
				return;
		}
		groupSleepModeDAO.saveOrUpdate(groupSleepMode);
	}
}
