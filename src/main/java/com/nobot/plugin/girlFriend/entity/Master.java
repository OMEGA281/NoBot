package com.nobot.plugin.girlFriend.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@Data
public class Master
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private long userNum;

	@Column
	private long groupNum;

	@Column
	private int gold;

	@OneToMany(cascade = CascadeType.ALL,mappedBy = "master")
	private List<Girl> girlList;

	@Column
	private int active;

	@Column
	private int lastSignTime;

	@Column
	private int creatTime;

	@Column
	private int decomposeTime;

	@Column
	private int buyTime;

	@Column
	private int saleTime;

	@Column
	private int workSumTime;

	@Column
	private int drawTime;

	public void setActive(int active)
	{
		if(active>100)
			active=100;
		if(active<0)
			active=0;
		this.active=active;
	}
}
