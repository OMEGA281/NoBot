package com.nobot.plugin.systemController.entity;

import lombok.Data;

import javax.persistence.*;

@Table
@Data
@Entity
public class GroupSleepMode
{
	@Id
	public long id;

	@Column(nullable = false)
	public boolean isActive;
}
