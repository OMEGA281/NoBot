package com.nobot.plugin.botInfo.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table
@Data
public class ExtraInfo
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "GroupState_id",nullable = false)
	private GroupState groupNum;

	@Column
	private String key;

	@Column
	private String value;
}
