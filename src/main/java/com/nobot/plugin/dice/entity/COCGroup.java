package com.nobot.plugin.dice.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Table
@Entity
@Data
public class COCGroup
{
	@Id
	private long id;

	@ManyToMany(cascade = CascadeType.ALL,mappedBy = "groupList",fetch = FetchType.EAGER)
	private List<COCCard> cardList;
}
