package com.nobot.plugin.girlFriend.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@Data
public class MyGroup
{
	@Id
	private long id;

	@OneToMany(cascade = CascadeType.ALL,mappedBy = "groupNum")
	private List<Girl> girlList;
}
