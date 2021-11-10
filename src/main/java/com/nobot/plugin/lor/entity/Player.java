package com.nobot.plugin.lor.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Table
@Entity
public class Player
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "Game_id")
//	@Column
	private Game gameId;

	@Column
	private Long userId;

	@OneToMany(cascade = CascadeType.ALL,mappedBy = "playerId")
	private List<Card> cardList;

	@Column
	private Integer team;

	@Column
	private Integer light;

	@Column
	private Integer hp;

	@Column
	private Integer defense;
}
