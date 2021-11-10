package com.nobot.plugin.botInfo.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@Data
public class GroupState
{
	@Id
	private long groupNum;

	@Column(nullable = false)
	private boolean state;

	@Column
	private String banUser;

	@OneToMany(cascade = CascadeType.ALL,mappedBy = "groupNum")
	private List<ExtraInfo> extra;

	public GroupState(long groupNum, boolean state)
	{
		this.groupNum=groupNum;
		this.state=state;
	}

	public GroupState()
	{
	}
}
