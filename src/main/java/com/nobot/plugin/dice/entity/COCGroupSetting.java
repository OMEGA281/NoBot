package com.nobot.plugin.dice.entity;

import lombok.Cleanup;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
@Data
public class COCGroupSetting
{
	@Id
	private long id;

	@Column
	private int successSetting=-1;

	@Column
	private int failSetting=-1;
}
