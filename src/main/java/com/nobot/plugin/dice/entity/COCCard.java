package com.nobot.plugin.dice.entity;

import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 这是一张角色卡
 */
@Entity
@Table
@Data
public class COCCard
{
	public enum BASE
	{
		司法科学(1), 医学(1), 动物学(1), 霰弹枪(25), 连枷(10), 说服(10), 绞索(15), 信用评级(0),
		电锯(10), 前行(20), 生存(10), 化学(1), 气象学(1), 操作重型机械(1), 攀爬(15), 跳跃(20),
		摄影(5), 历史(5), 图书馆使用(20), 聆听(20), 密码学(1), 精神分析(1), 锁匠(1), 骑术(5),
		考古学(1), 博物学(10), 人类学(1), 手枪(20), 物理(1), 机械维修(10), 计算机使用(5), 地质学(1),
		游泳(20), 生物学(1), 神秘学(5), 表演(5), 伪造(5), 动物驯养(5), 魅惑(15), 急救(30),
		药学(1), 电子学(1), 恐吓(15), 会计(5), 估价(5), 投掷(20), 炮术(1), 剑(20), 鞭子(5),
		潜水(1), 电气维修(10), 心理学(10), 妙手(10), 法律(5), 矛(20), 机关枪(10), 追踪(10),
		爆破(1), 斧头(15), 话术(5), 步枪(25), 美术(5), 斗殴(25), 植物学(1), 天文学(1), 侦察(25),
		冲锋枪(15), 领航(10), 弓术(15), 数学(1), 乔装(5), 火焰喷射器(10), 克苏鲁神话(0), 读唇(1),
		催眠(1), 汽车驾驶(20), 重武器(10);

		@Getter
		int value;

		BASE(int i)
		{
			value = i;
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@CreationTimestamp
	private Timestamp createdTime;

	@UpdateTimestamp
	private Timestamp updateTime;

	@Column
	private String name;

	/**
	 * 技能列表
	 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "card")
	private List<COCCardSkill> skillList;

	/**
	 * 属于的用户
	 */
	@Column
	private long user;

	/**
	 * 属于的群
	 */
	@Column
	private String groupNumList;

	private List<Long> getGroupNum()
	{
		List<Long> list=new ArrayList<>();
		String[] strings=groupNumList.split(";");
		for (String s:strings)
		{
			try
			{
				list.add(Long.parseLong(s));
			}
			catch (NumberFormatException e)
			{
				continue;
			}
		}
		return list;
	}

	private void saveGroupNum(List<Long> list)
	{
		StringBuilder stringBuilder=new StringBuilder();
		for (long l:list)
			stringBuilder.append(l+";");
		groupNumList=stringBuilder.toString();
	}

	public void addGroup(long group)
	{
		List<Long> list=getGroupNum();
		if(!list.contains(group))
			list.add(group);
		saveGroupNum(list);
	}

	public void removeGroup(long group)
	{
		List<Long> list=getGroupNum();
		list.remove(group);
		saveGroupNum(list);
	}

	public void changeGroup(long before,long after)
	{
		List<Long> list=getGroupNum();
		list.remove(before);
		if(!list.contains(after))
			list.add(after);
		saveGroupNum(list);
	}

	public Timestamp getUpdateTime()
	{
		if (updateTime == null)
			return createdTime;
		return updateTime;
	}

	/**
	 * 获取技能值
	 * @param skillName 技能名称
	 * @return 技能值 不存在则返回0
	 */
	public int getSkill(String skillName)
	{
		for (COCCardSkill num : skillList)
			if (num.getSkillName().getSkillName().equals(skillName))
				return num.getPoint();
		return 0;
	}

	public void setSkillList(List<COCCardSkill> list)
	{
		for(COCCardSkill skill:list)
			skill.setCard(this);
		this.skillList=list;
	}

	public void setSkill(COCCardSkillName skillName, int point)
	{
		if (skillList == null)
			skillList = new ArrayList<COCCardSkill>();
		for (COCCardSkill num : skillList)
			if (num.getSkillName().equals(skillName))
			{
				num.setPoint(point);
				return;
			}
		COCCardSkill cocCardSkill = new COCCardSkill();
		cocCardSkill.setCard(this);
		cocCardSkill.setSkillName(skillName);
		cocCardSkill.setPoint(point);
		skillList.add(cocCardSkill);
	}

	public void deleteSkill(String skillName)
	{
		for (COCCardSkill num : skillList)
			if (num.getSkillName().getSkillName().equals(skillName))
			{
				num.setCard(null);
				skillList.remove(num);
				return;
			}
		return;
	}

}
