package com.nobot.plugin.dice.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 储存每一个技能的数值
 */
@Table
@Entity
@Data
public class COCCardSkill
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "COCCard_id",nullable = false)
	private COCCard card;

	@Column
	private String name;

	@Column
	private Integer point;
}
