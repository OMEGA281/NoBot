package com.nobot.plugin.girlFriend.service;

import com.icecreamqaq.yudb.jpa.annotation.Transactional;
import com.nobot.plugin.girlFriend.GirlPool;
import com.nobot.plugin.girlFriend.dao.GirlDAO;
import com.nobot.plugin.girlFriend.dao.GroupDAO;
import com.nobot.plugin.girlFriend.dao.MasterDAO;
import com.nobot.plugin.girlFriend.entity.Girl;
import com.nobot.plugin.girlFriend.entity.Master;
import com.nobot.plugin.girlFriend.entity.MyGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.var;

import javax.inject.Inject;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class Service implements GirlPool
{
	@AllArgsConstructor
	public class NoMasterExceptionRecordException extends RuntimeException
	{
		@Getter
		private long groupNum, masterNum;
	}

	private final int NO_SALE=-1;
	@Inject
	private MasterDAO masterDAO;
	@Inject
	private GroupDAO groupDAO;
	@Inject
	private GirlDAO girlDAO;

	Random random=new Random();

	@Transactional
	public boolean setGirlMaster(long groupNum,long girlID,long masterNum)
	{
		var girl=girlDAO.findById(girlID);
		if(girl==null)
			return false;
		Master master=masterDAO.findByGroupNumAndUserNum(groupNum,masterNum);
		if(master==null)
			masterDAO.save(creatNewMaster(masterNum,groupNum));
		if(girl.getMaster().getUserNum()==masterNum)
			return true;
		girl.setMaster(master);
		girlDAO.update(girl);
		return true;
	}

	@Transactional
	public boolean setGirlMasterByMasterID(long girlID,long masterID)
	{
		var girl=girlDAO.findById(girlID);
		if(girl==null)
			return false;
		var master=masterDAO.findByID(masterID);
		if(master==null)
			return false;

		if(girl.getMaster()==master)
			return true;
		girl.setMaster(master);
		girlDAO.update(girl);
		return true;
	}

	@Transactional
	public List<Girl> getWifeList(long userNum, long groupNum)
	{
		Master master=masterDAO.findByGroupNumAndUserNum(groupNum,userNum);
		if(master==null)
			master= creatNewMaster(userNum,groupNum);
		return master.getGirlList();
	}

	@Deprecated
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
			girl.setDecomposeTime(0);
		}
		girl.setMaster(master);
		girl.setGroupNum(group);
		girl.setSaleNum(-1);
		girl.setMarry(false);
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
			girl.setDecomposeTime(0);
		}
		girl.setMaster(null);
		girl.setGroupNum(group);
		girl.setSaleNum(-1);
		girl.setMarry(false);
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
	public boolean saleWife(long groupNum, long userNum, String girlName, int gold)
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
			if(g.getName().equals(girlName))
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
		master.setCreatTime(getToday());
		master.setBuyTime(0);
		master.setSaleTime(0);
		master.setDecomposeTime(0);
		master.setWorkSumTime(0);
		return master;
	}

	public MyGroup creatGroup(long groupNum)
	{
		MyGroup group=new MyGroup();
		group.setId(groupNum);
		group.setGirlList(new ArrayList<>());
		return group;
	}



	/**
	 * 随机获得一个新老婆
	 * @param groupNum
	 * @return
	 */
	@Transactional
	public Girl getRandomFreeGirl(long groupNum)
	{
		MyGroup group=groupDAO.findById(groupNum);
		if(group==null)
		{
			group = creatGroup(groupNum);
			groupDAO.save(group);
		}

		List<Girl> girlList=group.getGirlList();
		List<Girl> noMasterGirl=new ArrayList<>();
		girlList.forEach(girl -> {
			if (girl.getMaster()==null)
				noMasterGirl.add(girl);
		});

		Map<String,File> map=listGirl();
		girlList.forEach(girl -> map.remove(girl.getName()));

		if(noMasterGirl.size()==0&&map.size()==0)
			return null;

		int i=random.nextInt(noMasterGirl.size()+map.size());
		if(i>=noMasterGirl.size())
		{
//			超过已有card，抽取库存
			i=i-noMasterGirl.size();
			Girl girl=new Girl();
			girl.setGroupNum(group);
			girl.setSaleNum(NO_SALE);
			girl.setMarry(false);
			girl.setDecomposeTime(0);
			girl.setName(map.keySet().toArray(new String[0])[i]);
			girlDAO.save(girl);
			return girl;
		}
		else
			return noMasterGirl.get(i);
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
	public int getGold(long groupNum,long userNum)
	{
		var master=masterDAO.findByGroupNumAndUserNum(groupNum,userNum);
		if(master==null)
			throw new NoMasterExceptionRecordException(groupNum,userNum);
		return master.getGold();
	}

	@Transactional
	public void setGold(long groupNum,long userNum,int gold)
	{
		var master=masterDAO.findByGroupNumAndUserNum(groupNum,userNum);
		if(master==null)
			throw new NoMasterExceptionRecordException(groupNum,userNum);
		master.setGold(gold);
		masterDAO.update(master);
	}

	@Transactional
	public int addGold(long groupNum,long userNum,int gold)
	{
		var master=masterDAO.findByGroupNumAndUserNum(groupNum,userNum);
		if(master==null)
			throw new NoMasterExceptionRecordException(groupNum,userNum);
		int i=master.getGold()+gold;
		master.setGold(i);
		masterDAO.update(master);
		return i;
	}

	@Transactional
	public int getActive(long groupNum,long userNum)
	{
		var master=masterDAO.findByGroupNumAndUserNum(groupNum,userNum);
		if(master==null)
			throw new NoMasterExceptionRecordException(groupNum,userNum);
		return master.getActive();
	}

	@Transactional
	public void setActive(long groupNum,long userNum,int active)
	{
		var master=masterDAO.findByGroupNumAndUserNum(groupNum,userNum);
		if(master==null)
			throw new NoMasterExceptionRecordException(groupNum,userNum);
		master.setActive(active);
		masterDAO.update(master);
	}

	@Transactional
	public int addActive(long groupNum,long userNum,int active)
	{
		var master=masterDAO.findByGroupNumAndUserNum(groupNum,userNum);
		if(master==null)
			throw new NoMasterExceptionRecordException(groupNum,userNum);
		int i=master.getActive()+active;
		master.setActive(active);
		masterDAO.update(master);
		return i;
	}

	@Transactional
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

	@Transactional
	public int countGroupWife(long groupNum)
	{
		MyGroup group=groupDAO.findById(groupNum);
		List<Girl> girlList=group.getGirlList();
		int i=0;
		for (Girl girl:girlList)
			if (girl.getMaster()!=null)
				i++;
		return i;
	}

	@Transactional
	public Master getMaster(long groupNum, long userNum)
	{
		return masterDAO.findByGroupNumAndUserNum(groupNum,userNum);
	}

	@Transactional
	public void saveMaster(Master master)
	{
		masterDAO.saveOrUpdate(master);
	}

	/**
	 * 检查今天是否签到了
	 * @param groupNum
	 * @param userNum
	 * @return
	 */
	@Transactional
	public boolean checkTodaySign(long groupNum,long userNum)
	{
		var master=masterDAO.findByGroupNumAndUserNum(groupNum,userNum);
		if(master==null)
			throw new NoMasterExceptionRecordException(groupNum,userNum);
		int i=master.getLastSignTime();
		return i >= getToday();
	}

	@Transactional
	public void setSign(long groupNum,long userNum,int sign)
	{
		var master=masterDAO.findByGroupNumAndUserNum(groupNum,userNum);
		if(master==null)
			throw new NoMasterExceptionRecordException(groupNum,userNum);
		master.setLastSignTime(sign);
		masterDAO.update(master);
	}

	@Transactional
	public int getDecomposeTime(long group,long userNum)
	{
		var master=masterDAO.findByGroupNumAndUserNum(group,userNum);
		if(master==null)
			throw new NoMasterExceptionRecordException(group,userNum);
		return master.getDecomposeTime();
	}

	@Transactional
	public void setDecomposeTime(long group,long userNum,int num)
	{
		var master=masterDAO.findByGroupNumAndUserNum(group,userNum);
		if(master==null)
			throw new NoMasterExceptionRecordException(group,userNum);
		master.setDecomposeTime(num);
		masterDAO.update(master);
	}

	@Transactional
	public int addDecomposeTime(long group,long userNum,int num)
	{
		var master=masterDAO.findByGroupNumAndUserNum(group,userNum);
		if(master==null)
			throw new NoMasterExceptionRecordException(group,userNum);
		int i=master.getDecomposeTime()+num;
		master.setDecomposeTime(i);
		masterDAO.update(master);
		return i;
	}

	@Transactional
	public int getBuyTime(long group,long userNum)
	{
		var master=masterDAO.findByGroupNumAndUserNum(group,userNum);
		if(master==null)
			throw new NoMasterExceptionRecordException(group,userNum);
		return master.getBuyTime();
	}

	@Transactional
	public void setBuyTime(long group,long userNum,int num)
	{
		var master=masterDAO.findByGroupNumAndUserNum(group,userNum);
		if(master==null)
			throw new NoMasterExceptionRecordException(group,userNum);
		master.setBuyTime(num);
		masterDAO.update(master);
	}

	@Transactional
	public int addBuyTime(long group,long userNum,int num)
	{
		var master=masterDAO.findByGroupNumAndUserNum(group,userNum);
		if(master==null)
			throw new NoMasterExceptionRecordException(group,userNum);
		int i=master.getBuyTime()+num;
		master.setBuyTime(i);
		masterDAO.update(master);
		return i;
	}

	@Transactional
	public int getSaleTime(long group,long userNum)
	{
		var master=masterDAO.findByGroupNumAndUserNum(group,userNum);
		if(master==null)
			throw new NoMasterExceptionRecordException(group,userNum);
		return master.getSaleTime();
	}

	@Transactional
	public void setSaleTime(long group,long userNum,int num)
	{
		var master=masterDAO.findByGroupNumAndUserNum(group,userNum);
		if(master==null)
			throw new NoMasterExceptionRecordException(group,userNum);
		master.setSaleTime(num);
		masterDAO.update(master);
	}

	@Transactional
	public int addSaleTime(long group,long userNum,int num)
	{
		var master=masterDAO.findByGroupNumAndUserNum(group,userNum);
		if(master==null)
			throw new NoMasterExceptionRecordException(group,userNum);
		int i=master.getSaleTime()+num;
		master.setSaleTime(i);
		masterDAO.update(master);
		return i;
	}

	@Transactional
	public int getWorkSumTime(long group,long userNum)
	{
		var master=masterDAO.findByGroupNumAndUserNum(group,userNum);
		if(master==null)
			throw new NoMasterExceptionRecordException(group,userNum);
		return master.getWorkSumTime();
	}

	@Transactional
	public void setWorkSumTime(long group,long userNum,int num)
	{
		var master=masterDAO.findByGroupNumAndUserNum(group,userNum);
		if(master==null)
			throw new NoMasterExceptionRecordException(group,userNum);
		master.setWorkSumTime(num);
		masterDAO.update(master);
	}

	@Transactional
	public int addWorkSumTime(long group,long userNum,int num)
	{
		var master=masterDAO.findByGroupNumAndUserNum(group,userNum);
		if(master==null)
			throw new NoMasterExceptionRecordException(group,userNum);
		int i=master.getWorkSumTime()+num;
		master.setWorkSumTime(i);
		masterDAO.update(master);
		return i;
	}

	@Transactional
	public int getDrawTime(long group,long userNum)
	{
		var master=masterDAO.findByGroupNumAndUserNum(group,userNum);
		if(master==null)
			throw new NoMasterExceptionRecordException(group,userNum);
		return master.getDrawTime();
	}

	@Transactional
	public void setDrawTime(long group,long userNum,int num)
	{
		var master=masterDAO.findByGroupNumAndUserNum(group,userNum);
		if(master==null)
			throw new NoMasterExceptionRecordException(group,userNum);
		master.setDrawTime(num);
		masterDAO.update(master);
	}

	@Transactional
	public int addDrawTime(long group,long userNum,int num)
	{
		var master=masterDAO.findByGroupNumAndUserNum(group,userNum);
		if(master==null)
			throw new NoMasterExceptionRecordException(group,userNum);
		int i=master.getDrawTime()+num;
		master.setDrawTime(i);
		masterDAO.update(master);
		return i;
	}

	public void signToday(long groupNum,long userNum)
	{
		setSign(groupNum,userNum,getToday());
	}

	/**
	 * 获得今天的日期
	 * @return 储存在int中的格式为yyyyMMdd的日期
	 */
	public int getToday()
	{
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd");
		return Integer.parseInt(simpleDateFormat.format(System.currentTimeMillis()));
	}
}
