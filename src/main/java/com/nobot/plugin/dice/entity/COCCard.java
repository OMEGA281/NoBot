package com.nobot.plugin.dice.entity;

import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * 这是一张角色卡
 */
@Entity
@Table
@Data
public class COCCard
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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
	@ManyToMany(cascade = CascadeType.ALL,mappedBy = "cardList")
	@JoinTable(name = "GROUP_TO_CARD",joinColumns = {@JoinColumn(name="COCGROUP_ID")},
			inverseJoinColumns = {@JoinColumn(name = "COCCARD_ID")})
	private List<COCGroup> groupList;

	public Timestamp getUpdateTime()
	{
		if (updateTime == null)
			return createdTime;
		return updateTime;
	}
}
