package com.nobot.plugin.lor.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Table
@Entity
public class Card
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "Game_id")
//	@Column
	private Game gameId;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "Player_id")
//	@Column
	private Player playerId;

	@Column
	private String title;

	@Column
	private Boolean hasGet;
}
