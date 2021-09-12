package com.nobot.plugin.girlFriend.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table
@Data
public class Girl
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private String name;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "Master_id")
	private Master master;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "MyGroup_id")
	private MyGroup groupNum;

	@Column
	private int saleNum;
}
