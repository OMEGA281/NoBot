package com.nobot.plugin.lor.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table
public class Game
{
	public interface State
	{int ACTIVE=1,SLEEP=2,END=0;}

	@Id
	private Long id;

	@Column(nullable = false)
	private Long initiator;

	@Column
	private Integer state;

	@OneToMany(cascade = CascadeType.ALL,mappedBy = "gameId")
	private List<Player> playerList;

	@OneToMany(cascade = CascadeType.ALL,mappedBy = "gameId")
	private List<Card> cardList;
}
