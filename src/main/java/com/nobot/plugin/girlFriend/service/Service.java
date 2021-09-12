package com.nobot.plugin.girlFriend.service;

import com.icecreamqaq.yudb.jpa.annotation.Transactional;
import com.nobot.plugin.girlFriend.GirlPool;
import com.nobot.plugin.girlFriend.dao.GirlDAO;
import com.nobot.plugin.girlFriend.dao.GroupDAO;
import com.nobot.plugin.girlFriend.dao.MasterDAO;
import com.nobot.plugin.girlFriend.entity.Girl;
import com.nobot.plugin.girlFriend.entity.Master;
import com.nobot.plugin.girlFriend.entity.MyGroup;

import javax.inject.Inject;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class Service implements GirlPool
{
	@Inject
	private MasterDAO masterDAO;
	@Inject
	private GroupDAO groupDAO;
	@Inject
	private GirlDAO girlDAO;

	Random random=new Random();

	@Transactional
	public List<Girl> getWifeList(long userNum, long groupNum)
	{
		Master master=masterDAO.findByGroupNumAndUserNum(groupNum,userNum);
		if(master==null)
			master= creatNewMaster(userNum,groupNum);
		return master.getGirlList();
	}

	@Transactional
	public void setWife(long userNum,long groupNum,Girl girl)
	{
		Master master=masterDAO.findByGroupNumAndUserNum(groupNum,userNum);
		if(master==null)
		{
			master = creatNewMaster(userNum, groupNum);
			masterDAO.save(master);
		}
		MyGroup group=groupDAO.findById(groupNum);
		if(group==null)
		{
			group = creatGroup(groupNum);
			groupDAO.save(group);
		}
		girl.setMaster(master);
		girl.setGroupNum(group);
		girl.setSaleNum(-1);
		girlDAO.saveOrUpdate(girl);
	}

	@Transactional
	public void setWifeByName(long userNum, long groupNum, String name)
	{
		Master master=masterDAO.findByGroupNumAndUserNum(groupNum,userNum);
		if(master==null)
		{
			master = creatNewMaster(userNum, groupNum);
			masterDAO.save(master);
		}
		MyGroup group=groupDAO.findById(groupNum);
		if(group==null)
		{
			group = creatGroup(groupNum);
			groupDAO.save(group);
		}
		List<Girl> girlList=group.getGirlList();
		Girl girl=null;
		for (Girl g:girlList)
		{
			if(g.getName().equals(name))
			{
				girl = g;
				break;
			}
		}
		if(girl==null)
		{
			girl=new Girl();
			girl.setName(name);
		}
		girl.setMaster(master);
		girl.setGroupNum(group);
		girl.setSaleNum(-1);
		girlDAO.saveOrUpdate(girl);
	}

	@Transactional
	public void setWifeFree(long userNum, long groupNum, String name)
	{
		Master master=masterDAO.findByGroupNumAndUserNum(groupNum,userNum);
		if(master==null)
		{
			master = creatNewMaster(userNum, groupNum);
			masterDAO.save(master);
		}
		MyGroup group=groupDAO.findById(groupNum);
		if(group==null)
		{
			group = creatGroup(groupNum);
			groupDAO.save(group);
		}
		List<Girl> girlList=group.getGirlList();
		Girl girl=null;
		for (Girl g:girlList)
		{
			if(g.getName().equals(name))
			{
				girl = g;
				break;
			}
		}
		if(girl==null)
		{
			girl=new Girl();
			girl.setName(name);
		}
		girl.setMaster(null);
		girl.setGroupNum(group);
		girl.setSaleNum(-1);
		girlDAO.saveOrUpdate(girl);
	}

	@Transactional
	public Girl findWife(long groupNum,String name)
	{
		MyGroup group=groupDAO.findById(groupNum);
		if(group==null)
		{
			group = creatGroup(groupNum);
			groupDAO.save(group);
		}
		List<Girl> girlList=group.getGirlList();
		Girl girl=null;
		for (Girl g:girlList)
		{
			if(g.getName().equals(name))
			{
				girl = g;
				break;
			}
		}
		return girl;
	}

	@Transactional
	public boolean haveWife(long userNum,long groupNum,String name)
	{
		Master master=masterDAO.findByGroupNumAndUserNum(groupNum,userNum);
		if(master==null)
		{
			master = creatNewMaster(userNum, groupNum);
			masterDAO.save(master);
		}
		MyGroup group=groupDAO.findById(groupNum);
		if(group==null)
		{
			group = creatGroup(groupNum);
			groupDAO.save(group);
		}
		List<Girl> girlList=master.getGirlList();
		for (Girl girl:girlList)
		{
			if(girl.getName().equals(name))
				return true;
		}
		return false;
	}

	@Transactional
	public boolean saleWife(long userNum,long groupNum,String name,int gold)
	{
		Master master=masterDAO.findByGroupNumAndUserNum(groupNum,userNum);
		if(master==null)
		{
			master = creatNewMaster(userNum, groupNum);
			masterDAO.save(master);
		}
		MyGroup group=groupDAO.findById(groupNum);
		if(group==null)
		{
			group = creatGroup(groupNum);
			groupDAO.save(group);
		}
		List<Girl> girlList=master.getGirlList();
		Girl girl = null;
		for (Girl g:girlList)
		{
			if(g.getName().equals(name))
			{
				girl = g;
				break;
			}
		}
		if(girl==null)
			return false;
		girl.setSaleNum(gold);
		girlDAO.save(girl);
		return true;
	}

	public Master creatNewMaster(long userNum, long groupNum)
	{
		Master master=new Master();
		master.setUserNum(userNum);
		master.setGroupNum(groupNum);
		master.setGold(80);
		master.setGirlList(new ArrayList<>());
		master.setLastSignTime(19000101);
		master.setActive(80);
		master.setCreatTime(getCurrentTime());
		return master;
	}

	public MyGroup creatGroup(long groupNum)
	{
		MyGroup group=new MyGroup();
		group.setId(groupNum);
		group.setGirlList(new ArrayList<>());
		return group;
	}

	public int getCurrentTime()
	{
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd");
		return Integer.parseInt(simpleDateFormat.format(System.currentTimeMillis()));
	}

	/**
	 * 随机获得一个新老婆 执行完之后老婆已到账
	 * @param groupNum
	 * @param userNum
	 * @return
	 */
	@Transactional
	public Girl getRandomFreeGirl(long groupNum,long userNum)
	{
		MyGroup group=groupDAO.findById(groupNum);
		if(group==null)
		{
			group = creatGroup(groupNum);
			groupDAO.save(group);
		}
		Master master=masterDAO.findByGroupNumAndUserNum(groupNum,userNum);

		List<Girl> girlList=group.getGirlList();
		List<Girl> noMasterGirl=new ArrayList<>();
		for (Iterator<Girl> iterator=girlList.iterator();iterator.hasNext();)
		{
			Girl girl=iterator.next();
			if (girl.getMaster() == null)
			{
				noMasterGirl.add(girl);
				iterator.remove();
			}
		}
		Map<String,File> map=listGirl();
		for (Girl girl:girlList)
			if(map.containsKey(girl.getName()))
				map.remove(girl.getName());
		if(map.size()==0)
			return null;
		int i=random.nextInt(map.size());
		String name=map.keySet().toArray(new String[0])[i];

		for (Girl girl:noMasterGirl)
			if(girl.getName().equals(name))
			{
				girl.setMaster(master);
				girlDAO.update(girl);
				return girl;
			}
		Girl girl=new Girl();
		girl.setName(name);
		girl.setGroupNum(group);
		girl.setSaleNum(-1);
		girl.setMaster(master);
		girlDAO.save(girl);
		return girl;
	}

	/**
	 * 模拟随机获得一个新老婆（看着玩的） 执行完之后老婆已到账
	 * @param groupNum
	 * @return
	 */
	@Transactional
	public Girl simulateGetRandomFreeGirl(long groupNum)
	{
		MyGroup group=groupDAO.findById(groupNum);
		if(group==null)
		{
			group = creatGroup(groupNum);
			groupDAO.save(group);
		}

		List<Girl> girlList=group.getGirlList();
		List<Girl> noMasterGirl=new ArrayList<>();
		for (Iterator<Girl> iterator=girlList.iterator();iterator.hasNext();)
		{
			Girl girl=iterator.next();
			if (girl.getMaster() == null)
			{
				noMasterGirl.add(girl);
				iterator.remove();
			}
		}
		Map<String,File> map=listGirl();
		for (Girl girl:girlList)
			if(map.containsKey(girl.getName()))
				map.remove(girl.getName());
		if(map.size()==0)
			return null;
		int i=random.nextInt(map.size());
		String name=map.keySet().toArray(new String[0])[i];

		for (Girl girl:noMasterGirl)
			if(girl.getName().equals(name))
			{
				return girl;
			}
		Girl girl=new Girl();
		girl.setName(name);
		girl.setGroupNum(group);
		return girl;
	}

	public Map<String, File> listGirl()
	{
		File dir=new File(GIRL_POOL);
		File[] girls=dir.listFiles();
		Map<String,File> map=new HashMap<>();
		for (File file:girls)
			map.put(file.getName().split("\\.")[0],file);
		return map;
	}

	public File getGirlImage(String name)
	{
		File[] files=new File(GIRL_POOL).listFiles();
		for(File file:files)
		{
			if(file.getName().startsWith(name+"."))
				return file;
		}
		return null;
	}

	@Transactional
	public void addGold(long userNum,long groupNum,int gold)
	{
		Master master=masterDAO.findByGroupNumAndUserNum(groupNum,userNum);
		if(gold==0)
			return;
		master.setGold(master.getGold()+gold);
		masterDAO.saveOrUpdate(master);
	}

	public Map<String,Integer> listForSaleGirl(long groupNum)
	{
		MyGroup group=groupDAO.findById(groupNum);
		if(group==null)
		{
			group = creatGroup(groupNum);
			groupDAO.save(group);
		}
		List<Girl> girlList=group.getGirlList();
		Map<String,Integer> map=new HashMap<>();
		for (Girl girl:girlList)
			if(girl.getSaleNum()>=0)
				map.put(girl.getName(),girl.getSaleNum());
		return map;
	}

	public Master getMaster(long userNum,long groupNum)
	{
		return masterDAO.findByGroupNumAndUserNum(groupNum,userNum);
	}

	@Transactional
	public void saveMaster(Master master)
	{
		masterDAO.saveOrUpdate(master);
	}

	@Transactional
	public void updateGirl(Girl girl)
	{
		girlDAO.update(girl);
	}
}
