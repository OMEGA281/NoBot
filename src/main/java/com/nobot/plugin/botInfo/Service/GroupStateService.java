package com.nobot.plugin.botInfo.Service;

import com.icecreamqaq.yudb.jpa.annotation.Transactional;
import com.nobot.plugin.botInfo.dao.GlobalSettingDAO;
import com.nobot.plugin.botInfo.dao.GroupStateDAO;
import com.nobot.plugin.botInfo.entity.GroupState;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupStateService
{
	@Inject
	GroupStateDAO groupStateDao;
	@Inject
	GlobalSettingDAO globalSettingDAO;

	private HashMap<Long, Boolean> groupStates=new HashMap<>();
	private Boolean defaultStates;
	private Map<Long,List<Long>> blackList=new HashMap<>();

	@Transactional
	public boolean getGroupState(long groupNum)
	{
		if (groupStates.containsKey(groupNum))
		{
			groupStates.put(groupNum, groupStates.get(groupNum));
			return getGroupState(groupNum);
		}
		else
		{
			if (groupNum == -1)
			{
				if (defaultStates == null)
					defaultStates = globalSettingDAO.getDefaultState();
				return defaultStates;
			}
			GroupState groupState = groupStateDao.findByGroupNum(groupNum);
			if (groupState == null)
				return getGroupState(-1);
			groupStates.put(groupState.getGroupNum(), groupState.isState());
			return groupState.isState();
		}
	}

	@Transactional
	public void setGroupState(long groupNum, boolean state) throws IOException
	{
		if (groupNum == -1)
		{
			globalSettingDAO.setDefaultState(state);
			defaultStates = state;
			return;
		}
		GroupState groupState = groupStateDao.findByGroupNum(groupNum);
		if (groupState == null)
		{
			groupState = new GroupState(groupNum, state);
			groupStateDao.save(groupState);
			groupStates.put(groupNum,state);
		}
		groupState.setState(state);
		groupStateDao.update(groupState);
		groupStates.put(groupNum,state);
	}

	@Transactional
	public List<Long> getGroupBlacklist(long groupNum)
	{
		if(blackList.containsKey(groupNum))
			return blackList.get(groupNum);
		if (groupNum == -1)
		{
			List<Long> list=globalSettingDAO.getGlobalBanUser();
			blackList.put(-1L,list);
			return getGroupBlacklist(-1);
		}
		GroupState groupState = groupStateDao.findByGroupNum(groupNum);
		List<Long> list = new ArrayList<>();
		if (groupState == null)
			return list;
		String s = groupState.getBanUser();
		if (s == null || s.isEmpty())
			return list;
		for (String string : s.split(","))
		{
			try
			{
				list.add(Long.parseLong(string));
			}
			catch (NumberFormatException ignored)
			{
			}
		}
		blackList.put(groupNum,list);
		return list;
	}

	@Transactional
	public void addGroupBlacklist(long groupNum, long userNum) throws IOException
	{
		if (groupNum == -1)
		{
			globalSettingDAO.addGlobalBanUser(userNum);
			getGroupBlacklist(groupNum).add(userNum);
			return;
		}
		GroupState groupState = groupStateDao.findByGroupNum(groupNum);
		List<Long> list = new ArrayList<>();
		if (groupState == null)
		{
			groupState = new GroupState(groupNum, globalSettingDAO.getDefaultState());
			groupState.setBanUser(Long.toString(userNum));
			groupStateDao.save(groupState);
			list.add(userNum);
			blackList.put(groupNum,list);
			return;
		}
		String s = groupState.getBanUser();
		if (s == null || s.isEmpty())
		{
			groupState.setBanUser(Long.toString(userNum));
			groupStateDao.update(groupState);
			return;
		}
		for (String string : s.split(","))
		{
			try
			{
				list.add(Long.parseLong(string));
			}
			catch (NumberFormatException ignored)
			{
			}
		}
		if (!list.contains(userNum))
		{
			list.add(userNum);
			StringBuilder stringBuilder = new StringBuilder();
			for (long l : list)
				stringBuilder.append(l).append(",");
			stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
			groupState.setBanUser(stringBuilder.toString());
			groupStateDao.update(groupState);
			blackList.put(groupNum,list);
		}
	}

	@Transactional
	public void removeGroupBlacklist(long groupNum, long userNum) throws IOException
	{
		if (groupNum == -1)
		{
			globalSettingDAO.removeGlobalBanUser(userNum);
			getGroupBlacklist(groupNum).remove(userNum);
			return;
		}
		GroupState groupState = groupStateDao.findByGroupNum(groupNum);
		List<Long> list = new ArrayList<>();
		if (groupState == null)
			return;
		String s = groupState.getBanUser();
		if (s == null || s.isEmpty())
			return;
		for (String string : s.split(","))
		{
			try
			{
				list.add(Long.parseLong(string));
			}
			catch (NumberFormatException ignored)
			{
			}
		}
		if (list.contains(userNum))
		{
			list.remove(userNum);
			StringBuilder stringBuilder = new StringBuilder();
			for (long l : list)
				stringBuilder.append(l).append(",");
			stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
			groupState.setBanUser(stringBuilder.toString());
			groupStateDao.update(groupState);
			blackList.put(groupNum,list);
		}
	}
}
