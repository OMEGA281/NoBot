package com.nobot.plugin.systemController.service;

import com.icecreamqaq.yudb.jpa.annotation.Transactional;
import com.nobot.plugin.systemController.dao.AdministratorsDAO;
import com.nobot.plugin.systemController.dao.BlackUserDAO;
import com.nobot.plugin.systemController.dao.WhiteUserDAO;
import com.nobot.plugin.systemController.entity.BlackUser;
import com.nobot.plugin.systemController.entity.WhiteUser;

import javax.inject.Inject;

public class BlackAndWhiteListService
{
	@Inject
	private BlackUserDAO blackUserDAO;
	@Inject
	private WhiteUserDAO whiteUserDAO;
	@Inject
	private AdministratorsDAO administratorsDAO;

	@Transactional
	public boolean isUserInBlackList(long userNum,long groupNum)
	{
		return blackUserDAO.existsByUserNumAndGroupNum(userNum,groupNum);
	}

	@Transactional
	public boolean addUser2BlackList(long userNum,long groupNum)
	{
		if(isUserInBlackList(userNum, groupNum))
			return false;
		BlackUser list=new BlackUser();
		list.setUserNum(userNum);
		list.setGroupNum(groupNum);
		blackUserDAO.save(list);
		return true;
	}

	@Transactional
	public boolean removeUserFromBlackList(long userNum,long groupNum)
	{
		return blackUserDAO.removeByUserNumAndGroupNum(userNum,groupNum)!=null;
	}

	@Transactional
	public boolean isUserInWhiteList(long userNum,long groupNum)
	{
		return whiteUserDAO.existsByUserNumAndGroupNum(userNum,groupNum);
	}

	@Transactional
	public boolean addUser2WhiteList(long userNum,long groupNum)
	{
		if(isUserInWhiteList(userNum, groupNum))
			return false;
		WhiteUser list=new WhiteUser();
		list.setUserNum(userNum);
		list.setGroupNum(groupNum);
		whiteUserDAO.save(list);
		return true;
	}

	@Transactional
	public boolean removeUserFromWhiteList(long userNum,long groupNum)
	{
		return whiteUserDAO.removeByUserNumAndGroupNum(userNum,groupNum)!=null;
	}

	public boolean isOP(long num)
	{
		return administratorsDAO.getOP().contains(num) || administratorsDAO.getSOP() == num;
	}
}
