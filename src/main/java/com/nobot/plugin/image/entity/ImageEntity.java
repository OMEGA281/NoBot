package com.nobot.plugin.image.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Table
@Entity
@Data
public class ImageEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private String name;

	@OneToMany(cascade = CascadeType.ALL,mappedBy = "image",fetch = FetchType.EAGER)
	private List<TagEntity> tags;
}
