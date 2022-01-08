package com.nobot.plugin.image.entity;

import lombok.Data;

import javax.persistence.*;

@Table
@Entity
@Data
public class TagEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private String name;

	@ManyToOne
	@JoinColumn(name = "ImageEntity_id")
	private ImageEntity image;
}
